package com.projectocean.safar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;

public class AddMoneyActivity extends AppCompatActivity {
    Spinner spinner;
    FirebaseAuth mAuth;
    FirebaseDatabase mData;
    DatabaseReference mRef, balRef, balRef2;
    EditText atmNo, amt;
    Button add;
    Integer balInInt, addBal;
    String type = "regularCall";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        Intent i = getIntent();

        if (i.hasExtra("type")) {
            type = i.getStringExtra("type");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mData = FirebaseDatabase.getInstance();


        spinner = (Spinner) findViewById(R.id.spinner);
        atmNo = findViewById(R.id.atmNo);
        amt = (EditText) findViewById(R.id.amt);
        add = (Button) findViewById(R.id.button2);

        final ArrayList<String> bankList = new ArrayList<>();

        mRef = mData.getReference("Banks");
        balRef = mData.getReference("Users");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bankList);

        spinner.setAdapter(adapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String currentBank;

                bankList.clear();

                for (DataSnapshot childData : dataSnapshot.getChildren()) {

                    currentBank = childData.getValue(String.class);
                    bankList.add(currentBank);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        balRef2 = balRef.child(mAuth.getCurrentUser().getUid()).child("wallet");
        balRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                balInInt = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    String sAmt = amt.getText().toString();

                    if (TextUtils.isEmpty(sAmt)) {
                        Toast.makeText(AddMoneyActivity.this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                    } else if (atmNo.getText().toString().length() != 6) {
                        Toast.makeText(AddMoneyActivity.this, "Please Enter Valid digits", Toast.LENGTH_SHORT).show();

                    } else {

                        addBal = Integer.parseInt(sAmt);
                        balInInt = addBal + balInInt;
                        balRef.child(mAuth.getCurrentUser().getUid()).child("wallet").setValue(balInInt);
                        Toast.makeText(AddMoneyActivity.this, "Added " + addBal + " successfully", Toast.LENGTH_SHORT).show();
                        Intent rIntent = new Intent();
                        rIntent.putExtra("balance", balInInt);
                        setResult(RESULT_OK, rIntent);
                        finish();
                    }
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AddMoneyActivity.this);
                    builder1.setMessage("No Internet Connection");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Retry",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    startActivity(new Intent(AddMoneyActivity.this, AddMoneyActivity.class));
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
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;

        return connected;
    }

}
