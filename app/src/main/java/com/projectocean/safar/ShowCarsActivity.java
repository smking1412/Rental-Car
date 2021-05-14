package com.projectocean.safar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.Car;
import com.projectocean.safar.viewHolders.CarViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class ShowCarsActivity extends AppCompatActivity {
    Toolbar toolbar;
    private RecyclerView mCarlist;
    private FirebaseDatabase db;
    Query firebaseSearchQuery;
    SearchView action_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cars);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();

        db = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

                viewHolder.setDetails(model.getNumberPlate(), model.getCarModelName(), model.getAvailability(), getApplicationContext(), model.getImg(), model.getCapacity(), model.getLocation(), model.getPerhr(), model.getBase(), "customer");

                viewHolder.car_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (model.getAvailability().equals("UNAVAILABLE"))
                            Toast.makeText(ShowCarsActivity.this, "Car is Already Booked", Toast.LENGTH_LONG).show();

                        else {
                            Intent i = new Intent(view.getContext(), AddDetailsActivity.class);
                            i.putExtra("Car", model);
                            startActivity(i);
                        }
                    }

                });
            }
        };
        mCarlist.setAdapter(firebaseRecyclerAdapter);

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
}
