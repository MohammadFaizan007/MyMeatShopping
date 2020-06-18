package com.mymeatshop.common_activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.facebook.login.Login;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseLogin;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivitySplash extends BaseActivity {

    @BindView(R.id.progressBar3)
    ProgressBar progressBar3;
    private AppUpdater updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        try {
            if (NetworkUtils.getConnectivityStatus(context) != 0) {
                checkUpdate();
            } else {
                createInfoDialog(context, "Alert!", getResources().getString(R.string.alert_internet));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate() {
        progressBar3.setVisibility(View.VISIBLE);
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this).setUpdateFrom(UpdateFrom.GOOGLE_PLAY).
                withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        progressBar3.setVisibility(View.GONE);

                        if (isUpdateAvailable) {
                            if (updater == null) {
                                updater = new AppUpdater(ActivitySplash.this).setDisplay(Display.NOTIFICATION);
                                updater.setContentOnUpdateAvailable("Update " + update.getLatestVersion() + " is available to download.");

                                updater.setButtonDoNotShowAgain("");
                                updater.setButtonDismiss(" ");
                                updater.setCancelable(false);
                                updater.start();
                            } else {
                                updater.start();
                            }
                        }

                        int SPLASH_TIME_OUT = 2000;
                        new Handler().postDelayed(() -> {
                            try {
                                goToActivityWithFinish(ActivitySplash.this, MainContainer.class, null);
                            } catch (Exception e) {
                                goToActivityWithFinish(ActivitySplash.this, Login.class, null);
                                e.printStackTrace();
                            }
                        }, SPLASH_TIME_OUT);
                    }

                    @Override

                    public void onFailed(AppUpdaterError error) {
                        //Log.d("AppUpdater Error", "Something went wrong");
                        progressBar3.setVisibility(View.GONE);

                    }

                });
        appUpdaterUtils.start();
    }

    private void getLogin() {
        try {
            progressBar3.setVisibility(View.VISIBLE);
            JsonObject param = new JsonObject();
            param.addProperty("username", decryptMsg(PreferencesManager.getInstance(context).getLoginID(), cross_intent));
            param.addProperty("password", decryptMsg(PreferencesManager.getInstance(context).getPassword(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getLogin(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        progressBar3.setVisibility(View.GONE);
                        LoggerUtil.logItem(response.body());
                        LoggerUtil.logItem(response.code());

                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            Gson gson = new GsonBuilder().create();
                            ResponseLogin responseLogin = gson.fromJson(paramResponse, ResponseLogin.class);
                            if (response.body() != null && responseLogin.getResponse().equalsIgnoreCase("Success")) {
                                AppConfig.authToken = responseLogin.getData().getToken();
                                LoggerUtil.logItem(AppConfig.authToken);
                                PreferencesManager.getInstance(context).setAuthToken(Cons.encryptMsg(String.valueOf(responseLogin.getData().getToken()), cross_intent));
                                PreferencesManager.getInstance(context).setUSERID(Cons.encryptMsg(String.valueOf(responseLogin.getData().getId()), cross_intent));
//                                PreferencesManager.getInstance(context).setLoginID(Cons.encryptMsg(PreferencesManager.getInstance(context).getLoginID(), cross_intent));
//                                PreferencesManager.getInstance(context).setPassword(Cons.encryptMsg(PreferencesManager.getInstance(context).getPassword(), cross_intent));
                                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(responseLogin.getData().getMobile(), cross_intent));
                                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(responseLogin.getData().getFirstname(), cross_intent));
                                PreferencesManager.getInstance(context).setLNAME(Cons.encryptMsg(responseLogin.getData().getLastname(), cross_intent));
                                PreferencesManager.getInstance(context).setCartid(Cons.encryptMsg(String.valueOf(responseLogin.getData().getQuoteId()), cross_intent));
                                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(responseLogin.getData().getEmail(), cross_intent));
                                goToActivityWithFinish(context, MainContainer.class, null);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                finishAffinity();
                            } else {
                                showMessage(responseLogin.getResponse());
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
                    progressBar3.setVisibility(View.GONE);
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            progressBar3.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
}
