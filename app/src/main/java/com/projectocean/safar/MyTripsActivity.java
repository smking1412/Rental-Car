package com.projectocean.safar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.Trip;
import com.projectocean.safar.viewHolders.RecentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MyTripsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mCar, mUser, mDeposit;
    String carKey;

    Toolbar toolbar;
    String tId;
    private RecyclerView mRecentlist;
    private DatabaseReference mTrips;
    private FirebaseDatabase db;
    Query firebaseSearchQuery;
    Integer wallet;
    Integer deposit;
    TextView noTrips;
    LinearLayout recentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseDatabase.getInstance();

        mRecentlist = findViewById(R.id.myrecentrecycleview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.scrollToPositionWithOffset(0, 0);
        mRecentlist.setLayoutManager(mLayoutManager);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recentView = findViewById(R.id.recent);

        noTrips = findViewById(R.id.no_trips);

        mAuth = FirebaseAuth.getInstance();
        mTrips = FirebaseDatabase.getInstance().getReference("Trips").child(mAuth.getUid());

        mUser = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        mDeposit = FirebaseDatabase.getInstance().getReference("RateDeposit");
        mCar = FirebaseDatabase.getInstance().getReference("Cars");


        mDeposit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deposit = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wallet = dataSnapshot.child("wallet").getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mTrips.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    recentView.setVisibility(View.GONE);
                    mRecentlist.setVisibility(View.GONE);
                    noTrips.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseSearchQuery = mTrips;
        FirebaseRecyclerAdapter<Trip, RecentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, RecentViewHolder>(
                Trip.class,
                R.layout.recent_row,
                RecentViewHolder.class,
                firebaseSearchQuery
        ) {


            @Override
            protected void populateViewHolder(final RecentViewHolder viewHolder, final Trip obj, int position) {
                tId = obj.getTripId();
                carKey = obj.getCarId();

                Log.d("debugCar", obj.getStatus() + " is status for key " + carKey + " and id is " + tId);

                viewHolder.setDetails(obj.getCarId(), obj.getDate(), obj.getTime(), obj.getPul(),
                        obj.getRent(), obj.getNumberPlate(), obj.getHours(), getApplicationContext(), obj.getStatus());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("showingclick", obj.getTripId() + " " + obj.getCarId());
                    }
                });


                viewHolder.endTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCar.child(obj.getCarId()).child("requests").removeValue();
                        mUser.child("isRenting").setValue(false);
                        mUser.child("wallet").setValue(wallet + deposit);
                        Toast.makeText(MyTripsActivity.this, "Deposit added back to account", Toast.LENGTH_SHORT).show();
                        mCar.child(obj.getCarId()).child("availability").setValue("AVAILABLE");
                        mTrips.child(obj.getTripId()).child("status").setValue("Trip Ended");

                        db.getReference("AllTrips").child(obj.getTripId()).child("status").setValue("Trip Ended");
                    }
                });
            }
        };


        mRecentlist.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(MyTripsActivity.this, DashboardActivity.class));
    }
}