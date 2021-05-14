package com.projectocean.safar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyLicenceActivity extends AppCompatActivity {
    Button submit;
    EditText number;
    FirebaseAuth mAuth;
    DatabaseReference mUser;
    LinearLayout licence;
    boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_licence);
        Intent i=getIntent();
        stateCheck();

        number=(EditText)findViewById(R.id.licence_number);
        submit=findViewById(R.id.submit_licence);
        licence=findViewById(R.id.licence_layout);

        mAuth=FirebaseAuth.getInstance();
        mUser=FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid() );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(flag==false){
                    if (dataSnapshot.child("licenceNo").exists()) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyLicenceActivity.this);
                        builder1.setMessage("Already Added");
                        builder1.setCancelable(false);

                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });

                        AlertDialog alert = builder1.create();
                        hideKeyboard(VerifyLicenceActivity.this);
                        alert.show();
                        flag=true;
                    }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stateCheck()&& !number.getText().toString().isEmpty()) {
                    mUser.child("licenceNo").setValue(number.getText().toString());
                    Toast.makeText(VerifyLicenceActivity.this, "You Have successfully Added Licence Number", Toast.LENGTH_SHORT).show();
                    flag = true;
                    finish();
                }
            }
        });

    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            boolean b = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        return connected;
    }

    private boolean stateCheck(){
        if (!isNetworkAvailable()){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyLicenceActivity.this);
            builder1.setMessage("No internet Connection");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(VerifyLicenceActivity.this, VerifyLicenceActivity.class));
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    "Exit",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder1.create();
            alert.show();
        }
        return isNetworkAvailable();
    }

}
