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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.address.add_address.ResponseAddAddress;
import com.mymeatshop.model.response.pincode.DataItem;
import com.mymeatshop.model.response.pincode.ResponseGetPincodes;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;
import com.mymeatshop.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;

public class AddAddressActivity extends BaseActivity implements OnMapReadyCallback {

    protected static final int PERMISSION_LOCATION_REQUEST_CODE = 14;
    private static final int DEFAULT_ZOOM = 15;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
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
    @BindView(R.id.place_img)
    ImageView placeImg;
    @BindView(R.id.firstName_et)
    TextInputEditText firstNameEt;
    @BindView(R.id.lastName_et)
    TextInputEditText lastNameEt;
    @BindView(R.id.Mobile_et)
    TextInputEditText MobileEt;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    boolean mLocationPermissionGranted;
    SupportMapFragment mapFragment;
    Bundle param;
    String from = "";
    int REQUEST_CODE = 900;
    private GoogleMap mMap;
    private Marker marker;
    List<DataItem> pincodelist = new ArrayList<>();
    double latitude = 0.0, longitude = 0.0;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
        }

        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        param = getIntent().getBundleExtra(PAYLOAD_BUNDLE);
        if (param != null) {
            from = param.getString("from");
        } else {
            from = "";
        }
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
        getPincodes();
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
                                        getAddress(AddAddressActivity.this, marker.getPosition().latitude, marker.getPosition().longitude);
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
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL


                StringBuilder addressStr = new StringBuilder();
                for (int i = 0; i < address.length - 2; i++) {
                    addressStr.append(",").append(address[i]);
                }

                Log.d(TAG, "getAddress:  address" + address);
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
                localityEt.setText(Utils.setDefault(addresses.get(0).getSubLocality(), ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

    public Address getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(AddAddressActivity.this);
        List<Address> address = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address == null) {
            return null;
        }
        Address location = address.get(0);
        location.getLatitude();
        location.getLongitude();

        return location;
    }

    @OnClick({R.id.save_address_btn, R.id.place_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save_address_btn:
                if (validation()) {
                    boolean match = false;
                    for (int i = 0; i < pincodelist.size(); i++) {
                        match = false;
                        if (pinCodeEt.getText().toString().trim().contains(pincodelist.get(i).getPincode())) {
                            if (NetworkUtils.getConnectivityStatus(context) != 0) {
                                Address address = getLocationFromAddress(addresset.getText().toString());
                                if (address != null) {
                                    getAddShippingAddress(address);
                                } else {
                                    showMessage("Please select address by dragging on map");
                                }
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
            case R.id.place_img:
                onSearchCalled();
                break;
        }
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //NIGERIA
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    public void getAddShippingAddress(Address latLng) {
        try {
            showLoading();
            String address_st = addresset.getText().toString() + " " + localityEt.getText().toString() + " " + landmarkEt.getText().toString();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            param.addProperty("firstname", firstNameEt.getText().toString());
            param.addProperty("lastname", lastNameEt.getText().toString());
            param.addProperty("email", Cons.decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent));
            param.addProperty("add", address_st);
            param.addProperty("city", cityEt.getText().toString());
            param.addProperty("pincode", pinCodeEt.getText().toString());
            param.addProperty("mobile", MobileEt.getText().toString());
            param.addProperty("customer_id", Integer.parseInt(Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent)));
            param.addProperty("add_id", "");
            param.addProperty("latitude", latLng.getLatitude() + "," + latLng.getLongitude());
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getAddShippingInformation(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
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
                            ResponseAddAddress responseAddAddress = new Gson().fromJson(paramResponse, ResponseAddAddress.class);
                            LoggerUtil.logItem(responseAddAddress);
                            if (!responseAddAddress.getData().get(0).getCode().equalsIgnoreCase("")) {
                                showMessage("Address Added Successfully.");
                                if (from.equalsIgnoreCase("checkout")) {
                                    Intent i = new Intent(context, ManageAddress.class);
                                    i.putExtra("from", "checkout");
                                    startActivityForResult(i, REQUEST_CODE);
                                } else {
                                    finish();
                                }
                            } else {
                                showMessage("Something went wrong.\nPlease try again.");
                            }
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
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("<><", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                Toast.makeText(AddAddressActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(AddAddressActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i("<><", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private boolean validation() {
        if (firstNameEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter First Name", firstNameEt);
            return false;
        } else if (lastNameEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Last Name", lastNameEt);
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
        } /*else if (landmarkEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Landmark", landmarkEt);
            return false;
        } */ else if (pinCodeEt.getText().toString().equalsIgnoreCase("")) {
            showError("Please Enter Pincode", pinCodeEt);
            return false;
        }/* else if (!pinCodeEt.getText().toString().trim().contains("226010")) {
            if (!pinCodeEt.getText().toString().trim().contains("226016")) {
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
