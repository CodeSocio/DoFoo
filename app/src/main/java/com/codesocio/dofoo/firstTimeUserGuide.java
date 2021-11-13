package com.codesocio.dofoo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class firstTimeUserGuide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_time_user_guide_1);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void gotoPage1(View v){
        setContentView(R.layout.first_time_user_guide_1);
    }

    public void gotoPage2(View v){
        setContentView(R.layout.first_time_user_guide_2);
    }

    public void gotoPage3(View v){
        setContentView(R.layout.first_time_user_guide_3);
    }

    public void gotoPage4(View v){
        setContentView(R.layout.first_time_user_guide_4);
    }

    public void gotoPage5(View v){
        setContentView(R.layout.first_time_user_guide_5);
    }

    public void finishGuide(View v){
        // add firstTime as false to avoid showing the instructions the next time
        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstTime", false);
        editor.apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}