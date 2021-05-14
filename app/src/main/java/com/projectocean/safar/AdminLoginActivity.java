package com.projectocean.safar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projectocean.safar.sql.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {
        EditText etEmail,etPassword;
        Button adminLogin;
        Button adminRegister;
        FirebaseAuth mAuth;
    private ProgressDialog pd;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }



        setContentView(R.layout.activity_admin_login);

       // ((RelativeLayout) findViewById(R.id.container)).setPadding(0,0,0,getSoftButtonsBarSizePort(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);



        databaseHelper = new DatabaseHelper(this);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pd = new ProgressDialog(this);
        pd.setMessage("Logging you in...");
        pd.setCancelable(false);

        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        adminLogin = findViewById(R.id.admin_login);
        adminRegister = findViewById(R.id.admin_register);

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                startSignIn(etEmail.getText().toString(),etPassword.getText().toString());
            }
        });

        adminRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminLoginActivity.this, RegisterAdminActivity.class));
                finish();
            }
        });
    }

    public static int getSoftButtonsBarSizePort(Activity activity) {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }


    private void startSignIn(String email, String pass) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else {

            pd.show();
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(AdminLoginActivity.this, "Login problem", Toast.LENGTH_LONG).show();
                        pd.dismiss();

                    }
                    else{

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admin").child(mAuth.getUid());

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                pd.dismiss();

                                if (dataSnapshot.exists()){
                                    startActivity(new Intent(AdminLoginActivity.this,AdminDashboardActivity.class));
                                    finish();
                                }
                                else {
                                    mAuth.signOut();
                                    Toast.makeText(AdminLoginActivity.this, "Non admin account detected... ", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }

                    // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
            });
        }


    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminLoginActivity.this,LoginActivity.class));
        finish();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
