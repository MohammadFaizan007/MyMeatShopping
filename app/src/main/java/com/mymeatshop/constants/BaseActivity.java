package com.mymeatshop.constants;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.retrofit.ApiServices;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.retrofit.ServiceGenerator;
import com.mymeatshop.utils.DialogUtil;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkConnectionChecker;
import com.mymeatshop.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;


public abstract class BaseActivity extends AppCompatActivity implements NetworkConnectionChecker.OnConnectivityChangedListener, View.OnClickListener, MvpView {
    protected static final int PERMISSION_CONTACTS_REQUEST_CODE = 13;
    protected static final int PERMISSION_LOCATION_REQUEST_CODE = 14;
    private static final String TAG = "BaseActivity";
    //AESCrypt-ObjC uses CBC and PKCS7Padding
    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET = "UTF-8";
    //AESCrypt-ObjC uses SHA-256 (and so a 256-bit key)
    private static final String HASH_ALGORITHM = "SHA-256";
    //AESCrypt-ObjC uses blank IV (not the best security, but the aim here is compatibility)
    private static final byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    //togglable log option (please turn off in live!)
    public static boolean DEBUG_LOG_ENABLED = false;
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public ApiServices apiServices_shoopping, apiGuest_Shopping;
    public ServiceGenerator serviceGenerator;
    public Activity context;
    public boolean isConnected;
    public SecretKey cross_intent;
    public SharedPreferences pref;
    Dialog flightDialog;
    private ProgressDialog mProgressDialog;

    public static void finishActivity(Activity activity) {
        Utils.hideSoftKeyboard(activity);
        activity.finish();
    }

