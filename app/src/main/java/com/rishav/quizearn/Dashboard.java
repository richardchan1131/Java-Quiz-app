package com.rishav.quizearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.rishav.quizearn.databinding.ActivityDashboardBinding;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    ActivityDashboardBinding binding;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout mainView;
    static final float END_SCALE = 0.7f;

    Users user;
    FirebaseFirestore database;
    CircularImageView profilepic;
    TextView name,emailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;
        View headerView=navigationView.getHeaderView(0);
        profilepic=headerView.findViewById(R.id.profilepic);
        name=headerView.findViewById(R.id.name);
        emailid=headerView.findViewById(R.id.emailid);
        mainView=binding.mainview;
        database=FirebaseFirestore.getInstance();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        final AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                binding.adView.loadAd(adRequest);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new HomeFragment());
        transaction.commit();


        //navigation
        navigationDrawer();
        setnavthings();

       binding.bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
           @Override
           public boolean onItemSelect(int i) {
               FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
               switch (i){
                   case 0:
                       binding.navigationView.setCheckedItem(R.id.nav_home);
                       transaction.replace(R.id.content, new HomeFragment());
                       transaction.commit();
                       break;
                   case 1:
                       binding.navigationView.setCheckedItem(R.id.nav_rank);
                       transaction.replace(R.id.content, new Leaderboard_Fragment());
                       transaction.commit();
                       break;
                   case 2:
                       binding.navigationView.setCheckedItem(R.id.nav_wallet);
                       transaction.replace(R.id.content, new WalletFragment());
                       transaction.commit();
                       break;
                   case 3:
                       binding.navigationView.setCheckedItem(R.id.nav_profile);
                       transaction.replace(R.id.content, new Profile_Fragment());
                       transaction.commit();
                       break;
               }
               return false;
           }
       });



       binding.logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                AlertDialog.Builder logoutDialog = new AlertDialog.Builder(v.getContext());
                logoutDialog.setTitle("Logout");
                logoutDialog.setMessage("If you want to Logout then click ok.");


                logoutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Dashboard.this,Login.class));
                    }
                });
                logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                logoutDialog.create().show();

            }
        });



    }

    private void setnavthings() {

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (getApplicationContext() == null) {
                    return;
                }
                user = documentSnapshot.toObject(Users.class);
                Glide.with(getApplicationContext())
                        .load(user.getProfile())
                        .into(profilepic);
                name.setText(String.valueOf(user.getName()));
                emailid.setText(String.valueOf(user.getEmail()));
                //binding.progressBar5.setVisibility(View.GONE);
                //binding.profilepic.setImageURI(imageUri);
            }
        });
    }


    //navigationDrawer
    public void onBackPressed(){

        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
            setnavthings();
        }
        else{
            super.onBackPressed();
        }
    }
    private void navigationDrawer() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        binding.drawerMenue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)){
                    setnavthings();
                    drawerLayout.closeDrawer(GravityCompat.START);
                }else {
                    setnavthings();
                    drawerLayout.openDrawer(GravityCompat.START);
                }

            }
        });

       // animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                final float diffScaleOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaleOffset;
                mainView.setScaleX(offsetScale);
                mainView.setScaleY(offsetScale);

                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = mainView.getMaxWidth() * diffScaleOffset / 2;
                final float xTransition = xOffset - xOffsetDiff;
                mainView.setTranslationX(xTransition);

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id==R.id.nav_home){
            binding.bottomBar.setItemActiveIndex(0);
            transaction.replace(R.id.content, new HomeFragment());
            transaction.commit();
        } else if (id==R.id.nav_rank){
            binding.bottomBar.setItemActiveIndex(1);
            transaction.replace(R.id.content, new Leaderboard_Fragment());
            transaction.commit();
        } else if (id==R.id.nav_wallet){
            binding.bottomBar.setItemActiveIndex(2);
            transaction.replace(R.id.content, new WalletFragment());
            transaction.commit();
        }
        else if (id==R.id.nav_profile){
            binding.bottomBar.setItemActiveIndex(3);
            transaction.replace(R.id.content, new Profile_Fragment());
            transaction.commit();
        }
        else if (id==R.id.nav_logout){
            AlertDialog.Builder logoutDialog = new AlertDialog.Builder(Dashboard.this);
            logoutDialog.setTitle("Logout");
            logoutDialog.setMessage("If you want to Logout then click ok.");


            logoutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Dashboard.this,Login.class));
                }
            });
            logoutDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            logoutDialog.create().show();
        }
        else if (id==R.id.nav_settings){
            startActivity(new Intent(Dashboard.this,Settings.class));
        }
        else if (id==R.id.nav_share){
            try{
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Quiz Earn");
                intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+Dashboard.this.getPackageName());
                startActivity(Intent.createChooser(intent,"Share With"));
            }catch (Exception e){
                Toast.makeText(Dashboard.this, "Unable to share at this moment.."+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (id==R.id.nav_aboutUs){
            startActivity(new Intent(Dashboard.this,AboutUs.class));
        } else if (id==R.id.nav_RateUs){
            try{
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}