package com.projectocean.safar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.projectocean.safar.models.Car;
import com.projectocean.safar.models.Trip;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddDetailsActivity extends AppCompatActivity {
    TextView carModel, carCapacity, carLocation, carPerhour, carBase, numRent, numDeposit, numTotal;
    EditText carHours, carPickupTime, carDate;
    Button btn_rent;
    LinearLayout rent_details;
    Button pay;

    Date currentTime;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());

    ImageView img;
    FirebaseDatabase db;

    String code;
    Integer rateDeposit;
    DatabaseReference mRef, mDeposit, mUser, mTrip, mAvl, mIncomeDeposit, mIncome, mOffers;
    FirebaseAuth mAuth;
    Integer setTotal;
    String sCarHours, sCarPickup, sCarDate;
    Integer rent, dis;
    int mHour, mMinute, mYear, mMonth, mDay;
    TextView havePromo, apply;
    EditText promocode_e;
    ProgressDialog pd;
    Long ts;
    ValueEventListener offersListener;
    boolean promoApplied = false;
    private int selectedId;
    Car carSelected;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);


        carSelected = (Car) getIntent().getSerializableExtra("Car");


        mOffers = db.getReference("Offers");
        mTrip = db.getReference("Trips");
        mRef = db.getReference("Cars");
        mDeposit = db.getReference("RateDeposit");
        mUser = db.getReference("Users").child(uid);
        mIncomeDeposit = db.getReference("IncomeDeposit");
        mAvl = mRef.child(carSelected.getCarId()).child("availability");

        btn_rent = findViewById(R.id.btn_rent);
        rent_details = findViewById(R.id.rent_details);
        carModel = findViewById(R.id.car_model);
        carCapacity = findViewById(R.id.car_capacity);
        carLocation = findViewById(R.id.car_location);
        carPerhour = findViewById(R.id.car_perhour);
        carBase = findViewById(R.id.car_base);
        carHours = findViewById(R.id.car_hours);
        carDate = findViewById(R.id.car_date);
        carPickupTime = findViewById(R.id.car_pickup_time);
        numDeposit = findViewById(R.id.num_deposit);
        numRent = findViewById(R.id.num_rent);
        numTotal = findViewById(R.id.num_total);
        pay = (Button) findViewById(R.id.pay);
        img = findViewById(R.id.car_img);
        havePromo = findViewById(R.id.have_promo);
        promocode_e = findViewById(R.id.promocode_e);
        apply = findViewById(R.id.apply);

        carModel.setText(carSelected.getCarModelName());
        carCapacity.setText(carSelected.getCapacity() + " Seater");
        carLocation.setText(carSelected.getLocation());
        carPerhour.setText("Per Hour- " + getString(R.string.currency) + " " + carSelected.getPerhr());
        carBase.setText("Initial- " + getString(R.string.currency) + " " + carSelected.getBase());

        if (carSelected.getImg() != null && !carSelected.getImg().isEmpty())
            Picasso.with(getApplicationContext()).load(carSelected.getImg()).into(img);

        pay.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        offersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot c : dataSnapshot.getChildren()) {

                    if (code.equals(c.child("promo").getValue(String.class)) && !promoApplied) {
                        dis = c.child("discount").getValue(Integer.class);
                        promoApplied = true;
                        btn_rent.performClick();

                        Toast.makeText(AddDetailsActivity.this, "Promo Applied", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        carPickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });

        carDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        btn_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateCheck();
                pd.show();

                mDeposit.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            rateDeposit = dataSnapshot.getValue(Integer.class);
                            Integer total;

                            sCarHours = carHours.getText().toString();
                            sCarDate = carDate.getText().toString();
                            sCarPickup = carPickupTime.getText().toString();

                            if (sCarDate.isEmpty() || sCarHours.isEmpty() || sCarPickup.isEmpty()) {
                                Toast.makeText(AddDetailsActivity.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                            } else {

                                rent = Integer.parseInt(sCarHours) * carSelected.getPerhr() +
                                        carSelected.getBase();

                                if (promoApplied) {
                                    rent = rent - dis;
                                }

                                total = rateDeposit + rent;
                                setTotal = total;
                                mUser.child("total").setValue(total);
                                numRent.setText(getString(R.string.currency) + " " + String.valueOf(rent));
                                numDeposit.setText(getString(R.string.currency) + " " + String.valueOf(rateDeposit));
                                numTotal.setText(getString(R.string.currency) + " " + String.valueOf(total));

                                //  numRent.setText(rent);
                                //numDeposit.setText();

                                if (promoApplied) {
                                    havePromo.setText("promo applied");
                                    promocode_e.setVisibility(View.GONE);
                                    apply.setVisibility(View.GONE);
                                }

                                rent_details.setVisibility(View.VISIBLE);
                                pay.setVisibility(View.VISIBLE);
                                havePromo.setVisibility(View.VISIBLE);
                                Log.e("aa", "till last lineeeeeeeeeeeeeeee");
                            }

                            pd.dismiss();
                        } else {
                            pd.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

        });


        havePromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promocode_e.setVisibility(View.VISIBLE);
                apply.setVisibility(View.VISIBLE);
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stateCheck();
                if (promocode_e.getText().toString().isEmpty()) {
                    Toast.makeText(AddDetailsActivity.this, "Enter Promocode", Toast.LENGTH_SHORT).show();
                } else {
                    code = promocode_e.getText().toString();
                    mOffers.addValueEventListener(offersListener);
                }

            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pd.show();

                db.getReference("Users").child(uid).child("isRenting").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        pd.dismiss();

                        if (Objects.equals(dataSnapshot.getValue(), true)) {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddDetailsActivity.this);
                            builder1.setMessage("We kindly request you to end trip current trip in \" My Trips \" Section");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent(AddDetailsActivity.this, MyTripsActivity.class));
                                            finish();
                                        }
                                    });

                            AlertDialog alert = builder1.create();
                            alert.show();
                        } else {
                            stateCheck();
                            if (isNetworkAvailable()) {
                                showBottomSheet();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


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


    public void pickTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String sHourOfDay, sMinutes;

                        sHourOfDay = Integer.toString(hourOfDay);
                        sMinutes = Integer.toString(minute);

                        if (hourOfDay < 10) {
                            sHourOfDay = "0" + sHourOfDay;
                        }
                        if (minute < 10) {
                            sMinutes = "0" + sMinutes;
                        }

                        String pickupTime = sHourOfDay + ":" + sMinutes;

                        carPickupTime.setText(pickupTime);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void pickDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear += 1;

                        String sDayofMonth, sMonthOfYear, sYear;

                        sDayofMonth = Integer.toString(dayOfMonth);
                        sMonthOfYear = Integer.toString(monthOfYear);
                        sYear = Integer.toString(year);

                        if (dayOfMonth < 10) {
                            sDayofMonth = "0" + sDayofMonth;
                        }

                        if (monthOfYear < 10) {
                            sMonthOfYear = "0" + sMonthOfYear;
                        }

                        String date = sDayofMonth + "-" + sMonthOfYear + "-" + sYear;

                        carDate.setText(date);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    private void stateCheck() {
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(AddDetailsActivity.this);
            builder1.setMessage("No internet Connection");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(AddDetailsActivity.this, ShowCarsActivity.class));
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

    }

    @Override
    public void finish() {

        mAvl.removeEventListener(offersListener);
        super.finish();
    }


    private void showBottomSheet() {

        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                AddDetailsActivity.this, R.style.BottomSheetDialogTheme
        );

        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (ScrollView) findViewById(R.id.bottom_sheet_container)
                );

        final RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);

        Button buttonContinue = bottomSheetView.findViewById(R.id.continue_pay);


        RadioButton radioButton = findViewById(selectedId);

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(getApplicationContext(), "Please select payment method", Toast.LENGTH_SHORT).show();
                } else {


                    switch (selectedId) {
                        case R.id.wallet:
                            bookCarUsingWallet();
                            break;
                        case R.id.card_payment:
                            Intent cardPayIntent = new Intent(AddDetailsActivity.this, CardPayActivity.class);

                            currentTime = Calendar.getInstance().getTime();
                            ts = Long.parseLong(dateFormat.format(currentTime));
                            String tId = mTrip.child(uid).push().getKey();
                            Trip trip = new Trip(carSelected.getCarId(), sCarDate, sCarPickup, carSelected.getLocation(),
                                    rent, carSelected.getNumberPlate(), Integer.parseInt(sCarHours), "LIVE", tId, ts);

                            cardPayIntent.putExtra("trip", trip);
                            startActivityForResult(cardPayIntent, 11);
                            break;
                    }
                }
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void bookCarUsingWallet() {

        db.getReference("Users").child(uid).child("wallet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue(Integer.class) != null) {
                    if (setTotal <= dataSnapshot.getValue(Integer.class)) {
                        pd = new ProgressDialog(AddDetailsActivity.this);
                        pd.setMessage("loading");
                        pd.setCancelable(false);
                        pd.show();

                        mUser.child("isRenting").setValue(true);
                        mRef.child(carSelected.getCarId()).child("requests").setValue(uid);
                        mRef.child(carSelected.getCarId()).child("availability").setValue("UNAVAILABLE");
                        mUser.child("wallet").setValue((dataSnapshot.getValue(Integer.class) - setTotal));
                        mUser.child("currentCar").setValue(carSelected.getCarId());


                        String tId = mTrip.child(uid).push().getKey();
                        currentTime = Calendar.getInstance().getTime();

                        ts = Long.parseLong(dateFormat.format(currentTime));

                        Trip trip = new Trip(carSelected.getCarId(), sCarDate, sCarPickup, carSelected.getLocation(),
                                rent, carSelected.getNumberPlate(), Integer.parseInt(sCarHours), "LIVE", tId, ts);

                        db.getReference("AllTrips").child(tId).setValue(trip);
                        db.getReference("AllTrips").child(tId).child("uid").setValue(uid);


                        mTrip.child(uid).child(tId).setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                startActivity(new Intent(AddDetailsActivity.this, MyTripsActivity.class));
                                Toast.makeText(getApplicationContext(), "Trip Added", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        });
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddDetailsActivity.this);
                        builder1.setMessage("Insufficient balance");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Add money to wallet",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(AddDetailsActivity.this, AddMoneyActivity.class);
                                        i.putExtra("type", "shortCall");
                                        startActivityForResult(i, 21);
                                    }
                                });

                        builder1.setNegativeButton(
                                "Cancel booking",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(new Intent(AddDetailsActivity.this, DashboardActivity.class));
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder1.create();
                        alert.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 21 && resultCode == RESULT_OK) {

        }
        if (requestCode == 11 && resultCode == RESULT_OK) {
            finish();
        }
    }
}