    /**
     * Encrypt and encode message using 256-bit AES with key generated from password.
     *
     * @param password used to generated key
     * @param message  the thing you want to encrypt assumed String UTF-8
     * @return Base64 encoded CipherText
     * @throws GeneralSecurityException if problems occur during encryption
     */
    public static String encryptMsg(String message, final SecretKey password)
            throws GeneralSecurityException {

        try {
//            password_key = new SecretKeySpec(password.getBytes(), "AES");
//            final SecretKeySpec key = generateKey(password);

            LoggerUtil.logItem(message);

            byte[] cipherText = encrypt(password, ivBytes, message.getBytes(CHARSET));

            //NO_WRAP is important as was getting \n at the end
            String encoded = Base64.encodeToString(cipherText, Base64.NO_WRAP);
            LoggerUtil.logItem(encoded);
            return encoded;
        } catch (UnsupportedEncodingException e) {
//            if (DEBUG_LOG_ENABLED)
//                Log.e(TAG, "UnsupportedEncodingException ", e);
            throw new GeneralSecurityException(e);
        }
    }

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key     AES key typically 128, 192 or 256 bit
     * @param iv      Initiation Vector
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKey key, final byte[] iv, final byte[] message)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] cipherText = cipher.doFinal(message);

        LoggerUtil.logItem(cipherText);

        return cipherText;
    }

    /**
     * Decrypt and decode ciphertext using 256-bit AES with key generated from password
     *
     * @param password                used to generated key
     * @param base64EncodedCipherText the encrpyted message encoded with base64
     * @return message in Plain text (String UTF-8)
     * @throws GeneralSecurityException if there's an issue decrypting
     */
    public static String decryptMsg(String base64EncodedCipherText, final SecretKey password)
            throws GeneralSecurityException {

        try {

            LoggerUtil.logItem(base64EncodedCipherText);
            byte[] decodedCipherText = Base64.decode(base64EncodedCipherText, Base64.NO_WRAP);
            LoggerUtil.logItem(decodedCipherText);

            byte[] decryptedBytes = decrypt(password, ivBytes, decodedCipherText);

            LoggerUtil.logItem(decryptedBytes);
            String message = new String(decryptedBytes, CHARSET);
            LoggerUtil.logItem(message);


            return message;
        } catch (UnsupportedEncodingException e) {
//            if (DEBUG_LOG_ENABLED)
//                Log.e(TAG, "UnsupportedEncodingException ", e);

            throw new GeneralSecurityException(e);
        }
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key               AES key typically 128, 192 or 256 bit
     * @param iv                Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKey key, final byte[] iv, final byte[] decodedCipherText)
            throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(AES_MODE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(decodedCipherText);

        LoggerUtil.logItem(decryptedBytes);

        return decryptedBytes;
    }

    /**
     * Converts byte array to hexidecimal useful for logging and fault finding
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public JsonObject bodyParam(JsonObject param) {
        JsonObject body = new JsonObject();
        try {
            body.addProperty("body", Cons.encryptMsg(param.toString(), cross_intent));
            LoggerUtil.logItem(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public void getAndroidId() {
        try {
            if (PreferencesManager.getInstance(context).getANDROIDID().equalsIgnoreCase("")) {
                AppConfig.ANDROIDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                PreferencesManager.getInstance(context).setANDROIDID(AppConfig.ANDROIDID);
                LoggerUtil.logItem(AppConfig.ANDROIDID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearPrefrenceforTokenExpiry() {
        PreferencesManager.getInstance(context).clear();
    }

    public void hasContactPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)

            requestContactPermission();
    }

    public void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.CALL_PHONE)
                || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
            Utils.createSimpleDialog1(context, getString(R.string.alert_text), getString(R.string.permission_camera_rationale11), getString(R.string.reqst_permission), new Utils.Method() {
                @Override
                public void execute() {
                    ActivityCompat.requestPermissions(context, new String[]{
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.READ_CONTACTS},
                            PERMISSION_CONTACTS_REQUEST_CODE);
                }
            });

        } else {
            ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CONTACTS},
                    PERMISSION_CONTACTS_REQUEST_CODE);
        }
    }

    public void hasLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

            requestLocationPermission();
    }
    protected boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(BaseActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            ActivityCompat.requestPermissions(BaseActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

    }


    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Utils.createSimpleDialog1(context, getString(R.string.alert_text), getString(R.string.permission_location), getString(R.string.reqst_permission), new Utils.Method() {
                @Override
                public void execute() {
                    ActivityCompat.requestPermissions(context, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_LOCATION_REQUEST_CODE);
                }
            });

        } else {
            ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    public void hideFlightLoading() {
        if (flightDialog != null && flightDialog.isShowing()) {
            flightDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        try {
            pref = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);
//            context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            cross_intent = new SecretKeySpec(BuildConfig.MYMEATSHOP_COMPARED.getBytes(), "AES");

            getAndroidId();
            serviceGenerator = ServiceGenerator.getInstance();
            apiServices_shoopping = ServiceGenerator.createServiceShopping(ApiServices.class);
            apiGuest_Shopping = ServiceGenerator.createGuestShopping(ApiServices.class);

            new DialogUtil(BaseActivity.this);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getTopDeal(String name, String sku) {

    }

    @Override
    public void getPayoutWithdrawalId(String requestId) {

    }

    @Override
    public void openSearchCategory(String searchItemId, String searchName) {

    }

    @Override
    public void checkAvailability(String id, String date, String name, String amount) {

    }

    @Override
    public void getGiftCardCategoryId(String id, String name) {

    }

    public void goToActivityWithFinish(Class<?> classActivity, Bundle bundle) {
        Intent intent = new Intent(context, classActivity);
        if (bundle != null) intent.putExtra(PAYLOAD_BUNDLE, bundle);
        Utils.hideSoftKeyboard(context);
        context.startActivity(intent);
        context.finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    public void showToastS(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text1 = layout.findViewById(R.id.text);
        text1.setText(text);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 170);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showLoading() {
        mProgressDialog = DialogUtil.showLoadingDialog(context, "Base Activity");
    }

    @Override
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void openActivityOnTokenExpire() {

    }

    @Override
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int resId) {

    }

    @Override
    public boolean isNetworkConnected() {
        return isConnected;
    }

    public void showAlert(String msg, int color, int icon) {
        Alerter.create(context).setText(msg).setTextAppearance(R.style.alertTextColor).setBackgroundColorRes(color).setIcon(icon).show();
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void getDateDetails(Date date) {

    }

    @Override
    public void getClickPosition(int position, String tag) {

    }

    @Override
    public void connectivityChanged(boolean availableNow) {
        isConnected = availableNow;
    }

    public void createInfoDialog(Context context, String title, String msg) {
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                BaseActivity.this.finish();
            }
        });

        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = activity.getCurrentFocus();
        if (v != null) {
            IBinder binder = activity.getCurrentFocus().getWindowToken();
            if (binder != null) {
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(binder, 0);
                }
            }
        }
    }

    public void goToActivity(Activity activity, Class<?> classActivity, Bundle bundle) {
        Utils.hideSoftKeyboard(activity);
        Intent intent = new Intent(activity, classActivity);
        if (bundle != null) intent.putExtra(PAYLOAD_BUNDLE, bundle);
        activity.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void goToActivityWithFinish(Activity activity, Class<?> classActivity, Bundle bundle) {
        Intent intent = new Intent(context, classActivity);
        if (bundle != null) intent.putExtra(PAYLOAD_BUNDLE, bundle);
        Utils.hideSoftKeyboard(activity);
        activity.startActivity(intent);
        activity.finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void getClickPositionDirectMember(int position, String tag, String memberId) {

    }

    @Override
    public void setTransferTicket(String trainingamount, String trainingname, String trainingID, String totalTickets, String date) {

    }

    @Override
    public void getProviderHint(String providerName, String hint) {

    }
}

