package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.projectocean.safar.models.User;

public class RegisterAdminActivity extends AppCompatActivity {

     EditText name,phone,email,password;
     Button button;
     FirebaseAuth mAuth;
     FirebaseDatabase mdata;
     DatabaseReference mRef;
     ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }


        setContentView(R.layout.activity_register);

        //((RelativeLayout) findViewById(R.id.container)).setPadding(0,0,0,getSoftButtonsBarSizePort(this));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i=getIntent();

        mAuth = FirebaseAuth.getInstance();
        mdata = FirebaseDatabase.getInstance();

        name=(EditText)findViewById(R.id.name);
        phone=(EditText) findViewById(R.id.number);
        email=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
        button=(Button) findViewById(R.id.btn_submit);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    void createAccount(){
        final String sEmail= email.getText().toString();
        final String sPass = password.getText().toString();
        final String sName=  name.getText().toString();
        final String sPhone=  phone.getText().toString();

        pd = new ProgressDialog(RegisterAdminActivity.this);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        if (TextUtils.isEmpty(sEmail) || TextUtils.isEmpty(sPass)|| TextUtils.isEmpty(sName)|| TextUtils.isEmpty(sPhone)) {
            Toast.makeText(this, "Field are empty", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
        else {

            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            mAuth.createUserWithEmailAndPassword(sEmail, sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterAdminActivity.this, "Register problem", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                    else {
                        User user=new User(name.getText().toString(),email.getText().toString(),phone.getText().toString(),false,"null",0,0);

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString())
                                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                                .build();

                        mAuth.getCurrentUser().updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                           Toast.makeText(RegisterAdminActivity.this, "User profile updated.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                        mRef= mdata.getReference("Admin").child(mAuth.getCurrentUser().getUid());

                        mRef.setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RegisterAdminActivity.this, "Register Successful", Toast.LENGTH_LONG).show();

                            }
                        });

                        mAuth.signInWithEmailAndPassword(sEmail,sPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                startActivity(new Intent(RegisterAdminActivity.this, DashboardActivity.class));
                                finish();
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
        startActivity(new Intent(RegisterAdminActivity.this,AdminLoginActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

