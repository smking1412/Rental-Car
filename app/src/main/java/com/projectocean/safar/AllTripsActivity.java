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


public class AllTripsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mCar, mUser, mDeposit;
    String carKey;

    Toolbar toolbar;
    String tId;
    private RecyclerView mRecentlist;
    private DatabaseReference mTrips;
    Query firebaseSearchQuery;
    Integer wallet, totalTrips;
    Integer deposit;
    TextView noTrips;
    LinearLayout recentView;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_trips);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();

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
        mTrips = FirebaseDatabase.getInstance().getReference("AllTrips");

        mDeposit = FirebaseDatabase.getInstance().getReference("RateDeposit");
        mUser = db.getReference("Users").child(mAuth.getUid());
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


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Trip, RecentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Trip, RecentViewHolder>(
                Trip.class,
                R.layout.recent_row,
                RecentViewHolder.class,
                mTrips
        ) {


            @Override
            protected void populateViewHolder(final RecentViewHolder viewHolder, final Trip obj, int position) {
                tId = obj.getTripId();//obj.gettId();
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

//               viewHolder.pul.setOnClickListener(new View.OnClickListener() {
//                   @Override
//                   public void onClick(View view) {
//                        String l=obj.getPul()+" BaseStation";
//                       String geoUri = "http://maps.google.com/maps?q=loc:" + obj.getLat() + "," + obj.getLng() + " (" + l + ")";
//                       Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                       getApplicationContext().startActivity(intent);
//                   }
//               });

                viewHolder.endTrip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        db.getReference("Users").child(obj.getUid()).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final Integer walletMoney = dataSnapshot.getValue(Integer.class);

                                db.getReference("RateDeposit").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Integer rateDeposit = dataSnapshot.getValue(Integer.class);
                                        mCar.child(obj.getCarId()).child("requests").removeValue();
                                        mUser.child("isRenting").setValue(false);
                                        mUser.child("wallet").setValue(walletMoney + rateDeposit);
                                        Toast.makeText(AllTripsActivity.this, "Deposit added back to account", Toast.LENGTH_SHORT).show();
                                        mCar.child(obj.getCarId()).child("availability").setValue("AVAILABLE");
                                        mTrips.child(obj.getTripId()).child("status").setValue("Trip Ended");

                                        db.getReference("Trips").child(obj.getUid()).child(obj.getTripId()).child("status").setValue("Trip Ended");

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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

}