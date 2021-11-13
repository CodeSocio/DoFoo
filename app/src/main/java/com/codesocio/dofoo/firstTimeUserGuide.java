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