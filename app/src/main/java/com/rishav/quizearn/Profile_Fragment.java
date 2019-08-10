package com.rishav.quizearn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rishav.quizearn.databinding.FragmentProfileBinding;

import java.net.URI;

import static android.app.Activity.RESULT_OK;

public class Profile_Fragment extends Fragment {


    public Profile_Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentProfileBinding binding;
    FirebaseFirestore database;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Users user;
    boolean emailVerified=false;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    CircularImageView profilepic;
    TextView name,emailid;
    //ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfileBinding.inflate(inflater, container, false);
        binding.progressBar5.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.nav_header,null);
        profilepic=view.findViewById(R.id.profilepic);
        name=view.findViewById(R.id.name);
        emailid=view.findViewById(R.id.emailid);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user1 = auth.getCurrentUser();

        if(user1.isEmailVerified()){
            emailVerified=true;
        }else {
            emailVerified=false;
        }

       binding.addpic.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               choosePicture();
           }
       });
        updateprofilepic();

       binding.updatebtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String newName = binding.updateFullname.getText().toString() ;
               final String newEmail =binding.updateEmailtxt.getText().toString();
               if(TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)){
                   binding.updateEmailtxt.setError("Fields cannot be empty");
                   binding.updateFullname.setError("Fields cannot be empty");
               }else{
                   binding.updateEmailtxt.setError(null);
                   binding.updateFullname.setError(null);
                   FirebaseAuth auth = FirebaseAuth.getInstance();
                   final FirebaseUser user1 = auth.getCurrentUser();
                   if(emailVerified==true){
                       Toast.makeText(getContext(), "email verified...updating", Toast.LENGTH_SHORT).show();
                       user1.updateEmail(newEmail)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           database.collection("Users")
                                                   .document(FirebaseAuth.getInstance().getUid())
                                                   .update("name",newName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   database.collection("Users")
                                                           .document(FirebaseAuth.getInstance().getUid())
                                                           .update("email",newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                       @Override
                                                       public void onSuccess(Void aVoid) {
                                                           Toast.makeText(getContext(), "New Details Updated.", Toast.LENGTH_SHORT).show();
                                                           binding.updateEmailtxt.setText("");
                                                           binding.updateFullname.setText("");
                                                           updateprofilepic();
                                                       }
                                                   }).addOnFailureListener(new OnFailureListener() {
                                                       @Override
                                                       public void onFailure(@NonNull Exception e) {
                                                           binding.updateEmailtxt.setText("");
                                                           binding.updateFullname.setText("");
                                                           Toast.makeText(getContext(), "Email is not updated please try again"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                       }
                                                   });
                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                               @Override
                                               public void onFailure(@NonNull Exception e) {
                                                   binding.updateEmailtxt.setText("");
                                                   binding.updateFullname.setText("");
                                                   Toast.makeText(getContext(), "Name is not updated please try again"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                               }
                                           });
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               binding.updateEmailtxt.setText("");
                               binding.updateFullname.setText("");
                               Toast.makeText(getContext(), "Email is not updated.."+e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                   else {
                       binding.updateEmailtxt.setText("");
                       binding.updateFullname.setText("");
                       Toast.makeText(getContext(), "Your email is not verified verify your email ...", Toast.LENGTH_SHORT).show();
                      // FirebaseAuth auth = FirebaseAuth.getInstance();
                       //final FirebaseUser user = auth.getCurrentUser();
                       user1.sendEmailVerification()
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           Toast.makeText(getContext(), "Verification link is sent to your older Email id verify to add new one...and login in", Toast.LENGTH_SHORT).show();
                                           FirebaseAuth.getInstance().signOut();
                                           startActivity(new Intent(getContext(),Login.class));
                                       }
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                           }
                       });
                   }

               }
           }
       });

       binding.delete1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final ProgressDialog pd = new ProgressDialog(getContext());
               pd.setMessage("Deleting your account");
               pd.setCanceledOnTouchOutside(false);
               pd.show();
               final AlertDialog.Builder logoutDialog = new AlertDialog.Builder(v.getContext());
               logoutDialog.setTitle("Delete Account");
               logoutDialog.setMessage("Are you sure that you want to delete your Account permanently!!!!!!.. We don't Recommend you to delete your account ..All your coins will be deleted..and you will be unable to withdraw money!!!");
               logoutDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       database.collection("Users")
                               .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                               .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               StorageReference riversRef = storageReference.child("images/"+ FirebaseAuth.getInstance().getUid());
                               riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                   }
                               }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       Toast.makeText(getContext(), "Account not Image deleted"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });
                               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                               user.delete()
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()) {
                                                   pd.dismiss();
                                                   Toast.makeText(getContext(), "Your Account is deleted.", Toast.LENGTH_SHORT).show();
                                                   startActivity(new Intent(getContext(),Singup.class));
                                                   //Log.d(TAG, "User account deleted.");
                                               }
                                           }
                                       }).addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       pd.dismiss();
                                       Toast.makeText(getContext(), "Account  not deleted"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                   }
                               });

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               pd.dismiss();
                               Toast.makeText(getContext(), "Account not data deleated"+e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });

                   }
               });
               logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       pd.dismiss();
                   }
               });

               logoutDialog.create().show();
           }
       });




        return binding.getRoot();
    }

    private void updateprofilepic() {
        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (getActivity() == null) {
                    return;
                }
                user = documentSnapshot.toObject(Users.class);
                Glide.with(getContext())
                        .load(user.getProfile())
                        .into(binding.profilepic);
                binding.userName.setText(String.valueOf(user.getName()));
                binding.userEmailid.setText(String.valueOf(user.getEmail()));
                binding.progressBar5.setVisibility(View.GONE);

                //binding.profilepic.setImageURI(imageUri);
            }
        });

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (getActivity() == null) {
                    return;
                }
                user = documentSnapshot.toObject(Users.class);
                Glide.with(getContext())
                        .load(user.getProfile())
                        .into(profilepic);
                name.setText(String.valueOf(user.getName()));
                emailid.setText(String.valueOf(user.getEmail()));
                //binding.progressBar5.setVisibility(View.GONE);
                //binding.profilepic.setImageURI(imageUri);
            }
        });
    }


    private void choosePicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            binding.progressBar5.setVisibility(View.VISIBLE);
            //binding.profilepic.setImageURI(imageUri);
            uploadpic();
        }
    }

    private void uploadpic(){

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setTitle("Uploading Image..");
        pd.show();

        final StorageReference riversRef = storageReference.child("images/"+ FirebaseAuth.getInstance().getUid());

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                       riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                               database.collection("Users")
                                       .document(FirebaseAuth.getInstance().getUid())
                                       .update("profile", uri.toString() ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       pd.dismiss();
                                       binding.progressBar5.setVisibility(View.GONE);
                                       binding.profilepic.setImageURI(imageUri);
                                       Toast.makeText(getContext(), "Image Uploaded.", Toast.LENGTH_SHORT).show();
                                       updateprofilepic();
                                   }
                               });
                           }
                       });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        pd.dismiss();
                        binding.progressBar5.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Fail to upload.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percent done : "+(int)progressPercent+"%");
                pd.setCanceledOnTouchOutside(false);
                binding.progressBar5.setVisibility(View.VISIBLE);

            }
        });
    }


}