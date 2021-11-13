package com.codesocio.dofoo;

/*
 * Sign up for the consumer/charity
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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.codesocio.dofoo.HelperClasses.Consumer;
import com.codesocio.dofoo.HelperClasses.CustomExceptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LocationManager mLocationManager;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        if (sharedPreferences.getBoolean("loggedIn", false)) {
            gotoProfile();
        }
        db = FirebaseFirestore.getInstance();
    }

    public void signUp(View view) {
        String name = ((TextView) findViewById(R.id.nameField)).getText().toString();
        String email = ((TextView) findViewById(R.id.emailField)).getText().toString();
        String phone = ((TextView) findViewById(R.id.phoneField)).getText().toString();
        String address = ((TextView) findViewById(R.id.addressField)).getText().toString();
        String password = ((TextView) findViewById(R.id.passwordField)).getText().toString();
        String confPassword = ((TextView) findViewById(R.id.confPasswordField)).getText().toString();


        if (!password.equals(confPassword)) {
            Toast.makeText(this, "Password doesn't match with confirm password entered", Toast.LENGTH_SHORT).show();
            return;
        }

        // Creating an object of c for storing user data
        Consumer consumer = new Consumer(name, email, phone, address, password);

        // Validating user entered cred
        try {
            consumer.verifyIntegrity();
        } catch (CustomExceptions.InvalidEmailException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (CustomExceptions.InvalidPasswordException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (CustomExceptions.InvalidPhoneException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // go for db operations if validated
        if (!consumer.isValidated())
            return;

        // getting gps location
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = getLastBestLocation();
        consumer.setgpsLat(location.getLatitude());
        consumer.setgpsLong(location.getLongitude());

        // Auth functions
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                                this.getSharedPreferences(
                                        this.getResources().getString(R.string.preferencesFile),
                                        MODE_APPEND);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("loggedIn", true);
                        FirebaseUser firebaseUser = null;
                        firebaseUser = mAuth.getCurrentUser();
                        String UID = firebaseUser.getUid();
                        editor.putString("UID", UID);
                        editor.apply();

                        //Database action to get Users document reference
                        storeUserData(consumer, UID);

                        gotoProfile();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignUp.this, "Authentication failed. Try again later",
                                Toast.LENGTH_SHORT).show();
                    }

                });
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
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    // Function to redirect to Profile page
    public void gotoProfile(){
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        boolean loggedIn = sharedPreferences.getBoolean("loggedIn", false);
        Intent intent;
        if(loggedIn)
            intent = new Intent(this, MyProfile.class);
        else
            intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    //Function to goback i.e. Login
    public void goBack(View v){
        finish();
    }

    private void storeUserData(Consumer consumer, String UID){
        DocumentReference dbConsumer = db.collection("Consumers").document(UID);

        dbConsumer.set(consumer).addOnSuccessListener(documentReference -> {
            // data addition is successful
            Toast.makeText(SignUp.this, "Your data has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // on fail
            Toast.makeText(SignUp.this, "Fail to add data \n" + e, Toast.LENGTH_SHORT).show();
        });
    }
}