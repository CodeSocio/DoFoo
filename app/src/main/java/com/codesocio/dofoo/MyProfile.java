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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codesocio.dofoo.HelperClasses.Consumer;
import com.codesocio.dofoo.HelperClasses.Listing;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends AppCompatActivity {
    private ProgressBar loadingProgress;
    private Consumer User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);

        // for hiding action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        loadingProgress = (ProgressBar) findViewById(R.id.profileProgressBar);
        loadingProgress.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //get User ID from the shared preferences
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        String UID = sharedPreferences.getString("UID", null);
        if (UID == null) {
            gotoMain();
        }

        DocumentReference dbUser = db.collection("Consumers").document(UID);
        dbUser.get().addOnSuccessListener(documentSnapshot -> {
            Consumer consumer = documentSnapshot.toObject(Consumer.class);
            if (consumer != null) {
                ((TextView) findViewById(R.id.charityName)).setText(consumer.getName());
                ((TextView) findViewById(R.id.phone)).setText(consumer.getPhone());
                ((TextView) findViewById(R.id.address)).setText(consumer.getAddress());
                loadingProgress.setVisibility(View.GONE);
                User = consumer;
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void editUserListing(View view){
        Intent intent = new Intent(this, EditRequirement.class);
        startActivity(intent);
    }

    public void gotoEditProfile(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void gotoCreateReq(View v){
        // Check for pre-existing listing of current user
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        String UID = sharedPreferences.getString("UID", null);

        if (UID == null)
            return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbConsumer = db.collection("Listings").document(UID);

        dbConsumer.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Toast.makeText(MyProfile.this, "You already have a listing on this platform." +
                        " Delete it to continue", Toast.LENGTH_LONG).show();
            }else{
                //Launch CreateRequirement Activity
                Intent intent = new Intent(this, CreateRequirement.class);
                startActivity(intent);
            }
        });
    }

    public void gotoSettings(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}