package com.codesocio.dofoo;

/*
 * Activity For Creating Requirements by the registered charity
 * */

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codesocio.dofoo.HelperClasses.Consumer;
import com.codesocio.dofoo.HelperClasses.Listing;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateRequirement extends AppCompatActivity {
    private View successRequirementPlacementFragment;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_requirements);

        // for hiding action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ((RadioGroup) findViewById(R.id.preferenceRadioGroup)).check(R.id.anyRadio);
        successRequirementPlacementFragment = (View) findViewById(R.id.successRequirementPlacementFragment);
        successRequirementPlacementFragment.setVisibility(View.GONE);
    }

    public void submit(View v) {
        boolean veg = false;
        int radioButtonId = ((RadioGroup) findViewById(R.id.preferenceRadioGroup)).getCheckedRadioButtonId();
        if (radioButtonId == R.id.vegOnlyRadio)
            veg = true;
        String note = ((EditText) findViewById(R.id.noteField)).getText().toString();
        int qty = Integer.parseInt(((EditText)findViewById(R.id.noOfPlatesField)).getText().toString());
        if (qty <= 0) {
            Toast.makeText(CreateRequirement.this, "Number of persons can't be zero or below", Toast.LENGTH_SHORT).show();
            return;
        }

        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        UID = sharedPreferences.getString("UID", null);

        if (UID == null)
            finish();

        // get user data and put them in text fields
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbUser = db.collection("Consumers").document(UID);
        boolean finalVeg = veg;
        dbUser.get().addOnSuccessListener(documentSnapshot -> {
            Consumer consumer = documentSnapshot.toObject(Consumer.class);
            if (consumer == null) {
                Toast.makeText(CreateRequirement.this, "Unable to fetch data from database." +
                        " Try again later", Toast.LENGTH_LONG).show();
                finish();
            }
            /*
             *  @Warning Since this is asynchronous function body there might be a long delay for the data to be updated
             * */
            Listing listing = new Listing(qty, finalVeg, note, consumer);
            pushListing(listing);
        });
    }

    public void goBack(View v){
        finish();
    }

    /**
     * For pushing the listing to db
     * @param listing : Listing object
     */
    private void pushListing(Listing listing){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference dbConsumer = db.collection("Listings").document(UID);

        dbConsumer.set(listing).addOnSuccessListener(documentReference -> {
            // data addition is successful
            Toast.makeText(CreateRequirement.this, "Added the listing successfully",
                    Toast.LENGTH_LONG).show();
            successRequirementPlacementFragment.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            // on fail
            Toast.makeText(CreateRequirement.this, "Fail to add data to database",
                    Toast.LENGTH_SHORT).show();
        });
    }

    public void hideSuccess(View view){
        successRequirementPlacementFragment.setVisibility(View.GONE);
    }
}