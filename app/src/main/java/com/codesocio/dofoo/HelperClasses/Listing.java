package com.codesocio.dofoo.HelperClasses;

/*
 *
 *   Used to Store Listing Data
 *   Please refer to the DB schema to get to know how it is used in database
 *   provides functionalities to insert and create listings
 *
 *   Expiry Format -- YYYY-MM-DD    Invert this string while displaying to make it user friendly
 *
 *   Note this class implements Parcelable to provide transfer between activities
 *   TODO implement some cloud function or firebase function to remove listings from db based on expiry date
 *
 */


import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class Listing  implements Parcelable {
    private int qty;
    private boolean veg;
    private String note;
    private String email;
    private String expiry;
    private double gpsLat, gpsLong;
    private String address;
    private String phone;
    private String name;

    public Listing() {
        qty = 0;
        veg = false;
        note = null;
        email = null;
        expiry = new java.sql.Date(System.currentTimeMillis()).toString(); // expiry data must be one day next
        address = null;
        phone = null;
        name = null;

    }

    /**
     *
     * @param qty : number of plates
     * @param veg : veg only preference
     * @param note : special note
     * @param consumer : Consumer object
     */
    public Listing(int qty, boolean veg, String note, Consumer consumer) {
        this.qty = qty;
        this.veg = veg;
        this.note = note;
        expiry = new java.sql.Date(System.currentTimeMillis()).toString(); // expiry data must be one day next
        this.phone = consumer.getPhone();
        this.address = consumer.getAddress();
        this.gpsLat = consumer.getgpsLat();
        this.gpsLong = consumer.getgpsLong();
        this.name = consumer.getName();
        this.email = consumer.getEmail();
    }

    public double getgpsLat() {
        return gpsLat;
    }

    public double getgpsLong() {
        return gpsLong;
    }

    public void setgpsLat(double gpsLat) {
        this.gpsLat = gpsLat;
    }

    public void setgpsLong(double gpsLong) {
        this.gpsLong = gpsLong;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public int getQty() {
        return qty;
    }

    public boolean isVeg() {
        return veg;
    }

    public String getNote() {
        return note;
    }

    public String getEmail() {
        return email;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setName(String name) {
        if (name.equals(""))
            return;
        this.name = name;
    }

    public void setPhone(String phone) {
        if (phone.equals(""))
            return;
        this.phone = phone;
    }

    public void setAddress(String address) {
        if (address.equals(""))
            return;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @param parcel : of type Parcel
     * @param i : int for flags
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBoolean(veg);
        parcel.writeInt(qty);
        parcel.writeString(note);
        parcel.writeString(email);
        parcel.writeString(expiry);
        parcel.writeDouble(gpsLat);
        parcel.writeDouble(gpsLong);
        parcel.writeString(address);
        parcel.writeString(phone);
        parcel.writeString(name);
    }

    public static final Creator<Listing> CREATOR = new Creator<Listing>() {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Listing createFromParcel(Parcel source) {
            return new Listing(source);
        }

        @Override
        public Listing[] newArray(int i) {
            return new Listing[0];
        }
    };

    /**
     * to support transport between activities
     * @param source : of type Parcel
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Listing(Parcel source) {
        veg = source.readBoolean();
        qty = source.readInt();
        note = source.readString();
        email = source.readString();
        expiry = source.readString();
        gpsLat = source.readDouble();
        gpsLong = source.readDouble();
        address = source.readString();
        phone = source.readString();
        name = source.readString();
    }
}
