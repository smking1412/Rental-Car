package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WalletActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase mData;
    DatabaseReference mRef;
    String loadName, loadEmail, loadPhone;
    Integer loadBalance;
    TextView Name, Email, Balance, Phone;
    Button btnAddMoney;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent i = getIntent();

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();
        mRef = mData.getReference("Users");

        Name = (TextView) findViewById(R.id.w_name);
        Email = (TextView) findViewById(R.id.w_email);
        Balance = (TextView) findViewById(R.id.w_balance);
        Phone = (TextView) findViewById(R.id.w_phone);
        btnAddMoney = (Button) findViewById(R.id.button);

        String id = mAuth.getCurrentUser().getUid();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
//set Email to wallet

        mRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("name").exists()) {
                        Name.setText(dataSnapshot.child("name").getValue(String.class));
                        Name.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.child("email").exists()) {
                        Email.setText(dataSnapshot.child("email").getValue(String.class));
                        Email.setVisibility(View.VISIBLE);
                    }


                    if (dataSnapshot.child("phone").exists()) {
                        Phone.setText(dataSnapshot.child("phone").getValue(String.class));
                        Phone.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.child("wallet").exists()) {
                        Balance.setText(getString(R.string.currency) + " " + dataSnapshot.child("wallet").getValue(Long.class));
                        Balance.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference name = mRef.child(id).child("name");
        name.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadName = dataSnapshot.getValue().toString();
                Name.setText(loadName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //set Email to wallet
        DatabaseReference email = mRef.child(id).child("email");
        email.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadEmail = dataSnapshot.getValue().toString();
                Email.setText(loadEmail);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set balance to walllet
        DatabaseReference balance = mRef.child(id).child("wallet");
        balance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadBalance = dataSnapshot.getValue(Integer.class);
                Balance.setText(getString(R.string.currency) + " " + String.valueOf(loadBalance));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //set phone to walllet
        DatabaseReference phone = mRef.child(id).child("phone");
        phone.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    loadPhone = dataSnapshot.getValue().toString();
                    Phone.setText(loadPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WalletActivity.this, AddMoneyActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
