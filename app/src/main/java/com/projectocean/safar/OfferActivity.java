package com.projectocean.safar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OfferActivity extends AppCompatActivity {
    ImageView offer_detail_img;
    DatabaseReference offers;
    String offername;
    TextView title, detail, promo, discount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);

        Intent i = getIntent();
        offername = i.getStringExtra("offername");
        offers = FirebaseDatabase.getInstance().getReference("Offers").child(offername);

        offer_detail_img = findViewById(R.id.offer_detail_img);
        title = findViewById(R.id.title);
        detail = findViewById(R.id.detail);
        promo = findViewById(R.id.promo);
        discount = findViewById(R.id.discount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        offers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Picasso.with(OfferActivity.this).load( dataSnapshot.child("img").getValue(String.class) ).into(offer_detail_img);
                title.setText(  dataSnapshot.child("title").getValue(String.class) );
                detail.setText(dataSnapshot.child("details").getValue(String.class));
                promo.setText("Promocode : " + dataSnapshot.child("promo").getValue(String.class));
                discount.setText("Discount : "+getString(R.string.currency)+" " +dataSnapshot.child("discount").getValue(Integer.class));
               // discount.setText("Discount : " + dataSnapshot.child("discount").getValue(Integer.class));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        offer_detail_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String geoUri = "http://maps.google.com/maps?q=loc:" + 18.539449 + "," + 73.886742 + " (" + "TRIAL" + ")";
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                getApplicationContext().startActivity(intent);
//            }
//        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

