package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.Car;
import com.projectocean.safar.viewHolders.CarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class CarsActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView mCarlist;
    private FirebaseDatabase db;
    Query firebaseSearchQuery;
    SearchView action_search;
    FloatingActionButton floatingActionButton;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri saveUri;
    private ImageView IVimg;
    private ProgressDialog pd;
    private StorageReference imageFolder;
    private String numberPlate, seats, perHour, location, initialRate, modelName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();

        db = FirebaseDatabase.getInstance();

        pd = new ProgressDialog(this);
        pd.setMessage("loading...");
        pd.setCancelable(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        floatingActionButton = findViewById(R.id.add_car);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        CarsActivity.this, R.style.BottomSheetDialogTheme
                );

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(
                                R.layout.add__car_bottom_sheet,
                                (ScrollView) findViewById(R.id.bottom_sheet_container)
                        );

//                final RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);
//
//                Button buttonContinue = bottomSheetView.findViewById(R.id.continue_pay);

                final EditText ETcarModel = bottomSheetView.findViewById(R.id.model_name);
                final EditText ETperHour = bottomSheetView.findViewById(R.id.per_hour_rate);
                final EditText ETinitialRate = bottomSheetView.findViewById(R.id.initial_rate);
                final EditText ETlocation = bottomSheetView.findViewById(R.id.location);
                final EditText ETseats = bottomSheetView.findViewById(R.id.seats);
                final EditText ETnumberPlate = bottomSheetView.findViewById(R.id.number_plate);

                IVimg = bottomSheetView.findViewById(R.id.img);

                IVimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chooseImage();
                    }
                });


                Button add = bottomSheetView.findViewById(R.id.btn_add);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seats = ETseats.getText().toString();
                        initialRate = ETinitialRate.getText().toString();
                        perHour = ETperHour.getText().toString();
                        location = ETlocation.getText().toString();
                        modelName = ETcarModel.getText().toString();
                        numberPlate = ETnumberPlate.getText().toString();


                        if (seats.isEmpty()) {
                            Toast.makeText(CarsActivity.this, "Seats can't be empty", Toast.LENGTH_SHORT).show();
                        } else if (initialRate.isEmpty()) {

                            Toast.makeText(CarsActivity.this, "Initial rate can't be empty", Toast.LENGTH_SHORT).show();

                        } else if (perHour.isEmpty()) {

                            Toast.makeText(CarsActivity.this, "Per Hour rate can't be empty", Toast.LENGTH_SHORT).show();

                        } else if (location.isEmpty()) {

                            Toast.makeText(CarsActivity.this, "Location can't be empty", Toast.LENGTH_SHORT).show();

                        } else if (modelName.isEmpty()) {

                            Toast.makeText(CarsActivity.this, "Model name can't be empty", Toast.LENGTH_SHORT).show();

                        } else {


                            if (saveUri != null) {
                                pd.setTitle("Uploading Image....");
                                pd.show();
                                String imageName = UUID.randomUUID().toString();
                                imageFolder = FirebaseStorage.getInstance().getReference().child("cars/" + imageName);

                                //upload new image
                                imageFolder.putFile(saveUri)
                                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    saveUri = null;
                                                    pd.setTitle("Saving Data..");
                                                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String key = db.getReference("Cars").push().getKey() + "";

                                                            Car car = new Car(numberPlate, modelName, "AVAILABLE",
                                                                    uri.toString(), location, key, Integer.parseInt(seats),
                                                                    Integer.parseInt(perHour), Integer.parseInt(initialRate), location.toLowerCase());

                                                            db.getReference("Cars").child(key).setValue(car).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        pd.dismiss();
                                                                        bottomSheetDialog.dismiss();
                                                                        Toast.makeText(CarsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        pd.dismiss();
                                                                        bottomSheetDialog.dismiss();
                                                                        Toast.makeText(CarsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(CarsActivity.this, " upload Operation cancelled", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(CarsActivity.this, "Please select display image", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });


        // mDatabase.keepSynced(true);
        //
        mCarlist = (RecyclerView) findViewById(R.id.myrecycleview);
        mCarlist.setHasFixedSize(true);
        mCarlist.setLayoutManager(new LinearLayoutManager(this));
    }


    private void firebaseSearch(Query q) {

        FirebaseRecyclerAdapter<Car, CarViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Car, CarViewHolder>(Car.class,
                R.layout.car_row,
                CarViewHolder.class,
                q) {
            @Override
            protected void populateViewHolder(CarViewHolder viewHolder, final Car model, int position) {

                viewHolder.setDetails(model.getNumberPlate(), model.getCarModelName(), model.getAvailability(), getApplicationContext(), model.getImg(), model.getCapacity(), model.getLocation(), model.getPerhr(), model.getBase(),"admin");
                //  final String id=obj.getId();

//                viewHolder.location.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String l=c.getLocation()+" BaseStation";
//                        String geoUri = "http://maps.google.com/maps?q=loc:" + c.getLat() + "," + c.getLng() + " (" + l + ")";
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                        getApplicationContext().startActivity(intent);
//                    }
//                });

                viewHolder.car_book.setText("EDIT");
                viewHolder.car_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                                CarsActivity.this, R.style.BottomSheetDialogTheme
                        );

                        View bottomSheetView = LayoutInflater.from(getApplicationContext())
                                .inflate(
                                        R.layout.add__car_bottom_sheet,
                                        (ScrollView) findViewById(R.id.bottom_sheet_container)
                                );

//                final RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radio_group);
//
//                Button buttonContinue = bottomSheetView.findViewById(R.id.continue_pay);

                        final EditText ETcarModel = bottomSheetView.findViewById(R.id.model_name);
                        final EditText ETperHour = bottomSheetView.findViewById(R.id.per_hour_rate);
                        final EditText ETinitialRate = bottomSheetView.findViewById(R.id.initial_rate);
                        final EditText ETlocation = bottomSheetView.findViewById(R.id.location);
                        final EditText ETseats = bottomSheetView.findViewById(R.id.seats);
                        final EditText ETnumberPlate = bottomSheetView.findViewById(R.id.number_plate);
                        final TextView TVaddLabel = bottomSheetView.findViewById(R.id.add_label);

                        TVaddLabel.setText("Edit Car Information");


                        ETcarModel.setText(model.getCarModelName());
                        ETperHour.setText(model.getPerhr().toString());
                        ETinitialRate.setText(model.getBase().toString());
                        ETlocation.setText(model.getLocation());
                        ETseats.setText(model.getCapacity().toString());
                        ETnumberPlate.setText(model.getNumberPlate());

                        final ImageView IVdelete = bottomSheetView.findViewById(R.id.delete);
                        IVimg = bottomSheetView.findViewById(R.id.img);

                        if (model.getImg() != null && !model.getImg().isEmpty())
                            Picasso.with(getApplicationContext()).load(model.getImg()).into(IVimg);

                        IVimg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                chooseImage();
                            }
                        });


                        IVdelete.setVisibility(View.VISIBLE);

                        IVdelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                db.getReference("Cars").child(model.getCarId()).child("availability").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            if (dataSnapshot.getValue(String.class).equals("AVAILABLE")){
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(CarsActivity.this);
                                                builder1.setMessage("Are you sure to delete this car?");
                                                builder1.setCancelable(true);

                                                builder1.setPositiveButton(
                                                        "Yes",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                                db.getReference("Cars").child(model.getCarId()).removeValue();
                                                                dialog.dismiss();
                                                                bottomSheetDialog.dismiss();
                                                            }
                                                        });

                                                builder1.setNegativeButton(
                                                        "No",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });

                                                AlertDialog alert = builder1.create();
                                                alert.show();
                                            }
                                            else{
                                                Toast.makeText(CarsActivity.this, "Car is booked, First end the trip.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else{
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(CarsActivity.this);
                                            builder1.setMessage("Are you sure to delete this car?");
                                            builder1.setCancelable(true);

                                            builder1.setPositiveButton(
                                                    "Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {

                                                            db.getReference("Cars").child(model.getCarId()).removeValue();
                                                            dialog.dismiss();
                                                            bottomSheetDialog.dismiss();
                                                        }
                                                    });

                                            builder1.setNegativeButton(
                                                    "No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert = builder1.create();
                                            alert.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                
                              
                            }
                        });

                        Button add = bottomSheetView.findViewById(R.id.btn_add);

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                seats = ETseats.getText().toString();
                                initialRate = ETinitialRate.getText().toString();
                                perHour = ETperHour.getText().toString();
                                location = ETlocation.getText().toString();
                                modelName = ETcarModel.getText().toString();
                                numberPlate = ETnumberPlate.getText().toString();


                                if (seats.isEmpty()) {
                                    Toast.makeText(CarsActivity.this, "Seats can't be empty", Toast.LENGTH_SHORT).show();
                                } else if (initialRate.isEmpty()) {

                                    Toast.makeText(CarsActivity.this, "Initial rate can't be empty", Toast.LENGTH_SHORT).show();

                                } else if (perHour.isEmpty()) {

                                    Toast.makeText(CarsActivity.this, "Per Hour rate can't be empty", Toast.LENGTH_SHORT).show();

                                } else if (location.isEmpty()) {

                                    Toast.makeText(CarsActivity.this, "Location can't be empty", Toast.LENGTH_SHORT).show();

                                } else if (modelName.isEmpty()) {

                                    Toast.makeText(CarsActivity.this, "Model name can't be empty", Toast.LENGTH_SHORT).show();

                                } else {

                                    if (saveUri != null) {
                                        pd.setMessage("Uploading Image....");
                                        pd.show();
                                        String imageName = UUID.randomUUID().toString();
                                        imageFolder = FirebaseStorage.getInstance().getReference().child("cars/" + imageName);

                                        //upload new image
                                        imageFolder.putFile(saveUri)
                                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                        if (task.isSuccessful()){
                                                            saveUri = null;
                                                            pd.setMessage("Saving Data..");
                                                            Toast.makeText(CarsActivity.this, "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                                                            imageFolder.getDownloadUrl()
                                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    String key = model.getCarId();

                                                                    Car car = new Car(numberPlate, modelName, "AVAILABLE",
                                                                            uri.toString(), location, key, Integer.parseInt(seats),
                                                                            Integer.parseInt(perHour), Integer.parseInt(initialRate), location.toLowerCase());

                                                                    db.getReference("Cars").child(key).setValue(car).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                pd.dismiss();
                                                                                bottomSheetDialog.dismiss();
                                                                                Toast.makeText(CarsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                                                            } else {
                                                                                pd.dismiss();
                                                                                bottomSheetDialog.dismiss();
                                                                                Toast.makeText(CarsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                        else{
                                                            pd.dismiss();
                                                            Toast.makeText(CarsActivity.this, "Upload operation cancelled", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }
                                                });

                                    } else {
                                        String key = model.getCarId();

                                        Car car = new Car(numberPlate, modelName, "AVAILABLE",
                                                model.getImg(), location, key, Integer.parseInt(seats),
                                                Integer.parseInt(perHour), Integer.parseInt(initialRate), location.toLowerCase());

                                        db.getReference("Cars").child(key).setValue(car).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    bottomSheetDialog.dismiss();
                                                    Toast.makeText(CarsActivity.this, "added", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    pd.dismiss();
                                                    bottomSheetDialog.dismiss();
                                                    Toast.makeText(CarsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });                                    }

                                }

                            }
                        });

                        bottomSheetDialog.setContentView(bottomSheetView);
                        bottomSheetDialog.show();
                    }

                });


            }


        };
        mCarlist.setAdapter(firebaseRecyclerAdapter);

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                saveUri = data.getData();
                Picasso.with(getApplicationContext()).load(saveUri).into(IVimg);
            } else {
                saveUri = null;
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseSearch(db.getReference("Cars"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu2, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Enter Location to Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String text = query.toLowerCase();
                firebaseSearchQuery = db.getReference("Cars").orderByChild("search").startAt(text).endAt(text + "\uf8ff");
                firebaseSearch(firebaseSearchQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter as u type
                String text = newText.toLowerCase();
                firebaseSearchQuery = db.getReference("Cars").orderByChild("search").startAt(text).endAt(text + "\uf8ff");
                firebaseSearch(firebaseSearchQuery);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //handle other action bar item clicks here
        if (id == R.id.action_settings) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
