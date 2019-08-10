package com.rishav.quizearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Singup extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore database;
    EditText emailBox,passwordBox,nameBox,codeBox;
    Button AlrdyAcnt,singupButton;
    ImageButton gsingup;
    ProgressDialog dialog,verifyemail;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN=0;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        auth=FirebaseAuth.getInstance();
        database=FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(this);
        dialog.setTitle("Signing");
        dialog.setMessage("We are creating new account");
        dialog.setCanceledOnTouchOutside(false);

        verifyemail = new ProgressDialog(this);
        verifyemail.setTitle("Verify Email");
        verifyemail.setTitle("Verifing email is send to your Email id....go and click the link to verify your email");
        verifyemail.setCanceledOnTouchOutside(false);
        verifyemail.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        nameBox=findViewById(R.id.FullName);
        emailBox=findViewById(R.id.Emailtxt);
        passwordBox=findViewById(R.id.Passwordtxt);
        codeBox=findViewById(R.id.code);
        singupButton=findViewById(R.id.Singupbtn);
        AlrdyAcnt=findViewById(R.id.CrtAcnt);
        gsingup=findViewById(R.id.gsingin);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);


        singupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email,pass,name,code;
                email=emailBox.getText().toString();
                pass=passwordBox.getText().toString();
                name=nameBox.getText().toString();
                code=codeBox.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(name) || TextUtils.isEmpty(code)){
                    emailBox.setError("Fields cannot be empty");
                    passwordBox.setError("Fields cannot be empty");
                    nameBox.setError("Fields cannot be empty");
                    codeBox.setError("Fields cannot be empty");
                }else {
                    dialog.show();
                    final Users user =new Users();
                    user.setName(name);
                    user.setEmail(email);
                    user.setPass(pass);
                    user.setCode(code);

                    auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                String uid =task.getResult().getUser().getUid();
                                database.collection("Users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                startActivity(new Intent(Singup.this,Dashboard.class));
                                                finish();
                                            }
                                        });
                            }else {
                                dialog.dismiss();
                                Toast.makeText(Singup.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        gsingup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        AlrdyAcnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Singup.this,Login.class));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                dialog.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                           // FirebaseUser user = auth.getCurrentUser();
                            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Singup.this);
                            if (acct != null) {
                                String personName = acct.getDisplayName();
                                //String personGivenName = acct.getGivenName();
                                //String personFamilyName = acct.getFamilyName();
                                String personEmail = acct.getEmail();
                               // String personId = acct.getId();
                                //final Uri personPhoto = acct.getPhotoUrl();
                                final Users user =new Users();
                                user.setName(personName);
                                user.setEmail(personEmail);
                                user.setPass("googlesingup");
                                user.setCode("gsingup");
                                String uid =task.getResult().getUser().getUid();
                                database.collection("Users")
                                        .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                                startActivity(new Intent(Singup.this,Dashboard.class));
                                                finish();
                                                Toast.makeText(Singup.this, "SingnedUp", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                           // updateUI(user);
                        } else {
                            Toast.makeText(Singup.this, "Sorry Could not Signed up", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}