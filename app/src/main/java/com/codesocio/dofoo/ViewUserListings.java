package com.codesocio.dofoo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codesocio.dofoo.HelperClasses.Listing;
import com.codesocio.dofoo.HelperClasses.ListingAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ViewUserListings extends AppCompatActivity {
    private ArrayList<Listing> listings;
    private boolean listingsReady = false;
    private ProgressBar loadingProgress;
    private ListView listView;
    private View viewRequirementFragment;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_user_listings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Getting Permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            Toast.makeText(ViewUserListings.this, "Please accept the Permissions from settings", Toast.LENGTH_SHORT).show();
        }

        userEmail = getIntent().getStringExtra("Target Email");

        viewRequirementFragment = (View) findViewById(R.id.viewRequirementFragment);
        viewRequirementFragment.findViewById(R.id.proceedToDonateButton).setVisibility(View.GONE);
        viewRequirementFragment.setVisibility(View.GONE);

        loadingProgress = (ProgressBar) findViewById(R.id.listingProgressBar);
        if (!listingsReady)
            loadingProgress.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.list);

        // Initilize DB instance & get Listings
        getListings();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getListings() {
        listings = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference listingsRef = db.collection("Listings");
        Query query = listingsRef.whereEqualTo("email", userEmail);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Listing temp = document.toObject(Listing.class);
                        listings.add(temp);
                }
                listingsReady = true;
                createListViews();
            } else {
                Toast.makeText(ViewUserListings.this, "Unable to connect to Internet Services", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // To create ListView for listings
    private void createListViews() {
        ListingAdapter listingAdapter = new ListingAdapter(getApplicationContext(), listings, listView);
        if (listingsReady)
            loadingProgress.setVisibility(View.GONE);
        listView.setAdapter(listingAdapter);
    }

    public void closeViewRequirement(View view) {
        viewRequirementFragment.setVisibility(View.GONE);
    }

}