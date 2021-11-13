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
 * Edit profile for the consumer/charity
 * */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codesocio.dofoo.HelperClasses.Consumer;
import com.codesocio.dofoo.HelperClasses.CustomExceptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity implements OnMapReadyCallback {
    private Consumer originalConsumerData = null;
    private LocationManager mLocationManager;
    private ProgressBar loadingProgress;
    private boolean mapOpen = false;
    private boolean wait = false;  // To indicate map marker activity is going on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViewById(R.id.map).setVisibility(View.GONE);
        loadingProgress = findViewById(R.id.editProgressBar);
        loadingProgress.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        String UID = sharedPreferences.getString("UID", null);

        if (UID == null) {
            gotoMain();
        }

        // get user data and put them in text fields
        DocumentReference dbUser = db.collection("Consumers").document(UID);
        dbUser.get().addOnSuccessListener(documentSnapshot -> {
            Consumer consumer = documentSnapshot.toObject(Consumer.class);
            if (consumer == null) {
                Toast.makeText(EditProfile.this, "Unable to fetch data from database." +
                        " Try again later", Toast.LENGTH_LONG).show();
                finish();
            }
            ((TextView) findViewById(R.id.editName)).setText(consumer.getName());
            ((TextView) findViewById(R.id.editPhone)).setText(consumer.getPhone());
            ((TextView) findViewById(R.id.editAddress)).setText(consumer.getAddress());
            ((TextView) findViewById(R.id.editEmail)).setText(consumer.getEmail());
            ((TextView) findViewById(R.id.nameBanner)).setText("Hi " + consumer.getName() + "!");

            /*
             *  @Warning Since this is asynchronous function body there might be a long delay for the data to be updated
             * */
            originalConsumerData = consumer;
            loadingProgress.setVisibility(View.GONE);
        });

    }

    //Function to save modified profile data to DB
    public void saveProfile(View view) {
        if (originalConsumerData == null || wait) {
            Toast.makeText(EditProfile.this, "Please wait for data to load", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = null;

        //Check if user logged in and in firebase cache
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You might not be logged in." +
                    " Try again after logging in.", Toast.LENGTH_LONG).show();
            gotoMain();
        }

        boolean passwordChangeReqd = false;

        // make new consumer object and set its fields to fields to text field data
        Consumer consumer = new Consumer(
                ((TextView) findViewById(R.id.editName)).getText().toString(),
                ((TextView) findViewById(R.id.editEmail)).getText().toString(),
                ((TextView) findViewById(R.id.editPhone)).getText().toString(),
                ((TextView) findViewById(R.id.editAddress)).getText().toString(),
                ""
        );
        String password = ((TextView) findViewById(R.id.editPassword)).getText().toString();
        String oldPassword = ((TextView) findViewById(R.id.editOldPassword)).getText().toString();
        if (password.equals(oldPassword))
            Toast.makeText(this, "Old and new passwords match. " +
                    "Try another one", Toast.LENGTH_SHORT).show();

        if (!password.equals("")) {
            passwordChangeReqd = true;
            consumer.setPassword(password);
        } else {
            //to avoid validation failures
            consumer.setPassword("testpass123");
        }

        // Verify integrity of password and other fields
        try {
            consumer.verifyIntegrity();
        } catch (CustomExceptions.InvalidEmailException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (CustomExceptions.InvalidPasswordException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (CustomExceptions.InvalidPhoneException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {

        }


        // push password to DB
        if (passwordChangeReqd && consumer.isValidated()) {
            // update password on db
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(originalConsumerData.getEmail(), oldPassword)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            firebaseUser.updatePassword(password)
                                    .addOnCompleteListener(task2 -> {
                                        if (!task2.isSuccessful()) {
                                            Toast.makeText(EditProfile.this, "Unable to push data to database." +
                                                    " Try again later", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            // If sign in fails
                            Toast.makeText(EditProfile.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
        }


        // update UID in shared preferences
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", true);
        FirebaseUser firebaseUser = null;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = firebaseUser.getUid();
        editor.putString("UID", UID);
        editor.apply();

        //Take changed values from consumer to originalConsumerData

        if (!originalConsumerData.getAddress().equals(consumer.getAddress()))
            originalConsumerData.setAddress(consumer.getAddress());

        if (!originalConsumerData.getPhone().equals(consumer.getPhone()))
            originalConsumerData.setPhone(consumer.getPhone());

        if (!originalConsumerData.getName().equals(consumer.getName()))
            originalConsumerData.setName(consumer.getName());

        //to avoid validation failures
        originalConsumerData.setPassword("testpass123");

        try {
            originalConsumerData.verifyIntegrity();
        } catch (CustomExceptions.InvalidEmailException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (CustomExceptions.InvalidPasswordException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (CustomExceptions.InvalidPhoneException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ignored) {

        }

        Log.d("GPSLAT", Double.toString(originalConsumerData.getgpsLat()));
        // Update user profile data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbConsumer = db.collection("Consumers").document(UID);

        dbConsumer.set(originalConsumerData).addOnSuccessListener(documentReference -> {
            // data addition is successful
            Toast.makeText(EditProfile.this, "Your data has been updated", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // on fail
            Toast.makeText(EditProfile.this, "Fail to add data \n" + e, Toast.LENGTH_SHORT).show();
        });

    }

    // callback for when map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        double lat = 0.0, lng = 0.0;
        String name = "Charity";

        if (originalConsumerData != null) {
            lat = originalConsumerData.getgpsLat();
            lng = originalConsumerData.getgpsLong();
            name = originalConsumerData.getName();
        }
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(name).draggable(true));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));

        //Tracking marker
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                wait = true;
            }

            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                wait = true;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker marker) {
                //Setting Lat and Long
                originalConsumerData.setgpsLong(marker.getPosition().longitude);
                originalConsumerData.setgpsLat(marker.getPosition().latitude);
                wait = false; //waiting can be stopped and data can be set
            }

        });
    }

    // Function to save gps co-ords
    public void saveGps(View view) {
        if (originalConsumerData == null) {
            Toast.makeText(EditProfile.this, "Please wait for data to load", Toast.LENGTH_SHORT).show();
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (!mapOpen) {
            findViewById(R.id.map).setVisibility(View.VISIBLE);
            mapOpen = true;
        } else {
            findViewById(R.id.map).setVisibility(View.GONE);
            mapOpen = false;
        }
    }

    //  Location Helper
    private Location getLastBestLocation() {

        // Check for Permission
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

    public void goBack(View view) {
        finish();
    }


    // Functions to go to main Activity
    public void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}