package com.mymeatshop.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.request.AddressInformation;
import com.mymeatshop.model.request.GuestShipping;
import com.mymeatshop.model.request.ShippingAddress;
import com.mymeatshop.model.response.pincode.DataItem;
import com.mymeatshop.model.response.pincode.ResponseGetPincodes;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressGuestActivity extends BaseActivity implements OnMapReadyCallback {

    protected static final int PERMISSION_LOCATION_REQUEST_CODE = 14;
    private static final int DEFAULT_ZOOM = 15;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.city_et)
    TextInputEditText cityEt;
    @BindView(R.id.pinCode_et)
    TextInputEditText pinCodeEt;
    @BindView(R.id.door_no_et)
    TextInputEditText addresset;
    @BindView(R.id.locality_et)
    TextInputEditText localityEt;
    @BindView(R.id.landmark_et)
    TextInputEditText landmarkEt;
    @BindView(R.id.save_address_btn)
    Button saveAddressBtn;
    @BindView(R.id.firstName_et)
    TextInputEditText firstNameEt;
    @BindView(R.id.lastName_et)
    TextInputEditText lastNameEt;
    @BindView(R.id.Mobile_et)
    TextInputEditText MobileEt;
    @BindView(R.id.Email_et)
    TextInputEditText Email_et;
    @BindView(R.id.Email_TextInput)
    TextInputLayout Email_TextInput;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    boolean mLocationPermissionGranted;
    SupportMapFragment mapFragment;
    Bundle param;
    String from = "";
    int REQUEST_CODE = 900;
    private GoogleMap mMap;
    private Marker marker;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    List<DataItem> pincodelist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getPincodes();
        param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NotNull AppBarLayout appBarLayout) {
                return false;
            }
        });
        params.setBehavior(behavior);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        collapsingToolbar.setTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    toolbar.setTitle("Add Address");
                    isVisible = true;
                } else if (isVisible) {
                    toolbar.setTitle("");
                    isVisible = false;
                }
            }
        });
        Email_TextInput.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(26.8467, 80.9462);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Lucknow"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 30, 30);

       /* googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                changePositionSmoothly(marker, googleMap.getCameraPosition().target);
            }

        });

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                getAddress(AddAddressGuestActivity.this, marker.getPosition().latitude, marker.getPosition().longitude);
            }
        });*/
    }

    void changePositionSmoothly(Marker marker, LatLng newLatLng) {
        if (marker == null) {
            return;
        }
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 100f);
        final float[] previousStep = {0f};
        animation.setDuration(1500);
        animation.addUpdateListener(animation1 -> {
            previousStep[0] = (Float) animation1.getAnimatedValue();
            marker.setPosition(mMap.getCameraPosition().target);

        });
        animation.start();
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
        updateLocationUI();
    }

    private void updateLocationUI() {

        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

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
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            LatLng sydney = new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Lucknow"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            if (marker != null) {
                                mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                                    @Override
                                    public void onCameraMove() {
                                        changePositionSmoothly(marker, mMap.getCameraPosition().target);
                                    }

                                });

                                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                    @Override
                                    public void onCameraIdle() {
                                        getAddress(AddAddressGuestActivity.this, marker.getPosition().latitude, marker.getPosition().longitude);
                                    }
                                });
                            }
                        } else {
                            Log.d("MAP", "Current location is null. Using defaults.");
                            Log.e("MAP", "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
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
                pinCodeEt.setText(postalCode);
                cityEt.setText(city);
                addresset.setText(address_str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    @OnClick({R.id.save_address_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_address_btn:
                if (validation()) {
                    boolean match = false;
                    for (int i = 0; i < pincodelist.size(); i++) {
                        match = false;
                        if (pinCodeEt.getText().toString().trim().contains(pincodelist.get(i).getPincode())) {
                            if (NetworkUtils.getConnectivityStatus(context) != 0) {

                                getAddGuestShippingAddress();
                            } else {
                                showMessage(getResources().getString(R.string.alert_internet));
                            }
                            match = true;
                            break;
                        }
                    }
                    if (!match) {
                        showError("Pincode entered is not under our service area", pinCodeEt);
                    }

                }
                break;
        }
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

    public void getAddGuestShippingAddress() {
        try {
            showLoading();


            GuestShipping guestShipping = new GuestShipping();
            AddressInformation information = new AddressInformation();
            ShippingAddress shippingAddress = new ShippingAddress();
            shippingAddress.setRegion("UP");
            shippingAddress.setRegionId(0);
            shippingAddress.setCountryId("IN");
            shippingAddress.setCompany("MYMEATSHOP");
            shippingAddress.setTelephone(MobileEt.getText().toString());
            shippingAddress.setPostcode(pinCodeEt.getText().toString());
            shippingAddress.setCity(cityEt.getText().toString());
            shippingAddress.setFirstname(firstNameEt.getText().toString());
            shippingAddress.setLastname(lastNameEt.getText().toString());
            shippingAddress.setEmail(Email_et.getText().toString());
            shippingAddress.setPrefix("address_");
            shippingAddress.setRegionCode("UP");
            shippingAddress.setSameAsBilling(1);
            shippingAddress.setStreet(Collections.singletonList(localityEt.getText().toString()));


            ShippingAddress billingAddress = new ShippingAddress();
            billingAddress.setRegion("UP");
            billingAddress.setRegionId(0);
            billingAddress.setCountryId("IN");
            billingAddress.setCompany("MYMEATSHOP");
            billingAddress.setTelephone(MobileEt.getText().toString());
            billingAddress.setPostcode(pinCodeEt.getText().toString());
            billingAddress.setCity(cityEt.getText().toString());
            billingAddress.setFirstname(firstNameEt.getText().toString());
            billingAddress.setLastname(lastNameEt.getText().toString());
            billingAddress.setEmail(Email_et.getText().toString());
            billingAddress.setPrefix("address_");
            billingAddress.setRegionCode("UP");
            billingAddress.setSameAsBilling(1);
            billingAddress.setStreet(Collections.singletonList(localityEt.getText().toString()));


            information.setShippingAddress(shippingAddress);
            information.setBillingAddress(billingAddress);
            information.setShippingCarrierCode("freeshipping");
            information.setShippingMethodCode("freeshipping");

            guestShipping.setAddressInformation(information);
            Gson gson = new Gson();
            String json = gson.toJson(guestShipping);
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(json);


            LoggerUtil.logItem(jo);
            Call<JsonObject> loginCall = apiGuest_Shopping.getGuestShipAddress(jo, Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            /*String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            ResponseAddAddress responseAddAddress = new Gson().fromJson(paramResponse, ResponseAddAddress.class);
                            LoggerUtil.logItem(responseAddAddress);*/
                            Bundle bundle = new Bundle();
                            bundle.putString("totalItems", param.getString("totalItems"));
                            bundle.putString("finalAmount", param.getString("finalAmount"));
                            bundle.putString("firstName", firstNameEt.getText().toString());
                            bundle.putString("lastName", lastNameEt.getText().toString());
                            bundle.putString("street", localityEt.getText().toString());
                            bundle.putString("city", cityEt.getText().toString());
                            bundle.putString("email", Email_et.getText().toString());
                            bundle.putString("pincode", pinCodeEt.getText().toString());
                            bundle.putString("number", MobileEt.getText().toString());
                            goToActivity(context, Checkout.class, bundle);
                            finish();
                        } else if (response.code() == 403) {
                            hideLoading();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String addressid = data.getStringExtra("addressid");
            Intent intent = new Intent();
            intent.putExtra("addressid", addressid);
            setResult(REQUEST_CODE, intent);
            finish();
        }
    }

    private boolean validation() {
        if (firstNameEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter First Name", firstNameEt);
            return false;
        } else if (lastNameEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Last Name", lastNameEt);
            return false;
        } else if (Email_et.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Email", Email_et);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email_et.getText().toString()).matches()) {
            showError("Please Enter Valid Email", Email_et);
            return false;
        } else if (MobileEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Mobile No", MobileEt);
            return false;
        } else if (addresset.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter DoorNo/Street", addresset);
            return false;
        } else if (localityEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Locality", localityEt);
            return false;
        }/* else if (landmarkEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Landmark", landmarkEt);
            return false;
        } */ else if (pinCodeEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Pincode", pinCodeEt);
            return false;
        } /*else if (!pinCodeEt.getText().toString().trim().contains("226010")) {
            if (!pinCodeEt.getText().toString().trim().contains("226012")) {
                if(!pinCodeEt.getText().toString().trim().contains("226012")){
                    showError("Pincode entered is not under our service area", pinCodeEt);
                    return false;
                }
            }

        }*/ else if (cityEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Select Location", cityEt);
            return false;
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
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
