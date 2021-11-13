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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.codesocio.dofoo.HelperClasses.Listing;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactTheRecipient extends AppCompatActivity implements OnMapReadyCallback {
    private Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_the_recipient);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        listing = intent.getParcelableExtra("Target Listing");
        ((TextView) findViewById(R.id.charityName3)).setText(listing.getName());
        ((TextView) findViewById(R.id.charityEmail3)).setText(listing.getEmail());
        ((TextView) findViewById(R.id.charityPhone3)).setText(listing.getPhone());
        ((TextView) findViewById(R.id.charityAddress3)).setText(listing.getAddress());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // callback for when map is ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        double lat = 0.0, lng = 0.0;
        String name = "Charity";

        if (listing != null) {
            lat = listing.getgpsLat();
            lng = listing.getgpsLong();
            name = listing.getName();
        }
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(name).draggable(false));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16));
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void callTheRecipient(View view) {
        // Getting Permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
            }
        } catch (Exception e) {
            Toast.makeText(ContactTheRecipient.this, "Please accept the Permissions from settings", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + listing.getPhone()));
        startActivity(intent);
    }

    /**
     * To open maps in the phone
     *
     * @param view
     */
    public void openMaps(View view) {

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + listing.getgpsLat() + "," + listing.getgpsLong());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}