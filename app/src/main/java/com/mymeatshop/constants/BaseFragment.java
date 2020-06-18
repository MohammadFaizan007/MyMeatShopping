package com.mymeatshop.constants;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
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
import com.mymeatshop.utils.Utils;
import com.tapadoo.alerter.Alerter;

import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;


public abstract class BaseFragment extends Fragment implements MvpView {
    // Toolbar toolbar;
    static final int ASK_SEND_SMS_PERMISSION_REQUEST_CODE = 14;
    private static final String TAG = "BaseFragment";
    protected final Gson gson = new Gson();
    public Activity context;
    public ApiServices apiServices_shoopping;
    public SecretKey cross_intent;
    private ServiceGenerator serviceGenerator;
    private ProgressDialog mProgressDialog;

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
                AppConfig.ANDROIDID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            cross_intent = new SecretKeySpec(BuildConfig.MYMEATSHOP_COMPARED.getBytes(), "AES");
            serviceGenerator = ServiceGenerator.getInstance();
            apiServices_shoopping = ServiceGenerator.createServiceShopping(ApiServices.class);
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
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
    public void getClickPosition(int position, String tag) {

    }

    @Override
    public void getGiftCardCategoryId(String id, String name) {

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
    public void showMessage(int resId) {

    }

    @Override
    public void getTopDeal(String name, String sku) {

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideKeyboard() {

    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }

    @Override
    public void getDateDetails(Date date) {

    }

    @Override
    public void getProviderHint(String providerName, String hint) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PreferencesManager.initializeInstance(getContext());
        onViewCreatedStuff(view, savedInstanceState);
    }

    public void showAlert(String msg, int color, int icon) {
        Alerter.create(context).setText(msg).setTextAppearance(R.style.alertTextColor).setBackgroundColorRes(color).setIcon(icon).show();
    }

    public abstract void onViewCreatedStuff(View view, @Nullable Bundle savedInstanceState);

    public void showLoading() {
        mProgressDialog = DialogUtil.showLoadingDialog(getActivity(), TAG);
    }

    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void createInfoDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void goToActivity(Class<?> classActivity, Bundle bundle) {
        Utils.hideSoftKeyboard(getActivity());
        Intent intent = new Intent(getContext(), classActivity);
        if (bundle != null) intent.putExtra(PAYLOAD_BUNDLE, bundle);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void goToActivityWithFinish(Class<?> classActivity, Bundle bundle) {
        Intent intent = new Intent(getContext(), classActivity);
        if (bundle != null) intent.putExtra(PAYLOAD_BUNDLE, bundle);
        Utils.hideSoftKeyboard(getActivity());
        getActivity().startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void checkSMSPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED)
            requestSMSPermission();
    }

    public void requestSMSPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECEIVE_SMS)) {
            Utils.createSimpleDialog1(getActivity(), getString(R.string.alert_text), getString(R.string.permission_camera_rationale11), getString(R.string.reqst_permission), new Utils.Method() {
                @Override
                public void execute() {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, ASK_SEND_SMS_PERMISSION_REQUEST_CODE);
                }
            });
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, ASK_SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void getClickPositionDirectMember(int position, String tag, String memberId) {

    }

    @Override
    public void setTransferTicket(String trainingamount, String trainingname, String trainingID, String totalTickets, String date) {

    }
}
