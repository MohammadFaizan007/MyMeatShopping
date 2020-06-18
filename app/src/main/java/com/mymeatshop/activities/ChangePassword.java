package com.mymeatshop.activities;

import android.content.Context;
import android.content.Intent;
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
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends BaseActivity {
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
    @BindView(R.id.et_old_pswd)
    TextInputEditText etOldPswd;
    @BindView(R.id.et_new_pswd)
    TextInputEditText etNewPswd;
    @BindView(R.id.et_confrm_pswd)
    TextInputEditText etConfrmPswd;
    @BindView(R.id.msg_txt)
    TextView msgTxt;
    @BindView(R.id.btn_update_password)
    Button btnUpdatePassword;
    @BindView(R.id.textInputLayoutOldPassword)
    TextInputLayout textInputLayoutOldPassword;
    @BindView(R.id.textInputLayoutNewPswd)
    TextInputLayout textInputLayoutNewPswd;
    @BindView(R.id.textInputLayoutCnfPswd)
    TextInputLayout textInputLayoutCnfPswd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        ButterKnife.bind(this);
        title.setText("Change Password");
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }

    @OnClick({R.id.side_menu, R.id.btn_update_password, R.id.badge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.btn_update_password:
                if (Validation()) {
                    if (NetworkUtils.getConnectivityStatus(context) != 0) {
                        getChangePassword();
                    } else {
                        showMessage(getResources().getString(R.string.alert_internet));
                    }
                }
                break;
            case R.id.badge:
            case R.id.cart_counter:
                goToActivity(ChangePassword.this, CartActivity.class, null);
                break;
        }
    }

    private void getChangePassword() {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("currentPassword", etOldPswd.getText().toString());
            jsonObject.addProperty("newPassword", etNewPswd.getText().toString());
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            jsonObject.addProperty("id", PreferencesManager.getInstance(context).getUSERID());
            LoggerUtil.logItem(jsonObject);
            Call<JsonObject> loginCall = apiServices_shoopping.getChangePassword(bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseChangePassword responseChangePassword = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            LoggerUtil.logItem(responseChangePassword);
                            if (response.body() != null && responseChangePassword.getResponse().equalsIgnoreCase("Success")) {
                                showMessage("Password Changed Successfully.");
                                logout(context, LoginActivity.class);
                            } else {
                                showMessage(responseChangePassword.getMessage());
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
        if (etOldPswd.getText().toString().trim().length() == 0) {
            etOldPswd.setError("Please enter old password");
            return false;
        } else if (etOldPswd.getText().toString().length() < 6) {
            etOldPswd.setError("Invalid old password");
            return false;
        } else if (etNewPswd.getText().toString().trim().length() == 0) {
            etNewPswd.setError("Please enter new password");
            return false;
        } else if (etNewPswd.getText().toString().trim().length() < 6) {
            etNewPswd.setError("Invalid new password");
            return false;
        } else if (etConfrmPswd.getText().toString().trim().length() == 0) {
            etConfrmPswd.setError("Please enter confirm password");
            return false;
        } else if (etConfrmPswd.getText().toString().trim().length() < 6) {
            etConfrmPswd.setError("Invalid confirm password");
            return false;
        } else if (!etConfrmPswd.getText().toString().trim().equalsIgnoreCase(etNewPswd.getText().toString().trim())) {
            etConfrmPswd.setError("Password not matched");
            return false;
        }
        return true;
    }

    public void logout(final Context context, final Class activity) {
        ChangePassword.this.clearPrefrenceforTokenExpiry();
        Intent intent1 = new Intent(context, activity);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
        ChangePassword.this.finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }
}
