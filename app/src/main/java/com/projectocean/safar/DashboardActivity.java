package com.projectocean.safar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    TextView headerName, headerEmail, headerBalance;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase db;
    DatabaseReference mRef;
    Integer loadBalance;
    ImageView walletimg;
    CardView cardView;
    ImageView img1, img2, offer3;
    boolean val = false;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer);

        View headerView = navigationView.getHeaderView(0);

        walletimg = (ImageView) headerView.findViewById(R.id.walletimg);
        headerName = (TextView) headerView.findViewById(R.id.header_name);
        headerEmail = (TextView) headerView.findViewById(R.id.header_email);
        headerBalance = (TextView) headerView.findViewById(R.id.tv_balance);
        img1 = findViewById(R.id.offer1);
        img2 = findViewById(R.id.offer2);
        offer3 = findViewById(R.id.offer3);
        cardView = findViewById(R.id.cardview);

        String id = mAuth.getUid();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, OfferActivity.class);
                i.putExtra("offername", "o1");
                startActivity(i);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, OfferActivity.class);
                i.putExtra("offername", "o2");
                startActivity(i);
            }
        });

        offer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, OfferActivity.class);
                i.putExtra("offername", "o3");
                startActivity(i);
            }
        });

        mRef = db.getReference("Users");

        //set name to header
        if (firebaseUser != null) {
            String name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            headerName.setText(name);
            headerEmail.setText(email);
        }

        //set balance to header
        DatabaseReference balance = mRef.child(id).child("wallet");
        balance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadBalance = dataSnapshot.getValue(Integer.class);
                    headerBalance.setText(getString(R.string.currency) + " " + loadBalance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        walletimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
                closeDrawer();
            }
        });

        //to set original color
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerArrowDrawable(new HamburgerDrawable(this));
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, ShowCarsActivity.class));
            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() != R.id.logout)
            closeDrawer();

        switch (item.getItemId()) {
            case R.id.my_trips:
                startActivity(new Intent(DashboardActivity.this, MyTripsActivity.class));
                break;
            case R.id.verify_licence:
                startActivity(new Intent(DashboardActivity.this, VerifyLicenceActivity.class));
                break;

            case R.id.saved_cards:
                startActivity(new Intent(DashboardActivity.this, CardsActivity.class));
                break;

            case R.id.wallet:
                startActivity(new Intent(DashboardActivity.this, WalletActivity.class));
                break;

            case R.id.policies:
                startActivity(new Intent(DashboardActivity.this, PoliciesActivity.class));
                break;
            case R.id.help_and_support:
                startActivity(new Intent(DashboardActivity.this, HelpAndSuppportActivity.class));
                break;
            case R.id.logout:

                AlertDialog.Builder builder1 = new AlertDialog.Builder(DashboardActivity.this);
                builder1.setMessage("Click Yes To Logout");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                mGoogleSignInClient.signOut();

                                // mGoogleSignInClient.signOut();
                                Toast.makeText(DashboardActivity.this, "Successfully Signed Out", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                                finish();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder1.create();
                alert.show();
                break;
        }

        return true;
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawer();

        } else {
            if (!val) {
                Toast.makeText(DashboardActivity.this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
                val = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        val = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
