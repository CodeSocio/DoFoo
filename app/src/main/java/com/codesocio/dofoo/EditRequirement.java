package com.codesocio.dofoo;

/*
 * Activity For Editing Requirements by the registered charity
 * */

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codesocio.dofoo.HelperClasses.Listing;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditRequirement extends AppCompatActivity {
    private View successRequirementPlacementFragment;
    private String UID;
    private Listing listing;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_requirements);

        // for hiding action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loadingProgress = (ProgressBar) findViewById(R.id.profileProgressBar);
        loadingProgress.setVisibility(View.VISIBLE);

        ((RadioGroup) findViewById(R.id.preferenceRadioGroup)).check(R.id.anyRadio);
        successRequirementPlacementFragment = (View) findViewById(R.id.successRequirementPlacementFragment);
        successRequirementPlacementFragment.setVisibility(View.GONE);

        @SuppressLint("WrongConstant") SharedPreferences sharedPreferences =
                this.getSharedPreferences(
                        this.getResources().getString(R.string.preferencesFile),
                        MODE_APPEND);
        UID = sharedPreferences.getString("UID", null);

        if (UID == null)
            finish();

        // fetch listing data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbConsumer = db.collection("Listings").document(UID);

        dbConsumer.get().addOnSuccessListener(documentSnapshot -> {
            // data fetch is successful
            listing = documentSnapshot.toObject(Listing.class);
            ((TextView) findViewById(R.id.noOfPlatesField)).setText(Integer.toString(listing.getQty()));
            ((TextView) findViewById(R.id.noteField)).setText(listing.getNote());
            if(listing.isVeg())
                ((RadioGroup) findViewById(R.id.preferenceRadioGroup)).check(R.id.vegOnlyRadio);
            loadingProgress.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            // on fail
            finish();
        });
    }

    public void submit(View v) {
        boolean veg = false;
        int radioButtonId = ((RadioGroup) findViewById(R.id.preferenceRadioGroup)).getCheckedRadioButtonId();
        if (radioButtonId == R.id.vegOnlyRadio)
            veg = true;
        String note = ((EditText) findViewById(R.id.noteField)).getText().toString();
        int qty = Integer.parseInt(((EditText)findViewById(R.id.noOfPlatesField)).getText().toString());
        if (qty <= 0) {
            Toast.makeText(EditRequirement.this, "Number of persons can't be zero or below", Toast.LENGTH_SHORT).show();
            return;
        }
        listing.setNote(note);
        listing.setVeg(veg);
        listing.setQty(qty);
        pushListing();
    }

    public void goBack(View v){
        finish();
    }

    /**
     * For pushing the listing to db
     */
    private void pushListing(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbConsumer = db.collection("Listings").document(UID);

        dbConsumer.set(listing).addOnSuccessListener(documentReference -> {
            // data addition is successful
            Toast.makeText(EditRequirement.this, "Added the listing successfully",
                    Toast.LENGTH_LONG).show();
            successRequirementPlacementFragment.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            // on fail
            Toast.makeText(EditRequirement.this, "Fail to add data to database",
                    Toast.LENGTH_SHORT).show();
        });
    }

    public void hideSuccess(View view){
        successRequirementPlacementFragment.setVisibility(View.GONE);
    }

    public void deleteThisListing(View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dbConsumer = db.collection("Listings").document(UID);

        dbConsumer.delete().addOnSuccessListener(documentReference -> {
            // data addition is successful
            Toast.makeText(EditRequirement.this, "Listing successfully deleted.",
                    Toast.LENGTH_LONG).show();
        }).addOnFailureListener(e -> {
            // on fail
            Toast.makeText(EditRequirement.this, "Failed to delete data from database",
                    Toast.LENGTH_SHORT).show();
        });
    }
}