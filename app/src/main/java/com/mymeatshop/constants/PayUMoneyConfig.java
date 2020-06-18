package com.mymeatshop.constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PayUMoneyConfig {

    // Testing Payment Gateway
    public static final String MONEY_HASH = "https://debajyotibasak.000webhostapp.com/PayUMoneyHash.php";
    public static final String SURL = "https://www.payumoney.com/mobileapp/payumoney/success.php";
    public static final String FURL = "https://www.payumoney.com/mobileapp/payumoney/failure.php";

    public static final String MERCHANT_KEY = "XZRY7bvz";
    public static final String MERCHANT_ID = "6792066";
    public static final String MERCHANT_SALT = "cyHBsm5D2n";

    public static final boolean DEBUG = false;

//    Auth Header: 2Or9B39rpCtnIXibsI9AkSjDflHHtcISQhKl1LcIDNk=

    public static String hashCal(String type, String str) {
        byte[] hashseq = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return hexString.toString();
    }

}
