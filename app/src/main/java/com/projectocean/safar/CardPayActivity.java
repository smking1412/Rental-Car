package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.CardData;
import com.projectocean.safar.models.Trip;
import com.projectocean.safar.viewHolders.CardDataViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CardPayActivity extends AppCompatActivity {
    RecyclerView recyclerviewCards;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String uid;
    private ProgressDialog pd;

    RelativeLayout relativeLayoutRecyclerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        uid = mAuth.getUid();

        recyclerviewCards = findViewById(R.id.recycler_view);
        relativeLayoutRecyclerViewContainer = findViewById(R.id.recycler_view_container);

        final EditText ETcardNumber = findViewById(R.id.card_number);
         final EditText ETmm = findViewById(R.id.month);
         final EditText ETyy = findViewById(R.id.year);
         final EditText ETcvv = findViewById(R.id.cvv);
        final EditText ETname =findViewById(R.id.name);
        final CheckBox CBsave = findViewById(R.id.save);


        db.getReference("SavedCards").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    relativeLayoutRecyclerViewContainer.setVisibility(View.VISIBLE);
                }
                else{
                    relativeLayoutRecyclerViewContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ETcardNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });

        Button pay = findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = ETname.getText().toString();
                String year = ETyy.getText().toString();
                String month = ETmm.getText().toString();
                String cvv = ETcvv.getText().toString();
                String cardnumber = ETcardNumber.getText().toString();
                boolean save = CBsave.isChecked();

                if (name.isEmpty()) {
                    Toast.makeText(CardPayActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                }

                else if (year.isEmpty() || Integer.parseInt(year) < 20) {

                    Toast.makeText(CardPayActivity.this, "Invalid year", Toast.LENGTH_SHORT).show();

                }

                else if (month.isEmpty() || Integer.parseInt(month) > 12) {

                    Toast.makeText(CardPayActivity.this, "Invalid Month", Toast.LENGTH_SHORT).show();

                }

                else if (cvv.isEmpty() || Integer.parseInt(cvv) <= 100) {

                    Toast.makeText(CardPayActivity.this, "CVV should be 3 digits", Toast.LENGTH_SHORT).show();

                }

                else if (cardnumber.length() != 19) {

                    Toast.makeText(CardPayActivity.this, "Invalid card Number", Toast.LENGTH_SHORT).show();

                }

                else {
                    String key = db.getReference("SavedCards").child(uid).push().getKey() + "";
                    CardData cardData = new CardData(cardnumber, name, key, Integer.parseInt(month), Integer.parseInt(year), Integer.parseInt(cvv));

                    if (save){
                        db.getReference("SavedCards").child(uid).child(key).setValue(cardData);
                    }

                    Trip trip = (Trip) getIntent().getSerializableExtra("trip");

                    trip.setCardInfo(cardData);

                    createTrip(trip);

                }
            }
        });


        recyclerviewCards.setHasFixedSize(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerviewCards.setLayoutManager(mLayoutManager);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Query q = db.getReference("SavedCards").child(uid);

        firebaseSearch(q);
    }

    public void firebaseSearch(Query q) {

        Log.d("recycle debug", "firebasesearch");

        FirebaseRecyclerAdapter<CardData, CardDataViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<CardData, CardDataViewHolder>(
                CardData.class, R.layout.cardlayout, CardDataViewHolder.class, q
        ) {

            @Override
            protected void populateViewHolder(final CardDataViewHolder viewHolder, final CardData model, final int position) {


                viewHolder.setDetails(model.getCardNumber(), model.getExpMonth(), model.getExpYear(), model.getCardHolderName(), model.getCvv());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Trip trip = (Trip) getIntent().getSerializableExtra("trip");

                        trip.setCardInfo(model);

                        createTrip(trip);
                    }
                });

            }
        };


        recyclerviewCards.setAdapter(firebaseRecyclerAdapter);
    }

    private void createTrip(final Trip trip) {
        db.getReference("Users").child(uid).child("isRenting").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                pd = new ProgressDialog(CardPayActivity.this);
                pd.setMessage("loading");
                pd.setCancelable(false);
                pd.show();


                db.getReference("AllTrips").child(trip.getTripId()).setValue(trip);
                db.getReference("AllTrips").child(trip.getTripId()).child("uid").setValue(uid);

                db.getReference("Cars").child(trip.getCarId()).child("requests").setValue(uid);
                db.getReference("Cars").child(trip.getCarId()).child("availability").setValue("UNAVAILABLE");
                db.getReference("Users").child(uid).child("currentCar").setValue(trip.getCarId());
                db.getReference("Trips").child(uid).child(trip.getTripId()).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        startActivity(new Intent(CardPayActivity.this, MyTripsActivity.class));
                        Toast.makeText(getApplicationContext(), "Trip Added", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
        });
    }
}
