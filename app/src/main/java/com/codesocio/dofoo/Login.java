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
 * Login Page Activity
 *
 * */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    // Function to go to SignUp page
    public void gotoSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    // Executed when login button clicked
    public void logIn(View view) {
        String email = ((TextView) findViewById(R.id.emailField)).getText().toString();
        String password = ((TextView) findViewById(R.id.passwordField)).getText().toString();

        if(email.equals("")||password.equals("")){
            Toast.makeText(Login.this, "Please enter the username and password", Toast.LENGTH_SHORT).show();
            return;
        }


        //Perform Login operation
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(Login.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                        //Set Shared Preference Data
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

                        goBack();
                    } else {
                        // sign in fails
                        Toast.makeText(Login.this, "Log In Failed. Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}