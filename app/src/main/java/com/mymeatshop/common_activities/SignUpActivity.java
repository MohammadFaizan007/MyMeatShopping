package com.mymeatshop.common_activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseLogin;
import com.mymeatshop.model.response.ResponseSignUp;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignUpActivity extends BaseActivity {


    @BindView(R.id.sign_up_btn)
    Button signUpBtn;
    @BindView(R.id.alreadyMemberTv)
    TextView alreadyMemberTv;
    @BindView(R.id.imageView10)
    ImageView imageView10;

    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.first_name_et)
    TextInputEditText firstNameEt;
    @BindView(R.id.first_nameTextInput)
    TextInputLayout firstNameTextInput;
    @BindView(R.id.last_name_et)
    TextInputEditText lastNameEt;
    @BindView(R.id.lasr_nameTextInput)
    TextInputLayout lasrNameTextInput;
    @BindView(R.id.email_et)
    TextInputEditText emailEt;
    @BindView(R.id.email_TextInput)
    TextInputLayout emailTextInput;
    @BindView(R.id.phone_et)
    TextInputEditText phoneEt;
    @BindView(R.id.phone_TextInput)
    TextInputLayout phoneTextInput;
    @BindView(R.id.password_et)
    TextInputEditText passwordEt;
    @BindView(R.id.password_TextInput)
    TextInputLayout passwordTextInput;
    String first_name_st, last_name_st, email_st, phoneno_st, password_st, tokenid, type;
    Bundle param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.sign_up);
            ButterKnife.bind(this);

            param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
            if (param != null) {
                firstNameEt.setText(param.getString("firstname"));
                lastNameEt.setText(param.getString("lastname"));
                emailEt.setText(param.getString("email"));
                tokenid = param.getString("id");
                type = param.getString("type");
                passwordTextInput.setVisibility(View.GONE);
                textView8.setVisibility(View.INVISIBLE);
                imageView10.setVisibility(View.GONE);
                alreadyMemberTv.setVisibility(View.GONE);
                signUpBtn.setText("CONTINUE");
            }


        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.sign_up_btn, R.id.alreadyMemberTv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up_btn:
                if (signUpBtn.getText().toString().equalsIgnoreCase("CONTINUE")) {
                    if (socialValidation()) {
                        if (NetworkUtils.getConnectivityStatus(context) != 0) {
                            getSocialRegister();
                        } else {
                            showMessage(getResources().getString(R.string.alert_internet));
                        }
                    }
                } else {
                    if (Validation()) {
                        if (NetworkUtils.getConnectivityStatus(context) != 0) {
                            getSignup();
                        } else {
                            showMessage(getResources().getString(R.string.alert_internet));
                        }
                    }
                }

                break;
            case R.id.alreadyMemberTv:
                Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
                break;
        }
    }

    private void alertdialog(String from) {
        AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
        // Setting Dialog Title
        alertDialog.setTitle("Alert");
        // Setting Dialog Message
        alertDialog.setMessage("You are successfully registered");
        alertDialog.setCancelable(false);

        alertDialog.setButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //goToActivityWithFinish(SignUpActivity.this, LoginActivity.class, null);
                if(from.equalsIgnoreCase("social")){
                    getsocialLogin();
                }else {
                    getLogin();
                }

            }
        });
        alertDialog.show();
    }

    private void getSignup() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("firstname", first_name_st);
            param.addProperty("lastname", last_name_st);
            param.addProperty("email", email_st);
            param.addProperty("mobile", phoneno_st);
            param.addProperty("password", password_st);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getSignUp(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseSignUp responseSignUp = new Gson().fromJson(paramResponse, ResponseSignUp.class);
                            LoggerUtil.logItem(responseSignUp);
                            if (responseSignUp.getResponse().equalsIgnoreCase("Success")) {
                                alertdialog("normal");
//                                goToActivityWithFinish(SignUpActivity.this, LoginActivity.class, null);
//                                getLogin();
                            } else {
                                showToastS(responseSignUp.getMessage());
                            }
                        } else if (response.code() == 403) {
                            clearPrefrenceforTokenExpiry();
                            getAndroidId();
                            goToActivityWithFinish(context, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(call.request().url());
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    private void getsocialLogin() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token_id", tokenid);
            param.addProperty("username", email_st);
            param.addProperty("password", "");
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.socialLogin(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            Gson gson = new GsonBuilder().create();
                            ResponseLogin responseLogin = gson.fromJson(paramResponse, ResponseLogin.class);
                            if (responseLogin.getResponse().equalsIgnoreCase("Success")) {
                                AppConfig.authToken = responseLogin.getData().getToken();
                                PreferencesManager.getInstance(context).setAuthToken(Cons.encryptMsg(String.valueOf(responseLogin.getData().getToken()), cross_intent));
                                LoggerUtil.logItem(AppConfig.authToken);
                                PreferencesManager.getInstance(context).setUSERID(Cons.encryptMsg(String.valueOf(responseLogin.getData().getId()), cross_intent));
                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(email_st, cross_intent));
                                PreferencesManager.getInstance(context).setPassword(Cons.encryptMsg(password_st, cross_intent));
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(responseLogin.getData().getMobile(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(responseLogin.getData().getFirstname(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(responseLogin.getData().getLastname(), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                PreferencesManager.getInstance(context).setCartid(Cons.encryptMsg(String.valueOf(responseLogin.getData().getQuoteId()), cross_intent));
                                PreferencesManager.getInstance(context).setGuestToken("");
                                goToActivity(SignUpActivity.this, MainContainer.class, null);
                                finishAffinity();
                            } else {
                                showMessage(responseLogin.getResponse());
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        showMessage("Invalid Username or Password");
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

    private void getSocialRegister() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("firstname", first_name_st);
            param.addProperty("lastname", last_name_st);
            param.addProperty("email", email_st);
            param.addProperty("mobile", phoneno_st);
            param.addProperty("token_id", tokenid);
            param.addProperty("type", type);
            param.addProperty("id", PreferencesManager.getInstance(context).getANDROIDID());
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.socialRegister(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseSignUp responseSignUp = new Gson().fromJson(paramResponse, ResponseSignUp.class);
                            LoggerUtil.logItem(responseSignUp);
                            if (responseSignUp.getResponse().equalsIgnoreCase("Success")) {
                                alertdialog("social");//                                goToActivityWithFinish(SignUpActivity.this, LoginActivity.class, null);
//                                getLogin();
                            } else {
                                showToastS(responseSignUp.getMessage());
                            }
                        } else if (response.code() == 403) {
                            clearPrefrenceforTokenExpiry();
                            getAndroidId();
                            goToActivityWithFinish(context, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(call.request().url());
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    private void getLogin() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("username", email_st);
            param.addProperty("password", password_st);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getLogin(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            Gson gson = new GsonBuilder().create();
                            ResponseLogin responseLogin = gson.fromJson(paramResponse, ResponseLogin.class);
                            if (response.body() != null && responseLogin.getResponse().equalsIgnoreCase("Success")) {
                                AppConfig.authToken = responseLogin.getData().getToken();
                                PreferencesManager.getInstance(context).setAuthToken(Cons.encryptMsg(String.valueOf(responseLogin.getData().getToken()), cross_intent));
                                LoggerUtil.logItem(AppConfig.authToken);
                                PreferencesManager.getInstance(context).setUSERID(Cons.encryptMsg(String.valueOf(responseLogin.getData().getId()), cross_intent));
                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(email_st, cross_intent));
                                PreferencesManager.getInstance(context).setPassword(Cons.encryptMsg(password_st, cross_intent));
                                PreferencesManager.getInstance(context).setGuestToken("");
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(responseLogin.getData().getMobile(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(responseLogin.getData().getFirstname(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(responseLogin.getData().getLastname(), cross_intent));
                                PreferencesManager.getInstance(context).setCartid(Cons.encryptMsg(String.valueOf(responseLogin.getData().getQuoteId()), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                goToActivity(SignUpActivity.this, MainContainer.class, null);
                                finishAffinity();
                            } else {
                                goToActivityWithFinish(context, LoginActivity.class, null);
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
        try {
            first_name_st = firstNameEt.getText().toString();
            last_name_st = lastNameEt.getText().toString();
            email_st = emailEt.getText().toString();
            phoneno_st = phoneEt.getText().toString();
            password_st = passwordEt.getText().toString();

            if (first_name_st.isEmpty()) {
                showError("Please Enter First Name", firstNameEt);
                return false;
            } else if (last_name_st.isEmpty()) {
                showError("Please Enter Last Name", lastNameEt);
                return false;
            } else if (email_st.isEmpty()) {
                showError("Please Enter Your Email", emailEt);
                return false;
            } else if (phoneno_st.length() == 0 || phoneno_st.length() != 10) {
                showError("Please Enter Your Valid Mobile Number", phoneEt);
                return false;
            } else if (password_st.isEmpty()) {
                showError("Please Enter Password", passwordEt);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    private boolean socialValidation() {
        try {
            first_name_st = firstNameEt.getText().toString();
            last_name_st = lastNameEt.getText().toString();
            email_st = emailEt.getText().toString();
            phoneno_st = phoneEt.getText().toString();
            password_st = passwordEt.getText().toString();

            if (first_name_st.isEmpty()) {
                showError("Please Enter First Name", firstNameEt);
                return false;
            } else if (last_name_st.isEmpty()) {
                showError("Please Enter Last Name", lastNameEt);
                return false;
            } else if (email_st.isEmpty()) {
                showError("Please Enter Your Email", emailEt);
                return false;
            } else if (phoneno_st.length() == 0 || phoneno_st.length() != 10) {
                showError("Please Enter Your Valid Mobile Number", phoneEt);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    private void showError(String error_st, EditText editText) {
        Dialog error_dialog = new Dialog(this);
        error_dialog.setCanceledOnTouchOutside(false);
        error_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        error_dialog.setContentView(R.layout.error_dialoge);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        error_dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        error_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView error_text = error_dialog.findViewById(R.id.error_text);
        Button ok_btn = error_dialog.findViewById(R.id.ok_btn);
        error_text.setText(error_st);
        error_dialog.show();
        ok_btn.setOnClickListener(view -> {
            error_dialog.dismiss();
            requestFocus(editText);
        });
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(getApplicationContext(), MainContainer.class);
        startActivity(intent2);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finishAffinity();
    }
}
