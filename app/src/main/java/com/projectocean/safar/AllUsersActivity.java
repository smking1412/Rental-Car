package com.projectocean.safar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectocean.safar.models.User;
import com.projectocean.safar.viewHolders.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;


public class AllUsersActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_all_users);
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
        // mDatabase.keepSynced(true);
        //
        mCarlist = (RecyclerView) findViewById(R.id.myrecycleview);
        mCarlist.setHasFixedSize(true);
        mCarlist.setLayoutManager(new LinearLayoutManager(this));
    }


    private void firebaseSearch(Query q) {

        FirebaseRecyclerAdapter<User, UserViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class,
                R.layout.user_layout,
                UserViewHolder.class,
                q) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, final User model, int position) {

                viewHolder.setDetails(model.getName(),model.getEmail());

            }


        };
        mCarlist.setAdapter(firebaseRecyclerAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();

        firebaseSearch(db.getReference("Users"));
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
