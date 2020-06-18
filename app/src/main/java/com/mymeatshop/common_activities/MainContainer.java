package com.mymeatshop.common_activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.activities.AboutUsActivity;
import com.mymeatshop.activities.AddAddressActivity;
import com.mymeatshop.activities.BulkOrderActivity;
import com.mymeatshop.activities.CartActivity;
import com.mymeatshop.activities.ContactUsActivity;
import com.mymeatshop.activities.MyProfile;
import com.mymeatshop.activities.OrderHistroy;
import com.mymeatshop.activities.OrderTrackActivity;
import com.mymeatshop.activities.PrivacyPolicy;
import com.mymeatshop.activities.SearchActivity;
import com.mymeatshop.activities.SpecialOfferActivity;
import com.mymeatshop.activities.WishList;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.fragment.Dashboard;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.model.response.ResponseLogin;
import com.mymeatshop.model.response.contactus.ContactusResponse;
import com.mymeatshop.model.response.pincode.DataItem;
import com.mymeatshop.model.response.pincode.ResponseGetPincodes;
import com.mymeatshop.utils.DialogUtil;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainContainer extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public TextView profile_name, emailTxt, BulkOrderTv, walletBalanceTxt, shopTv, orderHistoryTv, myProfileTv, wishlistTv, faqTv, aboutMeatShopTv, termAndCondTv, loginTv, contactUsTv, signOutTv;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.bot_nav_view)
    BottomNavigationView botNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.title)
    TextView title_tv;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.img_track)
    ImageView trackImg;
    @BindView(R.id.notification_bell)
    ImageView notificationBell;
    @BindView(R.id.tv_noti_count)
    TextView tvNotiCount;
    @BindView(R.id.img_contactUs)
    ImageView imgContactUs;
    @BindView(R.id.cart_counter)
    TextView cartCounter;
    boolean mLocationPermissionGranted;
    double lat = 0.0, longg = 0.0;
    View OrderView, ProfView, wish_view;
    ConstraintLayout walletView;
    private SharedPreferences pref;
    private Fragment currentFragment;
    private CircleImageView profileImag;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    Dialog dialoglog;
    private LocationManager locationManager;
    List<DataItem> pincodelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);
        ButterKnife.bind(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getLocationPermission();
        pref = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        final ConstraintLayout holder = findViewById(R.id.header);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//                float scaleFactor = 7f;
//                float slideX = drawerView.getWidth() * slideOffset;
//
//                holder.setTranslationX(slideX);
//                holder.setScaleX(1 - (slideOffset / scaleFactor));
//                holder.setScaleY(1 - (slideOffset / scaleFactor));
//
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
        };
