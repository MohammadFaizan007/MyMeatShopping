package com.mymeatshop.common_activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.ResponseLogin;
import com.mymeatshop.model.response.ResponseSignUp;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String EMAIL = "email";
    @BindView(R.id.username_et)
    EditText usernameEt;
    @BindView(R.id.password_et)
    TextInputEditText passwordEt;
    @BindView(R.id.sign_in_btn)
    Button signInBtn;
    @BindView(R.id.forgot_password_tv)
    TextView forgotPasswordTv;
    @BindView(R.id.newUserTv)
    TextView newUserTv;
    AlertDialog alertDialog;
    String TAG = "GoogleLogin";
    GoogleSignInOptions gso;
    CallbackManager callbackManager;
    private String userName_st = "";
    private String password_st = "";
    private SharedPreferences pref;
    private Dialog resetPasswordDialog;
    private int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            ButterKnife.bind(this);
            pref = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);

            String forgot = getColoredSpanned("Forgot", "#458609");
            String pswd = getColoredSpanned("Password", "#000000");
            String str3 = getColoredSpanned("?", "#458609");
            forgotPasswordTv.setText(Html.fromHtml(forgot + " " + pswd + str3));
//            google login setup
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
          /*  mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();*/
        /*    boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

            if (!loggedOut) {
               // Picasso.with(this).load(Profile.getCurrentProfile().getProfilePictureUri(200, 200)).into(imageView);
                Log.d("TAG", "Username is: " + Profile.getCurrentProfile().getName());

                //Using Graph API
                getUserProfile(AccessToken.getCurrentAccessToken());
            }*/
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                            Log.d("API123", loggedIn + " ??");
                            getUserProfile(AccessToken.getCurrentAccessToken());


                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                        }
                    });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    @OnClick({R.id.sign_in_btn, R.id.forgot_password_tv, R.id.newUserTv, R.id.facebookLogin_bnt, R.id.gmailLogin_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
//                goToActivityWithFinish(LoginActivity.this, MainContainer.class, null);
                if (Validation()) {
                    if (NetworkUtils.getConnectivityStatus(context) != 0) {
                        getLogin(userName_st, password_st);
                    } else {
                        showMessage(getResources().getString(R.string.alert_internet));
                    }
                }
                break;
            case R.id.forgot_password_tv:
                openForgotPasswordDialog();
                break;
            case R.id.newUserTv:
                goToActivityWithFinish(LoginActivity.this, SignUpActivity.class, null);
                break;
            case R.id.facebookLogin_bnt:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                break;
            case R.id.gmailLogin_btn:
               /* Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);*/
                signIn();
                break;
        }
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request;
        request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = null;
                            try {
                                email = object.getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String id = object.getString("id");
                           // String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                            getsocialLogin(first_name, last_name, id, email, "facebook");

                            /*txtUsername.setText("First Name: " + first_name + "\nLast Name: " + last_name);
                            txtEmail.setText(email);*/
                            // Picasso.with(MainActivity.this).load(image_url).into(imageView);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void getLogin(String username, String pswd) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("username", username);
            param.addProperty("password", pswd);
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
                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(userName_st, cross_intent));
                                PreferencesManager.getInstance(context).setPassword(Cons.encryptMsg(password_st, cross_intent));
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(responseLogin.getData().getMobile(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(responseLogin.getData().getFirstname(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(responseLogin.getData().getLastname(), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                PreferencesManager.getInstance(context).setCartid(Cons.encryptMsg(String.valueOf(responseLogin.getData().getQuoteId()), cross_intent));
                                PreferencesManager.getInstance(context).setGuestToken("");
                                goToActivity(LoginActivity.this, MainContainer.class, null);
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

    private boolean Validation() {
        try {
            userName_st = usernameEt.getText().toString().trim();
            password_st = passwordEt.getText().toString().trim();

            if (userName_st.length() == 0) {
                userName_st = "";
                showError("Please enter user name", usernameEt);
                return false;
            }
            if (password_st.length() == 0) {
                password_st = "";
                showError(getResources().getString(R.string.enter_pswd_err), passwordEt);
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

    private void openForgotPasswordDialog() {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.forgot_pass_dialog, null);

        //Initizliaing confirm button fo dialog box and edittext of dialog box
        AppCompatButton button_getpassword = confirmDialog.findViewById(R.id.button_getpassword);
        EditText mobile_number = confirmDialog.findViewById(R.id.mobile_number);
        EditText user_id = confirmDialog.findViewById(R.id.user_id);

        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        alertDialog = alert.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(true);

        //Displaying the alert dialog
        alertDialog.show();
        button_getpassword.setOnClickListener(view -> {
            try {
                String mobileNo_st = mobile_number.getText().toString().trim();
                String user_id_st = user_id.getText().toString().trim();
                if (user_id_st.length() == 0) {
                    user_id_st = "";
                    showError("Please enter your Email Id", user_id);
                } else {
                    if (NetworkUtils.getConnectivityStatus(context) != 0) {
                        hideKeyboard();
                        getForgPassword(user_id_st);
                    } else {
                        showMessage(getResources().getString(R.string.alert_internet));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getForgPassword(String userid) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("email", userid);
            param.addProperty("template", "email_reset");
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getResetPassword(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseChangePassword responseResetPassword = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            LoggerUtil.logItem(responseResetPassword);
                            if (responseResetPassword.getResponse().equalsIgnoreCase("Success")) {
                                alertDialog.dismiss();
                                showMessage("OTP sent to your Email Id");
                                openResetPasswordDialog(userid);
                            } else {
                                showMessage("Invalid Email Id.\nPlease enter your registered Email Id");
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

    private void openResetPasswordDialog(String userid) {
        try {
            hideKeyboard();
            resetPasswordDialog = new Dialog(context);
            resetPasswordDialog.setCanceledOnTouchOutside(true);
            resetPasswordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            resetPasswordDialog.setContentView(R.layout.reset_pass_dialog);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.80);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
            resetPasswordDialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            resetPasswordDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Button button_submit = resetPasswordDialog.findViewById(R.id.button_submit);
            EditText ed_Password = resetPasswordDialog.findViewById(R.id.ed_Password);
            EditText ed_Confirm_Password = resetPasswordDialog.findViewById(R.id.ed_Confirm_Password);
            EditText ed_otp = resetPasswordDialog.findViewById(R.id.ed_otp);

            button_submit.setOnClickListener(v -> resetPasswordDialog.dismiss());
            button_submit.setOnClickListener(view -> {
                hideKeyboard();
                if (ed_otp.getText().toString().length() != 0) {
                    if (ed_Password.getText().toString().trim().length() != 0 && ed_Password.getText().toString().trim().length() >= 6) {
                        if (ed_Password.getText().toString().trim().equals(ed_Confirm_Password.getText().toString().trim())) {
                            getChangedPassword(userid, ed_otp.getText().toString(), ed_Confirm_Password.getText().toString().trim());
                        } else {
                            showMessage("New Password & Confirmed Password not matched.");
                        }
                    } else {
                        showMessage("Please input password greater then 6 characters.");
                    }
                } else {
                    showMessage("Please enter OTP");
                }
            });

            resetPasswordDialog.show();

        } catch (Exception e) {
            hideLoading();
        }

    }

    private void getChangedPassword(String userid, String otp, String password) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("email", userid);
            param.addProperty("resetToken", otp);
            param.addProperty("newPassword", password);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getForgotPassword(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseChangePassword responseResetPassword = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            LoggerUtil.logItem(responseResetPassword);
                            if (responseResetPassword.getResponse().equalsIgnoreCase("Success")) {
                                showMessage("Password Reset Successfully.\nPlease Login.");
                                resetPasswordDialog.dismiss();
                            } else {
                                showMessage(responseResetPassword.getMessage());
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


//    GOOGLE LOGIN

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleGoogleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            try {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();

                Log.e("Gmail login", "display name: " + acct.getDisplayName());

                String name = acct.getDisplayName();
                String[] nameStr = name.split("\\s+");
                String first_name = nameStr[0];
                String last_name = nameStr[1];
                String personPhotoUrl = acct.getPhotoUrl().toString();
                String email_id = acct.getEmail();
                String id = acct.getId();
                // String token = acct.getIdToken();
                String mobile_phone = "";
                getsocialLogin(first_name, last_name, id, email_id, "google");
                Log.e(TAG, "Name: " + first_name + ", email: " + email_id
                        + ", Image: " + personPhotoUrl + " ID :" + id);

                //getSignup(first_name, last_name, email_id, mobile_phone, "");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
            googleSignInClient.signOut();
            // Signed out, show unauthenticated UI.
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(">>requestCode", requestCode + "");
        if (requestCode == RC_SIGN_IN) {
//            google
          /*  GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            Log.e("Gmail login", "display name: " + acct.getDisplayName());

            String name = acct.getDisplayName();
            String[] nameStr = name.split("\\s+");
            String first_name = nameStr[0];
            String last_name = nameStr[1];
           // String personPhotoUrl = acct.getPhotoUrl().toString();
            String email_id = acct.getEmail();
            String id = acct.getId();
            // String token = acct.getIdToken();
            String mobile_phone = "";
            getsocialLogin(first_name, last_name, id, email_id, "google");
           /* Log.e(TAG, "Name: " + first_name + ", email: " + email_id
                    + ", Image: " + personPhotoUrl + " ID :" + id);*/
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    private void getsocialLogin(String first_name, String last_name, String token, String email, String type) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token_id", token);
            param.addProperty("username", email);
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
                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                PreferencesManager.getInstance(context).setPassword(Cons.encryptMsg(password_st, cross_intent));
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(responseLogin.getData().getMobile(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(responseLogin.getData().getFirstname(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(responseLogin.getData().getLastname(), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                PreferencesManager.getInstance(context).setCartid(Cons.encryptMsg(String.valueOf(responseLogin.getData().getQuoteId()), cross_intent));
                                PreferencesManager.getInstance(context).setGuestToken("");
                                goToActivity(LoginActivity.this, MainContainer.class, null);
                                finishAffinity();
                            } else {
                                if (responseLogin.getMessage().equalsIgnoreCase("1")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("firstname", first_name);
                                    bundle.putString("lastname", last_name);
                                    bundle.putString("email", email);
                                    bundle.putString("id", token);
                                    bundle.putString("type", type);
                                    goToActivity(context, SignUpActivity.class, bundle);
                                }
                                //showMessage(responseLogin.getResponse());
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

    private void getSignup(String firstName, String lastName, String emailId, String mobileNo, String pswd) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("firstname", firstName);
            param.addProperty("lastname", lastName);
            param.addProperty("email", emailId);
            param.addProperty("mobile", mobileNo);
            param.addProperty("password", pswd);
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
                                getLogin(emailId, pswd);
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

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(getApplicationContext(), MainContainer.class);
        startActivity(intent2);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        finishAffinity();
    }
}
