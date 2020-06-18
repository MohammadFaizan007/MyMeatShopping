package com.mymeatshop.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Vivek on 17/9/18.
 */

public class PreferencesManager {
    //app login variables
    private static final String PREF_NAME = "com.mymeatshop";
    private static final String USERID = "user_id";
    private static final String LOGINID = "login_id";
    private static final String PASSWORD = "password";
    private static final String CARTID = "cart_id";
    private static final String PERMISSION = "permission";
    private static final String PINCODE = "pincode";
    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String ADDRESS = "address";
    private static final String NAME = "name";
    private static final String DOB = "dob";
    private static final String LNAME = "lname";
    private static final String EMAIL = "email";
    private static final String MOBILE = "mobile";
    private static final String PROFILEPIC = "pic";
    private static final String ANDROIDID = "androidid";
    private static final String CARTCOUNT = "cart_count";
    private static final String PRODUCTID = "productid";
    private static final String CONFIRMATION = "confirmation";
    private static final String GuestToken = "guesttoken";
    private static final String Qid = "q_id";
    private static final String AuthToken = "authToken";
    private static final String ORDERID_GUEST = "orderidGuest";
    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    //for fragment
    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    //for getting instance
    public static synchronized PreferencesManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
        return sInstance;
    }

    public String getAuthToken() {
        return mPref.getString(AuthToken, "");
    }

    public void setAuthToken(String value) {
        mPref.edit().putString(AuthToken, value).apply();
    } public String getOrderidGuest() {
        return mPref.getString(ORDERID_GUEST, "");
    }

    public void setOrderidGuest(String value) {
        mPref.edit().putString(ORDERID_GUEST, value).apply();
    }

    public String getGuestToken() {
        return mPref.getString(GuestToken, "");
    }

    public void setGuestToken(String value) {
        mPref.edit().putString(GuestToken, value).apply();
    }

    public String getQid() {
        return mPref.getString(Qid, "");
    }

    public void setQid(String value) {
        mPref.edit().putString(Qid, value).apply();
    }

    public boolean clear() {
        return mPref.edit().clear().commit();
    }


    public String getAddress() {
        return mPref.getString(ADDRESS, "");
    }

    public void setAddress(String value) {
        mPref.edit().putString(ADDRESS, value).apply();
    }


    public String getCity() {
        return mPref.getString(CITY, "");
    }

    public void setCity(String value) {
        mPref.edit().putString(CITY, value).apply();
    }

    public String getSate() {
        return mPref.getString(STATE, "");
    }

    public void setState(String value) {
        mPref.edit().putString(STATE, value).apply();
    }

    public String getPINCODE() {
        return mPref.getString(PINCODE, "");
    }

    public void setPINCODE(String value) {
        mPref.edit().putString(PINCODE, value).apply();
    }


    public String getLoginID() {
        return mPref.getString(LOGINID, "");
    }

    public void setLoginID(String value) {
        mPref.edit().putString(LOGINID, value).apply();
    }

    public String getPassword() {
        return mPref.getString(PASSWORD, "");
    }

    public void setPassword(String value) {
        mPref.edit().putString(PASSWORD, value).apply();
    }

    public String getCartid() {
        return mPref.getString(CARTID, "");
    }

    public void setCartid(String value) {
        mPref.edit().putString(CARTID, value).apply();
    }

    public String getUSERID() {
        return mPref.getString(USERID, "");
    }

    public void setUSERID(String value) {
        mPref.edit().putString(USERID, value).apply();
    }

    public boolean getPermission() {
        return mPref.getBoolean(PERMISSION, false);
    }

    public void setPermission(boolean value) {
        mPref.edit().putBoolean(PERMISSION, value).apply();
    }

    public String getNAME() {
        return mPref.getString(NAME, "");
    }

    public void setNAME(String value) {
        mPref.edit().putString(NAME, value).apply();
    }

    public String getDOB() {
        return mPref.getString(DOB, "");
    }

    public void setDOB(String value) {
        mPref.edit().putString(DOB, value).apply();
    }

    public String getANDROIDID() {
        return mPref.getString(ANDROIDID, "");
    }

    public void setANDROIDID(String value) {
        mPref.edit().putString(ANDROIDID, value).apply();
    }


    public String getLNAME() {
        return mPref.getString(LNAME, "");
    }

    public void setLNAME(String value) {
        mPref.edit().putString(LNAME, value).apply();
    }

    public String getEMAIL() {
        return mPref.getString(EMAIL, "");
    }

    public void setEMAIL(String value) {
        mPref.edit().putString(EMAIL, value).apply();
    }

    public String getMOBILE() {
        return mPref.getString(MOBILE, "");
    }

    public void setMOBILE(String value) {
        mPref.edit().putString(MOBILE, value).apply();
    }

    public String getPROFILEPIC() {
        return mPref.getString(PROFILEPIC, "");
    }

    public void setPROFILEPIC(String value) {
        mPref.edit().putString(PROFILEPIC, value).apply();
    }

    public String getCartcount() {
        return mPref.getString(CARTCOUNT, "0");
    }

    public void setCartcount(String value) {
        mPref.edit().putString(CARTCOUNT, value).apply();
    }

    public String getProductid() {
        return mPref.getString(PRODUCTID, "");
    }

    public void setProductid(String value) {
        mPref.edit().putString(PRODUCTID, value).apply();
    }

    public String getConfirmation() {
        return mPref.getString(CONFIRMATION, "");
    }

    public void setConfirmation(String value) {
        mPref.edit().putString(CONFIRMATION, value).apply();
    }

}
