package com.mymeatshop.activities;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.ManageAddressAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.address.ResponseGetAddressList;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;

public class ManageAddress extends BaseActivity {


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
    @BindView(R.id.order_recycler)
    RecyclerView orderRecycler;

    @BindView(R.id.firstName_et)
    EditText firstNameEt;
    @BindView(R.id.lastName_et)
    EditText lastNameEt;
    @BindView(R.id.street_et)
    EditText streetEt;
    @BindView(R.id.city_et)
    EditText cityEt;
    @BindView(R.id.region_et)
    EditText regionEt;
    @BindView(R.id.pinCode_et)
    EditText pinCodeEt;
    @BindView(R.id.mobile_et)
    EditText mobileEt;
    @BindView(R.id.update_btn)
    Button updateBtn;
    @BindView(R.id.editAddressLo)
    ConstraintLayout editAddressLo;
    ResponseGetAddressList responseGetAddressList;
    String email_st = "", addressid_st = "";
    int REQUEST_CODE = 900;
    Bundle param;
    String from = "";
    @BindView(R.id.cvAddNewAddress)
    CardView cvAddNewAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        ButterKnife.bind(this);
        title.setText("Manage Address");
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
        param = getIntent().getBundleExtra(PAYLOAD_BUNDLE);
        if (param != null) {
            from = param.getString("from");
            cvAddNewAddress.setVisibility(View.VISIBLE);
        } else {
            cvAddNewAddress.setVisibility(View.VISIBLE);
        }
        LoggerUtil.logItem(from);

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        orderRecycler.setLayoutManager(manager);

    }


    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter, R.id.update_btn, R.id.cvAddNewAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                onBackPressed();
                break;
            case R.id.searchImg:
                break;
            case R.id.badge:
            case R.id.cart_counter:
                goToActivity(ManageAddress.this, CartActivity.class, null);
                break;
            case R.id.update_btn:
                if (Validation()) {

                    if (NetworkUtils.getConnectivityStatus(context) != 0) {
                        Address address= getLocationFromAddress(streetEt.getText().toString());
                        if(address!=null){
                            getUpdateAddress(address);
                        }else {
                            showMessage("Please select address by dragging on map");
                        }

                    } else {
                        showMessage(getResources().getString(R.string.alert_internet));
                    }
                }
                break;
            case R.id.cvAddNewAddress:
                goToActivity(context, AddAddressActivity.class, null);
                break;
        }
    }
    public Address getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(ManageAddress.this);
        List<Address> address = null;
        try {
            address = coder.getFromLocationName(strAddress,5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address==null) {
            return null;
        }
        Address location=address.get(0);
        location.getLatitude();
        location.getLongitude();

        return location;
    }


    public void getAddressList() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getAddressList(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            hideLoading();
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseGetAddressList = new Gson().fromJson(paramResponse, ResponseGetAddressList.class);
                            LoggerUtil.logItem(responseGetAddressList);
                            if (response.body() != null && responseGetAddressList.getData().size() > 0) {
                                ManageAddressAdapter adapter = new ManageAddressAdapter(context, responseGetAddressList.getData(), ManageAddress.this, from);
                                orderRecycler.setAdapter(adapter);
                            } else {
                                showMessage("No Address found!");
                            }
                        } else {
                            hideLoading();
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        hideLoading();
                        showMessage("No Address found!");
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

    @Override
    public void getTopDeal(String addressid, String sku) {
        super.getTopDeal(addressid, sku);
        LoggerUtil.logItem(addressid);
        Log.d("<><<", "getTopDeal: " + "" + addressid + "<><<" + sku);
        Intent intent = new Intent();
        intent.putExtra("addressid", addressid);
        setResult(REQUEST_CODE, intent);
        finish();
    }

    @Override
    public void getClickPosition(int pos, String tag) {
        super.getClickPosition(pos, tag);
        orderRecycler.setVisibility(View.GONE);
        editAddressLo.setVisibility(View.VISIBLE);

        firstNameEt.setText(responseGetAddressList.getData().get(pos).getFirstname());
        lastNameEt.setText(responseGetAddressList.getData().get(pos).getLastname());
        streetEt.setText(responseGetAddressList.getData().get(pos).getStreet());
        cityEt.setText(responseGetAddressList.getData().get(pos).getCity());
        regionEt.setText(responseGetAddressList.getData().get(pos).getRegion());
        pinCodeEt.setText(responseGetAddressList.getData().get(pos).getPostcode());
        mobileEt.setText(responseGetAddressList.getData().get(pos).getTelephone());
        email_st = responseGetAddressList.getData().get(pos).getEmail();
        addressid_st = responseGetAddressList.getData().get(pos).getAddressId();
    }

    public void getUpdateAddress(Address address) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("firstname", firstNameEt.getText().toString());
            param.addProperty("lastname", lastNameEt.getText().toString());
            param.addProperty("email", email_st);
            param.addProperty("add", streetEt.getText().toString());
            param.addProperty("city", cityEt.getText().toString());
            param.addProperty("pincode", pinCodeEt.getText().toString());
            param.addProperty("mobile", mobileEt.getText().toString());
            param.addProperty("add_id", addressid_st);
            param.addProperty("latitude", address.getLatitude()+","+address.getLongitude());
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getUpdateAddress(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
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
                            ResponseChangePassword responseUpdateAddress = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            LoggerUtil.logItem(responseUpdateAddress);
                            if (response.body() != null && responseUpdateAddress.getResponse().equalsIgnoreCase("Success")) {
                                showMessage("Address Updated Successfully.");
                                editAddressLo.setVisibility(View.GONE);
                                orderRecycler.setVisibility(View.VISIBLE);
                                getAddressList();
                            } else {
                                showMessage(responseUpdateAddress.getMessage());
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

    private boolean Validation() {
        try {
            if (firstNameEt.getText().toString().length() == 0) {
                showError("Please enter first name", firstNameEt);
                return false;
            } else if (lastNameEt.getText().toString().length() == 0) {
                showError("Please enter last name", lastNameEt);
                return false;
            } else if (streetEt.getText().toString().length() == 0) {
                showError("Please enter street name", streetEt);
                return false;
            } else if (cityEt.getText().toString().length() == 0) {
                showError("Please enter city", cityEt);
                return false;
            } /*else if (regionEt.getText().toString().length() == 0) {
                showError("Please enter state", regionEt);
                return false;
            } */else if (pinCodeEt.getText().toString().length() == 0) {
                showError("Please enter pincode", pinCodeEt);
                return false;
            } else if (!pinCodeEt.getText().toString().trim().contains("226010")) {
                if (!pinCodeEt.getText().toString().trim().contains("226016")) {
                    if(!pinCodeEt.getText().toString().trim().contains("226012")){
                        showError("Pincode entered is not under our service area", pinCodeEt);
                        return false;
                    }
                }

            } else if (mobileEt.getText().toString().length() == 0) {
                showError("Please enter mobile no", mobileEt);
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
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getAddressList();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
