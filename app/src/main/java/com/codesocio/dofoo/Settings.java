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
 *  Settings page
 * */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        // for hiding action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        findViewById(R.id.aboutUsFragment).setVisibility(View.GONE);
        findViewById(R.id.faqFragment).setVisibility(View.GONE);
        findViewById(R.id.contactUsFragment).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void logOut(View v) {
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_PRIVATE);

        if (!sharedPreferences.getBoolean("loggedIn", false)) {
            Toast.makeText(Settings.this, "You are not logged in to log out", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedIn");
        editor.putBoolean("loggedIn", false);
        editor.apply();
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(Settings.this, "You are logged out successfully", Toast.LENGTH_SHORT).show();
    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void switchToFAQ(View view) {
        findViewById(R.id.faqFragment).setVisibility(View.VISIBLE);
    }

    public void closeFAQ(View view) {
        findViewById(R.id.faqFragment).setVisibility(View.GONE);
    }

    public void switchToContactUs(View view) {
        findViewById(R.id.contactUsFragment).setVisibility(View.VISIBLE);
    }

    public void closeContactUs(View view) {
        findViewById(R.id.contactUsFragment).setVisibility(View.GONE);
    }

    public void switchToSettings(View view) {
        setContentView(R.layout.settings);
        findViewById(R.id.aboutUsFragment).setVisibility(View.GONE);
    }

    public void closeAboutUs(View view) {
        findViewById(R.id.aboutUsFragment).setVisibility(View.GONE);
    }

    public void switchToAboutUs(View view) {
        findViewById(R.id.aboutUsFragment).setVisibility(View.VISIBLE);
    }

    public void openPermissions(View view){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    public void openLocationSettings(View view){
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void mailToDevs(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"+this.getResources().getString(R.string.devEmail));
        intent.setData(data);
        startActivity(intent);
    }

    public void mailToBackendDev(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"+this.getResources().getString(R.string.backendDevEmail));
        intent.setData(data);
        startActivity(intent);
    }

    public void callToBackendDev(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("tel:"+this.getResources().getString(R.string.backendDevPhone));
        intent.setData(data);
        startActivity(intent);
    }

    public void mailToFrontendDev(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:"+this.getResources().getString(R.string.frontendDevEmail));
        intent.setData(data);
        startActivity(intent);
    }

    public void callToFrontendDev(View view){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("tel:"+this.getResources().getString(R.string.frontendDevPhone));
        intent.setData(data);
        startActivity(intent);
    }
}