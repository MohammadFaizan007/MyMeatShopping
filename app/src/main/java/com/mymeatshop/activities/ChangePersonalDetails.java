package com.mymeatshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePersonalDetails;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import java.security.GeneralSecurityException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePersonalDetails extends BaseActivity {
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.badge)
    ImageView badge;
    @BindView(R.id.cart_counter)
    TextView cartCounter;
    @BindView(R.id.edit_tv)
    TextView editTv;
    @BindView(R.id.et_mobile)
    TextInputEditText etMobile;
    @BindView(R.id.textInputLayoutMobile)
    TextInputLayout textInputLayoutMobile;
    @BindView(R.id.et_lastName)
    TextInputEditText etLastName;
    @BindView(R.id.textInputLayoutLastName)
    TextInputLayout textInputLayoutLastName;
    @BindView(R.id.et_email)
    TextInputEditText etEmail;
    @BindView(R.id.textInputLayoutEmail)
    TextInputLayout textInputLayoutEmail;
    @BindView(R.id.et_firstName)
    TextInputEditText etFirstName;
    @BindView(R.id.textInputLayoutFirstName)
    TextInputLayout textInputLayoutFirstName;
    @BindView(R.id.btn_update_details)
    Button btnUpdateDetails;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_personal_details);
        ButterKnife.bind(this);
        title.setText("Change Personal Details");
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
        try {
            etFirstName.setText(decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent));
            etLastName.setText(decryptMsg(PreferencesManager.getInstance(context).getLNAME(), cross_intent));
            etEmail.setText(decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent));
            etMobile.setText(decryptMsg(PreferencesManager.getInstance(context).getMOBILE(), cross_intent));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter, R.id.edit_tv, R.id.btn_update_details})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.searchImg:
                break;
            case R.id.badge:
            case R.id.cart_counter:
                goToActivity(ChangePersonalDetails.this, CartActivity.class, null);
                break;
            case R.id.edit_tv:
                break;
            case R.id.btn_update_details:
                if (Validation()) {
                    if (NetworkUtils.getConnectivityStatus(context) != 0) {
                        getChangePersonalDetails();
                    } else {
                        showMessage(getResources().getString(R.string.alert_internet));
                    }
                }
                break;
        }
    }

    private void getChangePersonalDetails() {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            jsonObject.addProperty("firstname", etFirstName.getText().toString());
            jsonObject.addProperty("lastname", etLastName.getText().toString());
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("mobile", etMobile.getText().toString());
            jsonObject.addProperty("password", decryptMsg(PreferencesManager.getInstance(context).getPassword(), cross_intent));
            LoggerUtil.logItem(jsonObject);
            Call<JsonObject> loginCall = apiServices_shoopping.getChangePersonalDetails(bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseChangePersonalDetails responseChangePersonalDetails = new Gson().fromJson(paramResponse, ResponseChangePersonalDetails.class);
                            LoggerUtil.logItem(responseChangePersonalDetails);
                            if (response.body() != null && responseChangePersonalDetails.getResponse().equalsIgnoreCase("Success")) {
                                showMessage(" Updated Successfully.");
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(etMobile.getText().toString(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(etFirstName.getText().toString(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(etLastName.getText().toString(), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(etEmail.getText().toString(), cross_intent));
                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(etEmail.getText().toString(), cross_intent));
                                onBackPressed();
                            } else {
                                showMessage(responseChangePersonalDetails.getMessage());
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
    }


    private boolean Validation() {
        if (etFirstName.getText().toString().isEmpty()) {
            etFirstName.setError("Please Enter Your First Name");
            return false;
        } else if (etLastName.getText().toString().isEmpty()) {
            etLastName.setError("Please Enter Your Last Name");
            return false;
        } else if (etMobile.getText().toString().isEmpty()) {
            etMobile.setError("Please Enter Your Mobile Number");
            return false;
        } else if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Please Enter Your Last Name");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }
}