//        Window w = getWindow();
//        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);// will remove all possible our activity's window bounds

        drawerLayout.addDrawerListener(toggle);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();

        View hView = navView.getHeaderView(0);
        findIDs(hView);
        botNavView.setOnNavigationItemSelectedListener(this);
        botNavView.getMenu().setGroupCheckable(0, false, true);
        ReplaceFragmentWithHome(new Dashboard(), "Dashboard");
        getPincodes();
    }

    public void findIDs(View view) {
        try {
            profileImag = view.findViewById(R.id.profileImag);
            profile_name = view.findViewById(R.id.profile_name);
            emailTxt = view.findViewById(R.id.emailTxt);
            walletBalanceTxt = view.findViewById(R.id.walletBalanceTxt);
            shopTv = view.findViewById(R.id.shopTv);
            orderHistoryTv = view.findViewById(R.id.orderHistoryTv);
            myProfileTv = view.findViewById(R.id.myProfileTv);
            BulkOrderTv = view.findViewById(R.id.BulkOrderTv);
            wishlistTv = view.findViewById(R.id.wishlistTv);
            OrderView = view.findViewById(R.id.order_view);
            ProfView = view.findViewById(R.id.prof_view);
            walletView = view.findViewById(R.id.walletView);
            wish_view = view.findViewById(R.id.wish_view);
            faqTv = view.findViewById(R.id.faqTv);
            aboutMeatShopTv = view.findViewById(R.id.aboutMeatShopTv);
            termAndCondTv = view.findViewById(R.id.termAndCondTv);
            contactUsTv = view.findViewById(R.id.contactUsTv);
            signOutTv = view.findViewById(R.id.signOutTv);
            loginTv = view.findViewById(R.id.loginTv);

            profile_name.setOnClickListener(this);
            emailTxt.setOnClickListener(this);
            walletBalanceTxt.setOnClickListener(this);
            shopTv.setOnClickListener(this);
            orderHistoryTv.setOnClickListener(this);
            myProfileTv.setOnClickListener(this);
            BulkOrderTv.setOnClickListener(this);
            wishlistTv.setOnClickListener(this);
            faqTv.setOnClickListener(this);
            aboutMeatShopTv.setOnClickListener(this);
            termAndCondTv.setOnClickListener(this);
            contactUsTv.setOnClickListener(this);
            signOutTv.setOnClickListener(this);
            loginTv.setOnClickListener(this);
            if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                signOutTv.setVisibility(View.VISIBLE);
                loginTv.setVisibility(View.GONE);
                orderHistoryTv.setVisibility(View.VISIBLE);
                wishlistTv.setVisibility(View.VISIBLE);
                myProfileTv.setVisibility(View.VISIBLE);
                walletView.setVisibility(View.VISIBLE);
                OrderView.setVisibility(View.VISIBLE);
                ProfView.setVisibility(View.VISIBLE);
                wish_view.setVisibility(View.VISIBLE);
            } else {
                signOutTv.setVisibility(View.GONE);
                loginTv.setVisibility(View.VISIBLE);
                orderHistoryTv.setVisibility(View.GONE);
                wishlistTv.setVisibility(View.GONE);
                myProfileTv.setVisibility(View.GONE);
                walletView.setVisibility(View.GONE);
                OrderView.setVisibility(View.GONE);
                wish_view.setVisibility(View.GONE);
                ProfView.setVisibility(View.GONE);
            }
            /*if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                loginTv.setVisibility(View.GONE);

            }else {
                loginTv.setVisibility(View.VISIBLE);
            }*/
            if (PreferencesManager.getInstance(context).getPINCODE().equals("")) {
                pincodeDialog(context);
            }

            emailTxt.setText(Cons.decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent));
            profile_name.setText(String.format("%s %s", Cons.decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent),
                    Cons.decryptMsg(PreferencesManager.getInstance(context).getLNAME(), cross_intent)));

            if (NetworkUtils.getConnectivityStatus(context) != 0) {
                if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                    //Call guest Api
                    getGuestCartItem();

                } else {
                    //Call user Api
                    getCartItem();
                }
            } else {
                showMessage(getResources().getString(R.string.alert_internet));
            }

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }
    public void logoutFromFacebook() {

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // user already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

    public void getGuestCartItem() {
        try {

            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getGuestCartItem(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        PreferencesManager.getInstance(context).setCartcount("0");
                        updateCounter();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounter();
        getLocation();
        getLocationPermission();
    }
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
            getDeviceLocation();
        } else {
            showSettingsAlert("GPS is Disabled in your device");
        }
    }
    private void showSettingsAlert(String provider) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainContainer.this);
        alertDialog.setTitle(provider);
        alertDialog.setMessage("GPS is not enabled! Want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                goToActivityWithFinish(context, MainContainer.class, null);
            } else {
                showSettingsAlert("GPS is Disabled in your device");
            }
        });
        alertDialog.show();
    }

    public void getCartItem() {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getCartItem(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
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
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCounter() {
        cartCounter.setText(String.valueOf(PreferencesManager.getInstance(context).getCartcount()));
    }

    @Override
    public void onClick(View v) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        switch (v.getId()) {
            case R.id.profile_name:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.emailTxt:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.walletBalanceTxt:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.shopTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.orderHistoryTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    goToActivity(MainContainer.this, OrderHistroy.class, null);
                } else {
                    if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                        DialogUtil.loginDialog(context);
                    } else {
                        Toast.makeText(context, "Please Login to view Order History", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.myProfileTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    goToActivity(MainContainer.this, MyProfile.class, null);
                } else {
                    if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                        DialogUtil.loginDialog(context);
                    } else {
                        Toast.makeText(context, "Please Login to view Profile", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.wishlistTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    goToActivity(MainContainer.this, WishList.class, null);
                } else {
                    if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                        DialogUtil.loginDialog(context);
                    } else {
                        Toast.makeText(context, "Please Login to view Wishlist", Toast.LENGTH_SHORT).show();
                    }

                }

                break;
            case R.id.faqTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.BulkOrderTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                goToActivity(MainContainer.this, BulkOrderActivity.class, null);
                break;
            case R.id.aboutMeatShopTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                goToActivity(MainContainer.this, AboutUsActivity.class, null);
                break;
            case R.id.termAndCondTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                goToActivity(MainContainer.this, PrivacyPolicy.class, null);
                break;
            case R.id.contactUsTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                goToActivity(MainContainer.this, ContactUsActivity.class, null);
                break;
            case R.id.loginTv:
                drawerLayout.closeDrawer(GravityCompat.START);
               /* GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
                googleSignInClient.signOut();*/
                logoutFromFacebook();
                signOut();
                goToActivity(MainContainer.this, LoginActivity.class, null);
                break;
            case R.id.signOutTv:
                drawerLayout.closeDrawer(GravityCompat.START);
                logoutDialog(context, LoginActivity.class);
                break;
        }
    }

    public void logoutDialog(final Context context, final Class activity) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Logout");
        builder1.setMessage("Do you really want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    MainContainer.this.clearPrefrenceforTokenExpiry();
                    Intent intent1 = new Intent(context, activity);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                    MainContainer.this.finish();
                    cartCounter.setText("0");
                    PreferencesManager.getInstance(context).setCartcount("0");

                   /* GoogleSignInOptions gso = new GoogleSignInOptions.
                            Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                            build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, gso);
                    googleSignInClient.signOut();*/
                    signOut();
                    logoutFromFacebook();
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void getPincodes() {
        try {
            showLoading();
            Call<JsonObject> loginCall = apiServices_shoopping.getPincodes();
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            hideLoading();
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseGetPincodes responseCartItem = new Gson().fromJson(paramResponse, ResponseGetPincodes.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null) {
                                pincodelist.addAll(responseCartItem.getData());
                            } else {
                                showMessage("Something went wrong.");
                            }
                        } else {
                            hideLoading();
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoading();

                        showMessage("No data found!");

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LoggerUtil.logItem(t.getMessage());
                    hideLoading();
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
    }
    public void pincodeDialog(Context context) {
        TextView cancelPin, error_msg, setPin;
        EditText pineditText;
        dialoglog = new Dialog(context, R.style.DialogSlideAnim);
        dialoglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglog.setContentView(R.layout.pincode_dialog);
        Window window = dialoglog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialoglog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        pineditText = dialoglog.findViewById(R.id.pincode_et);
        cancelPin = dialoglog.findViewById(R.id.cancel_pin);
        error_msg = dialoglog.findViewById(R.id.error_msg);
        setPin = dialoglog.findViewById(R.id.set_pin);
        setPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i <pincodelist.size() ; i++) {
                    if(pineditText.getText().toString().trim().contains(pincodelist.get(i).getPincode())){
                        error_msg.setVisibility(View.GONE);
                        try {
                            PreferencesManager.getInstance(context).setPINCODE(Cons.encryptMsg(pineditText.getText().toString(), cross_intent));
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        dialoglog.dismiss();
                    }else {
                        error_msg.setVisibility(View.VISIBLE);
                    }
                }


             /*   if (!pineditText.getText().toString().trim().contains("226010")) {
                    if (!pineditText.getText().toString().trim().contains("226016")) {
                        if (!pineditText.getText().toString().trim().contains("226012")) {
                            error_msg.setVisibility(View.VISIBLE);

                        } else {
                            error_msg.setVisibility(View.GONE);
                            try {
                                PreferencesManager.getInstance(context).setPINCODE(Cons.encryptMsg(pineditText.getText().toString(), cross_intent));
                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                            }
                            dialoglog.dismiss();
                        }

                    } else {
                        error_msg.setVisibility(View.GONE);
                        try {
                            PreferencesManager.getInstance(context).setPINCODE(Cons.encryptMsg(pineditText.getText().toString(), cross_intent));
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        dialoglog.dismiss();
                    }

                } else {
                    error_msg.setVisibility(View.GONE);
                    try {
                        PreferencesManager.getInstance(context).setPINCODE(Cons.encryptMsg(pineditText.getText().toString(), cross_intent));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    dialoglog.dismiss();
                }*/


            }
        });
        cancelPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialoglog.dismiss();
            }
        });
        dialoglog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialoglog != null && dialoglog.isShowing()) {
            dialoglog.dismiss();
        }
    }

    public void ReplaceFragmentWithHome(Fragment setFragment, String title) {
        currentFragment = setFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame, setFragment, title);
        //notificationBell.setVisibility(View.VISIBLE);
        //title_tv.setText(Html.fromHtml("<medium><b>" + title.toUpperCase() + "</b></medium>"));
        // title_tv.setVisibility(View.VISIBLE);
        transaction.commit();
    }

    public void ReplaceFragment(Fragment setFragment, String title) {
        currentFragment = setFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction().addToBackStack(title);
        transaction.replace(R.id.frame, setFragment, title);
        //notificationBell.setVisibility(View.VISIBLE);
        // title_tv.setText(Html.fromHtml("<medium><b>" + title.toUpperCase() + "</b></medium>"));
        // title_tv.setVisibility(View.VISIBLE);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() < 1) {
            if (currentFragment instanceof Dashboard) {
                super.onBackPressed();
                finish();
//                moveTaskToBack(true);
            } else {
                ReplaceFragmentWithHome(new Dashboard(), "Dashboard");
            }
        } else {
            fm.popBackStack();
        }
    }

    @OnClick({R.id.img_search, R.id.notification_bell, R.id.tv_noti_count, R.id.img_contactUs, R.id.img_track})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_search:
                goToActivity(MainContainer.this, SearchActivity.class, null);
                break;
            case R.id.notification_bell:
                break;
            case R.id.tv_noti_count:
                break;
            case R.id.img_contactUs:
                break;
            case R.id.img_track:
                getCurrentOrder();
                break;
        }
    }

    @OnClick(R.id.cartLo)
    public void onViewClicked() {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
            goToActivity(MainContainer.this, CartActivity.class, null);
        } else {
            if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                DialogUtil.loginDialog(context);
            } else {
                goToActivity(MainContainer.this, CartActivity.class, null);
            }
        }
    }

    @OnClick(R.id.title)
    public void onTitleClicked() {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
            goToActivity(MainContainer.this, AddAddressActivity.class, null);
        }

    }
    private void getCurrentOrder() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                param.addProperty("order_id",PreferencesManager.getInstance(context).getOrderidGuest());
            }else {
                param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            }

            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postCurrentOrder(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseLogin responseOrderHistory = new Gson().fromJson(paramResponse, ResponseLogin.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (responseOrderHistory.getResponse().equalsIgnoreCase("Success")) {

                                String order_id = responseOrderHistory.getData().getOrder_id();
                                Bundle bundle = new Bundle();
                                bundle.putString("order_id",order_id);
                                goToActivity(MainContainer.this, OrderTrackActivity.class, bundle);

                            }else {
                                showMessage("You have no current orders");
                            }
                        } else {
                            showMessage("You have no current orders");
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

    public void getAddress(Context context, double LATITUDE, double LONGITUDE) {

        String TAG = "ADDRESS";
//Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


//                String address = addresses.get(0).getAddressLine(0);
                String[] address = addresses.get(0).getAddressLine(0).split(",");
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String address1 = addresses.get(0).getAddressLine(1);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                StringBuilder addressStr = new StringBuilder();
                for (int i = 0; i < address.length - 2; i++) {
                    addressStr.append(",").append(address[i]);
                }

                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  address" + address1);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);
                Log.d(TAG, "getAddress:  knownName" + addresses.get(0).getSubLocality());
                Log.e("TAG", "getAddress:  knownName" + addresses.get(0).getSubLocality());
                Log.e("TAG", "getAddress:  knownName" + addresses.get(0).getSubAdminArea());
                Log.e("TAG", "getAddress:  knownName" + addresses.get(0).getMaxAddressLineIndex());

                String address_str = addressStr.toString().replaceFirst(",", "").trim();
                title_tv.setText(Html.fromHtml("<medium><b>" + city.toUpperCase() + "</b></medium>"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()&& task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            lat = mLastKnownLocation.getLatitude();
                            longg = mLastKnownLocation.getLongitude();
                            getAddress(MainContainer.this, lat, longg);
                        } else {
                            getLocationPermission();
                            Log.d("MAP", "Current location is null. Using defaults.");
                            Log.e("MAP", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
        getDeviceLocation();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_spl:
                goToActivity(MainContainer.this, SpecialOfferActivity.class, null);
                return true;
            case R.id.navigation_wallet:
                return true;
            case R.id.navigation_orders:
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    goToActivity(MainContainer.this, OrderHistroy.class, null);
                } else {
                    if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                        DialogUtil.loginDialog(context);
                    } else {
                        Toast.makeText(context, "Please Login to view Order History", Toast.LENGTH_SHORT).show();
                    }

                }
                return true;
            case R.id.navigation_help:
                goToActivity(MainContainer.this, ContactUsActivity.class, null);
                return true;
        }
        return false;
    }
}
