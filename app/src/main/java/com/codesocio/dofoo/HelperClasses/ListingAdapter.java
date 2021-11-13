package com.codesocio.dofoo.HelperClasses;


/*
 * Used as an Adapter for Listings page
 * Use it only for listing purpose as its a custom class based on BaseAdapter
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codesocio.dofoo.ContactTheRecipient;
import com.codesocio.dofoo.R;

import java.util.ArrayList;

public class ListingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Listing> listings;
    private LayoutInflater inflater;
    private ListView thisListView;
    private int curPos = -1;

    public ListingAdapter(Context applicationContext, ArrayList<Listing> listings, ListView listView) {
        this.context = applicationContext;
        this.listings = listings;
        thisListView = listView;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return listings.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.list, null);
        Listing currListing = listings.get(i);
        TextView name = (TextView) view.findViewById(R.id.customerName);
        TextView phone = (TextView) view.findViewById(R.id.customerPhone);
        TextView address = (TextView) view.findViewById(R.id.customerAddress);
        TextView plates = (TextView) view.findViewById(R.id.platesRequired);
        TextView preference = (TextView) view.findViewById(R.id.listingPreferenceField);
        Button detailViewButton = (Button) view.findViewById(R.id.detailViewButton);
        detailViewButton.setOnClickListener(detailView);
        name.setText(currListing.getName());
        phone.setText(currListing.getPhone());
        address.setText(currListing.getAddress());
        plates.setText(Integer.toString(currListing.getQty()));
        if(!currListing.isVeg())
            preference.setText("Any");
        view.setTag(currListing);
        return view;
    }

    /**
     * @param null
     * onclick Listener for listings cards
     */
    private View.OnClickListener detailView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentView = v.getRootView();
            final int position = thisListView.getPositionForView((View) v.getParent());
            TextView preference = parentView.findViewById(R.id.requirementPreferenceField);
            TextView qtyField = parentView.findViewById(R.id.requirementQtyField);
            TextView note = parentView.findViewById(R.id.requirementNoteField);
            Button donateBtn = parentView.findViewById(R.id.proceedToDonateButton);
            Listing listing = listings.get(position);
            if(listing.isVeg())
                preference.setText("Veg Only");
            else
                preference.setText("Veg/Non-Veg");
            qtyField.setText(String.valueOf(listing.getQty()));
            if(!listing.getNote().equals(""))
                note.setText(listing.getNote());
            parentView.findViewById(R.id.viewRequirementFragment).setVisibility(View.VISIBLE);
            donateBtn.setOnClickListener(proceedToDonate);
            curPos = position;
        }
    };

    /**
     *
     * @param null
     * onclick Listener for proceed to book button
     */
    private View.OnClickListener proceedToDonate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context.getApplicationContext(), ContactTheRecipient.class);
            intent.putExtra("Target Listing", listings.get(curPos));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };
}
