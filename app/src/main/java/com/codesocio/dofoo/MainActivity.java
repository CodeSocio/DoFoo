 /*
  * DoFoo - A donation application
  * Copyright (C) 2021  CodeSocio
  *
  * This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU General Public License for more details.
  *
  * You should have received a copy of the GNU General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
  * Contact us at --> codesociodevs@gmail.com
  */

package com.codesocio.dofoo;

/*
 * Entry activity can also be called as listing activity and show listings
 * */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codesocio.dofoo.HelperClasses.GeoLocation;
import com.codesocio.dofoo.HelperClasses.Listing;
import com.codesocio.dofoo.HelperClasses.ListingAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Listing> listings;
    private boolean listingsReady = false;
    private ProgressBar loadingProgress;
    private int searchRadius = 5;   // Search radius for getting listings in KiloMeters
    private boolean prefVeg = false;
    private int filterQty = 100;
    private double latMin, latMax, longMin, longMax; // for searching withing searchRadius
    private LocationManager mLocationManager;
    private ListView listView;
    private View viewRequirementFragment;
    private View filtersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Getting Permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Please accept the Permissions from settings", Toast.LENGTH_SHORT).show();
        }

        checkFirstTimeUser();

        viewRequirementFragment = (View) findViewById(R.id.viewRequirementFragment);
        viewRequirementFragment.setVisibility(View.GONE);

        filtersFragment = (View) findViewById(R.id.filtersFragment);
        filtersFragment.setVisibility(View.GONE);

        ((RadioGroup) findViewById(R.id.preferenceRadioGroup1)).check(R.id.anyRadio);

        loadingProgress = (ProgressBar) findViewById(R.id.listingProgressBar);
        if (!listingsReady)
            loadingProgress.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.list);

        // set all gps data based on current location and search radius
        setMinMaxLatLong();

        // Initialize DB instance & get Listings
        getListings();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());
        //finish();
    }

    private void setMinMaxLatLong() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = getLastBestLocation();
        double currLat = location.getLatitude();
        double currLong = location.getLongitude();
        GeoLocation geoLocation = GeoLocation.fromDegrees(currLat, currLong);
        GeoLocation[] minMaxLocation = geoLocation.boundingCoordinates(searchRadius, 6371.01);
        latMin = minMaxLocation[0].getLatitudeInDegrees();
        longMin = minMaxLocation[0].getLongitudeInDegrees();
        latMax = minMaxLocation[1].getLatitudeInDegrees();
        longMax = minMaxLocation[1].getLongitudeInDegrees();
    }

    private void getListings() {
        listings = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference listingsRef = db.collection("Listings");
        Query query = listingsRef.whereGreaterThan("gpsLat", latMin)
                .whereLessThan("gpsLat", latMax);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    Listing temp = document.toObject(Listing.class);
                    if ((temp.getgpsLong() >= longMin && temp.getgpsLong() <= longMax) && (temp.isVeg() || !prefVeg) && (temp.getQty() <= filterQty)) {
                        listings.add(temp);
                    }
                }
                listingsReady = true;
                createListViews();
            } else {
                Toast.makeText(MainActivity.this, "Unable to connect to Internet Services", Toast.LENGTH_SHORT).show();
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

    // Function to call Profile if User already LoggedIn else go to login page
    public void gotoProfile(View v) {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        Intent intent;
        if (loggedIn)
            intent = new Intent(this, MyProfile.class);
        else
            intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void gotoSettings(View v) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    public void showContactForm(View v) {
        Intent intent = new Intent(this, ContactTheRecipient.class);
        startActivity(intent);
        finish();
    }

    public void closeViewRequirement(View view) {
        viewRequirementFragment.setVisibility(View.GONE);
    }

    //  Location Helper
    private Location getLastBestLocation() {

        // Check for Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    public void showFilters(View view) {
        filtersFragment.setVisibility(View.VISIBLE);
    }

    public void hideFilters(View view) {
        filtersFragment.setVisibility(View.GONE);
    }

    public void applyFilters(View view) {
        int[] searchRadiusTable = {1, 2, 5, 7, 10, 12, 15, 6371};
        int[] searchQtyTable = {10, 15, 25, 40, 50, 75, 100, Integer.MAX_VALUE};

        searchRadius = searchRadiusTable[((SeekBar) findViewById(R.id.distanceSeekBar)).getProgress()];
        int radioButtonId = ((RadioGroup) findViewById(R.id.preferenceRadioGroup1)).getCheckedRadioButtonId();
        prefVeg = radioButtonId == R.id.vegOnlyRadio;
        filterQty = searchQtyTable[((SeekBar) findViewById(R.id.platesSeekBar)).getProgress()];

        filtersFragment.setVisibility(View.GONE);

        loadingProgress.setVisibility(View.VISIBLE);
        listingsReady = false;

        // set all gps data based on current location and search radius
        setMinMaxLatLong();

        // Initilize DB instance & get Listings
        getListings();
    }

    // Instuctions for first time viewers
    private void checkFirstTimeUser(){
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        boolean firstTime = sharedPreferences.getBoolean("firstTime", true);
        if(!firstTime)
            return;
        Intent intent = new Intent(this, firstTimeUserGuide.class);
        startActivity(intent);
        finish();

    }
}