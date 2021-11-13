package com.codesocio.dofoo.HelperClasses;

import android.location.Address;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.codesocio.dofoo.HelperClasses.CustomExceptions;


/*
 *
 *   Used to Store Consumer Data
 *   Please refer to the DB schema to get to know how it is used in database
 *   provides functionalities to enforce cred rules and verify them
 *   validates the data internally to ensure integrity
 *
 *   Password validation Policy: Min 6 char long
 *                               must have a number
 *                               must have an alphabet
 *
 *
 */


public class Consumer {
    private String email;
    private String name;
    private String phone;
    private String address;
    private String password;
    private boolean validated;          // Used to verify validation is performed and is ready for use in database
    private double gpsLat, gpsLong;


    public Consumer() {
        this.address = null;
        this.name = null;
        this.password = null;
        this.email = null;
        this.phone = null;
        validated = false;
        gpsLat = 0.0;
        gpsLong = 0.0;
    }

    public Consumer(String name, String email, String phone, String address, String password) {
        this.address = address;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phone = phone;
        validated = false;
        gpsLat = 0.0;
        gpsLong = 0.0;
    }

    /**
     *
     * @return gpsLat -> Latitude for GPS
     */
    public double getgpsLat(){
        return gpsLat;
    }

    /**
     *
     * @return gpsLong -> Longitude for GPS
     */
    public double getgpsLong(){
        return gpsLong;
    }

    /**
     *
     * @param gpsLat : Latitude for GPS
     */
    public void setgpsLat(double gpsLat){
        this.gpsLat = gpsLat;
    }

    /**
     *
     * @param gpsLong : Longitude for GPS
     */
    public void setgpsLong(double gpsLong){
        this.gpsLong = gpsLong;
    }

    /**
     *
     * @param name : name of Consumer
     */
    public void setName(String name) {
        if(name.equals(""))
            return;
        this.name = name;
        validated = false;
    }

    /**
     *
     * @param password : password of Consumer
     */
    public void setPassword(String password) {
        if(password.equals(""))
            return;
        this.password = password;
        validated = false;
    }

    /**
     *
     * @param email : email of Consumer
     */
    public void setEmail(String email) {
        if(email.equals(""))
            return;
        this.email = email;
        validated = false;
    }

    /**
     *
     * @param phone : phone of Consumer
     */
    public void setPhone(String phone) {
        if(phone.equals(""))
            return;
        this.phone = phone;
        validated = false;
    }

    /**
     *
     * @param address : address of Consumer
     */
    public void setAddress(String address) {
        if(address.equals(""))
            return;
        this.address = address;
        validated = false;
    }

    /**
     *
     * @return name : name of Consumer
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return email : email of Consumer
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @return phone : phone of Consumer
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @return address : address of Consumer
     */
    public String getAddress() {
        return address;
    }

    /**
     *
     * @return  validated : to indicate whether the object is verified
     */
    public boolean isValidated() {
        return validated;
    }

    /**
     * Used to perform object Validation based on criteria for each member
     * @return validated : to indicate whether the object is verified
     * @throws CustomExceptions.InvalidEmailException
     * @throws CustomExceptions.InvalidPasswordException
     * @throws CustomExceptions.InvalidPhoneException
     */
    public boolean verifyIntegrity() throws CustomExceptions.InvalidEmailException,
            CustomExceptions.InvalidPasswordException,
            CustomExceptions.InvalidPhoneException {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{6,20}$"; // Used for password validation

        // Email check
        if(!email.matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))
            throw new CustomExceptions.InvalidEmailException("Check Email id");

        // Phone Check
        if (!phone.matches("(0/91)?[6-9][0-9]{9}"))
            throw new CustomExceptions.InvalidPhoneException("Check Phone Number");

        // Password Check
        if (password == null)
            throw new CustomExceptions.InvalidPasswordException("Password is Empty");
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (!m.matches())
            throw new CustomExceptions.InvalidPasswordException("Password doesn't match the requirements");

        // Ensure address not empty
        if (address == "" || address == null)
            return false;
        // Ensure name not empty
        if (name == "" || name == null)
            return false;

        validated = true;
        return true;
    }
};