package com.mymeatshop.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.MainContainer;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.track_order.TrackOrderResponse;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;

public class OrderTrackActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.order_id)
    TextView orderId;
    @BindView(R.id.order_status)
    TextView orderStatus;
    @BindView(R.id.delivery_name)
    TextView deliveryName;
    @BindView(R.id.delivery_img)
    ImageView deliveryImg;
    @BindView(R.id.delivery_call)
    ImageView deliveryCall;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    Bundle param;
    String order_id = "", phone = "";
    @BindView(R.id.delivery_address)
    TextView deliveryAddress;
    private GoogleMap mMap;
    private Marker marker, markerCurrent;
    SupportMapFragment mapFragment;
    private static final int DEFAULT_ZOOM = 11;
    Handler handler;
    Dialog dialoglog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_track);
        ButterKnife.bind(this);
        title.setText("Track Order");
        cartLo.setVisibility(View.GONE);
        param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
        order_id = param.getString("order_id");
        orderId.setText("ORDER ID : " + order_id);
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getTrackOrder(order_id);
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }

    Runnable runnable = () -> {
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getLiveLocation(order_id);
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    };

    @OnClick({R.id.side_menu, R.id.delivery_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.delivery_call:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phone));
                startActivity(dialIntent);
                break;
        }
    }

    private void getTrackOrder(String order_id) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("order_id", order_id);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postTrackOrder(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            hideLoading();
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            TrackOrderResponse responseOrderHistory = new Gson().fromJson(paramResponse, TrackOrderResponse.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (responseOrderHistory.getResponse().equalsIgnoreCase("Success")) {
                                new RequestOptions();
                                Glide.with(OrderTrackActivity.this).load(BuildConfig.BASE_URL_DELI_IMAGE +
                                        responseOrderHistory.getData().getImage())
                                        .apply(RequestOptions.circleCropTransform().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.ic_avatar_two).error(R.drawable.ic_avatar_two))
                                        .into(deliveryImg);
                                phone = responseOrderHistory.getData().getDeliveryMobile();
                                deliveryName.setText(responseOrderHistory.getData().getDeliveryName() + " is on his way. You will receive your order shortly.");
                                deliveryAddress.setText("Delivery Address : "+ responseOrderHistory.getData().getAdd());
                                // String[] latlong = responseOrderHistory.getData().getVLat().split(",");
                                String[] latlongD = responseOrderHistory.getData().getCLati().split(",");
                                // LatLng sydney = new LatLng(Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1]));
                                //  marker = mMap.addMarker(new MarkerOptions().position(sydney).title(getAddress(OrderTrackActivity.this,Double.parseDouble(latlong[0]), Double.parseDouble(latlong[1])))
                                // marker = mMap.addMarker(new MarkerOptions().position(sydney)
                                //   .icon(bitmapDescriptorFromVector(OrderTrackActivity.this, R.drawable.ic_pickup_location)));
                                // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
                                //marker.showInfoWindow();
                                LatLng sydney_ = new LatLng(Double.parseDouble(latlongD[0]), Double.parseDouble(latlongD[1]));
                                marker = mMap.addMarker(new MarkerOptions().position(sydney_).icon(bitmapDescriptorFromVector(OrderTrackActivity.this, R.drawable.ic_droplocation)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney_, DEFAULT_ZOOM));

                            }
                        } else {
                            hideLoading();
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        hideLoading();
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

    public void ratingDialog(Context context, String d_id) {
        TextView txt_orderId;
        EditText edt_desc;
        RatingBar ratingBar;
        Button btn_submit;
        dialoglog = new Dialog(context, R.style.DialogSlideAnim);
        if (!dialoglog.isShowing()) {
            dialoglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialoglog.setContentView(R.layout.customerrating);
            Window window = dialoglog.getWindow();
            window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            dialoglog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            edt_desc = dialoglog.findViewById(R.id.edt_desc);
            ratingBar = dialoglog.findViewById(R.id.ratingBar);
            btn_submit = dialoglog.findViewById(R.id.btn_submit);
            txt_orderId = dialoglog.findViewById(R.id.txt_orderId);
            txt_orderId.setText("OrderId " + order_id);
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ratingBar.getRating() != 0.0) {
                        hitDeliveryRating(order_id, String.valueOf(Math.round(ratingBar.getRating())), d_id, edt_desc.getText().toString());
                    }
                }
            });
            dialoglog.show();
        }

    }

    public String getAddress(Context context, double LATITUDE, double LONGITUDE) {

        String TAG = "ADDRESS", address_str = "";
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

                address_str = addressStr.toString().replaceFirst(",", "").trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address_str;
    }

    private void getLiveLocation(String order_id) {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("order_id", order_id);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postLiveLocation(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            handler.postDelayed(runnable, 8000);
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            TrackOrderResponse responseOrderHistory = new Gson().fromJson(paramResponse, TrackOrderResponse.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (responseOrderHistory.getResponse().equalsIgnoreCase("Success")) {
                                LatLng sydney = new LatLng(Double.parseDouble(responseOrderHistory.getData().getLatitude()),
                                        Double.parseDouble(responseOrderHistory.getData().getLongitude()));
                                // marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Lucknow"));
                                //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));
                                if (markerCurrent != null) {
                                    markerCurrent.remove();
                                }
                                if (responseOrderHistory.getData().getStatus().equalsIgnoreCase("Received")) {
                                    orderStatus.setText("Order Picked");
                                } else {
                                    orderStatus.setText("Order Delivered");
                                    ratingDialog(OrderTrackActivity.this, responseOrderHistory.getData().getD_id());
                                }

                                markerCurrent = mMap.addMarker(new MarkerOptions()
                                        .position(sydney)
                                        // .title(getAddress(OrderTrackActivity.this,Double.parseDouble(responseOrderHistory.getData().getLatitude()), Double.parseDouble(responseOrderHistory.getData().getLongitude())))
                                        .icon(bitmapDescriptorFromVector(OrderTrackActivity.this, R.drawable.ic_delivery_boy)));

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

    private void hitDeliveryRating(String order_id, String rating, String del_id, String comment) {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("order_id", order_id);
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            param.addProperty("d_id", del_id);
            param.addProperty("rating", rating);
            param.addProperty("comment", comment);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postDeliveryRating(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            TrackOrderResponse responseOrderHistory = new Gson().fromJson(paramResponse, TrackOrderResponse.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (responseOrderHistory.getResponse().equalsIgnoreCase("Success")) {
                                showMessage("Rating submitted successfully");
                                goToActivity(OrderTrackActivity.this, MainContainer.class, null);
                                finishAffinity();
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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
       /* LatLng sydney = new LatLng(26.8467, 80.9462);
        marker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Lucknow"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, DEFAULT_ZOOM));*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
