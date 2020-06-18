package com.mymeatshop.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.MainContainer;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.constants.PayUMoneyConfig;
import com.mymeatshop.constants.UrlConstants;
import com.mymeatshop.model.response.CouponResponse;
import com.mymeatshop.model.response.ResponseOrderPlace;
import com.mymeatshop.model.response.address.DataItem;
import com.mymeatshop.model.response.address.ResponseGetAddressList;
import com.mymeatshop.model.response.address.add_address.ResponseAddAddress;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;
import com.mymeatshop.utils.Utils;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.security.GeneralSecurityException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mymeatshop.constants.PayUMoneyConfig.MERCHANT_KEY;
import static com.mymeatshop.constants.PayUMoneyConfig.MERCHANT_SALT;

public class Checkout extends BaseActivity {


    Button payBtn;
    Bundle param;
    String paymentMethod_Online = "";
    String paymentMethod_Cod = "";
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
    @BindView(R.id.changeAddress_btn)
    Button changeAddressBtn;
    @BindView(R.id.addressTv)
    TextView addressTv;
    @BindView(R.id.cityTv)
    TextView cityTv;
    @BindView(R.id.pincodeTv)
    TextView pincodeTv;
    @BindView(R.id.numberTv)
    TextView numberTv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.itemTv)
    TextView itemTv;
    @BindView(R.id.priceTv)
    TextView priceTv;
    @BindView(R.id.deliveryCharge_tv)
    TextView deliveryChargeTv;
    @BindView(R.id.totalAmountTv)
    TextView totalAmountTv;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.grossAmountTv)
    TextView grossAmountTv;
    int REQUEST_CODE = 900;
    @BindView(R.id.radioPayment)
    RadioGroup radioPayment;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.apply_btn)
    Button apply_btn;
    String transId;
    DataItem dataItem = new DataItem();
    private String deliveryOption = "Delivery Home";
    private String paymentOption = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_out);
        ButterKnife.bind(this);
        title.setText("Checkout");
        searchImg.setVisibility(View.GONE);
        cartCounter.setVisibility(View.GONE);
        badge.setVisibility(View.GONE);

        param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
        itemTv.setText(String.format("(%s items)", param.getString("totalItems")));
        totalAmountTv.setText(String.format("₹ %s", param.getString("finalAmount")));
        grossAmountTv.setText(String.format("₹ %s", param.getString("finalAmount")));
        priceTv.setText(String.format("₹ %s", param.getString("finalAmount")));
        if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
            nameTv.setText(String.format("%s %s", param.getString("firstName"), param.getString("lastName")));
            addressTv.setText(param.getString("street"));
            cityTv.setText(param.getString("city"));
            //pincodeTv.setText(" "+param.getString("pincode"));
            numberTv.setText(param.getString("number"));
            paymentMethod_Online = "pumbolt";
            paymentMethod_Cod = "cashondelivery";
            String email = param.getString("email");
            String mob = param.getString("number");
            String name = param.getString("firstName") + " " + param.getString("lastName");
            try {
                PreferencesManager.getInstance(context).setEMAIL(Cons.encryptMsg(email, cross_intent));
                PreferencesManager.getInstance(context).setNAME(Cons.encryptMsg(name, cross_intent));
                PreferencesManager.getInstance(context).setMOBILE(Cons.encryptMsg(mob, cross_intent));
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            changeAddressBtn.setVisibility(View.GONE);
        } else {
            if (NetworkUtils.getConnectivityStatus(context) != 0) {
                getAddressList();
            } else {
                showMessage(getResources().getString(R.string.alert_internet));
            }
        }

        radioPayment.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.paymentCod) {
                paymentOption = "Cod";
            } else if (checkedId == R.id.paymentOnline) {
                paymentOption = "Online";
            }

        });


    }

    private void getAddressList() {
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
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            ResponseGetAddressList responseGetAddressList = new Gson().fromJson(paramResponse, ResponseGetAddressList.class);
                            LoggerUtil.logItem(responseGetAddressList);
                            if (response.body() != null && responseGetAddressList.getData().size() > 0) {
                                UrlConstants.addressList = responseGetAddressList.getData();
                                for (int i = 0; i < responseGetAddressList.getData().size(); i++) {
                                    if (responseGetAddressList.getData().get(i).getSameAsBilling().equalsIgnoreCase("1")) {
                                        dataItem = UrlConstants.addressList.get(i);
                                        nameTv.setText(String.format("%s %s", responseGetAddressList.getData().get(i).getFirstname(), responseGetAddressList.getData().get(i).getLastname()));
                                        addressTv.setText(responseGetAddressList.getData().get(i).getStreet());
                                        cityTv.setText(responseGetAddressList.getData().get(i).getCity());
                                        pincodeTv.setText(responseGetAddressList.getData().get(i).getPostcode());
                                        numberTv.setText(responseGetAddressList.getData().get(i).getTelephone());
                                        getPaymentMethod();
                                        break;
                                    }
                                }
                            } else {
//                                goToActivity(context, AddAddressActivity.class, null);
                                Intent i = new Intent(context, AddAddressActivity.class);
                                i.putExtra("from", "checkout");
                                startActivityForResult(i, REQUEST_CODE);
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

    private void getAddressListUpdate(String addressid) {
        try {
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
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            ResponseGetAddressList responseGetAddressList = new Gson().fromJson(paramResponse, ResponseGetAddressList.class);
                            LoggerUtil.logItem(responseGetAddressList);
                            if (response.body() != null && responseGetAddressList.getData().size() > 0) {
                                UrlConstants.addressList = responseGetAddressList.getData();
                                for (int i = 0; i < UrlConstants.addressList.size(); i++) {
                                    LoggerUtil.logItem(UrlConstants.addressList.get(i).getAddressId());
                                    if (UrlConstants.addressList.get(i).getAddressId().equalsIgnoreCase(addressid)) {
                                        dataItem = UrlConstants.addressList.get(i);
                                        nameTv.setText(String.format("%s %s", UrlConstants.addressList.get(i).getFirstname(), UrlConstants.addressList.get(i).getLastname()));
                                        addressTv.setText(UrlConstants.addressList.get(i).getStreet());
                                        cityTv.setText(UrlConstants.addressList.get(i).getCity());
                                        pincodeTv.setText(UrlConstants.addressList.get(i).getPostcode());
                                        numberTv.setText(UrlConstants.addressList.get(i).getTelephone());
                                    }
                                }
                                getPaymentMethod();
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

    private void getPaymentMethod() {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            param.addProperty("firstname", dataItem.getFirstname());
            param.addProperty("lastname", dataItem.getLastname());
            param.addProperty("email", dataItem.getEmail());
            param.addProperty("add", dataItem.getStreet());
            param.addProperty("city", dataItem.getCity());
            param.addProperty("pincode", dataItem.getPostcode());
            param.addProperty("mobile", dataItem.getTelephone());
            param.addProperty("add_id", dataItem.getAddressId());
            param.addProperty("latitude", dataItem.getC_lat());
            param.addProperty("customer_id", Integer.parseInt(Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent)));
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
                            ResponseAddAddress responseAddress = new Gson().fromJson(paramResponse, ResponseAddAddress.class);
                            LoggerUtil.logItem(responseAddress);
                            paymentMethod_Online = responseAddress.getData().get(0).getCode();
                            paymentMethod_Cod = responseAddress.getData().get(1).getCode();
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        hideLoading();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LoggerUtil.logItem(t.getMessage());
                    hideLoading();
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            hideFlightLoading();
        }
    }

    @OnClick({R.id.side_menu, R.id.changeAddress_btn, R.id.btn_continue, R.id.apply_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.apply_btn:
                if (!editText2.getText().toString().equalsIgnoreCase("")) {
                    getCoupon(true, editText2.getText().toString(), param.getString("finalAmount"));
                } else {
                    Toast.makeText(this, "Please enter Coupon code", Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.changeAddress_btn:
                Intent i = new Intent(context, ManageAddress.class);
                i.putExtra("from", "checkout");
                startActivityForResult(i, REQUEST_CODE);
                break;
            case R.id.btn_continue:
                if (PreferencesManager.getInstance(context).getConfirmation().equalsIgnoreCase("")) {
                    if (!paymentMethod_Online.equalsIgnoreCase("")) {
                        if (!deliveryOption.equalsIgnoreCase("")) {
                            if (!paymentOption.equalsIgnoreCase("")) {
                                if (NetworkUtils.getConnectivityStatus(context) != 0) {
                                    if (paymentOption.equalsIgnoreCase("Cod")) {
                                        if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                                            getGuestOrderPlace();
                                        } else {
                                            getOrderPlace();
                                        }

                                    } else if (paymentOption.equalsIgnoreCase("Online")) {
                                        launchPaymentFlow();
                                    }
                                } else {
                                    showMessage(getResources().getString(R.string.alert_internet));
                                }
                            } else {
                                showMessage("Please select any payment option");
                            }
                        } else {
                            showMessage("Please select any delivery option");
                        }
                    } else {
                        showMessage("Please select any address");
                    }
                } else {
                    showMessage("Please verify your Email Id");
                }
                break;
        }
    }

    public void getCoupon(boolean bool, String code, String finalAmount) {
        try {
            if (bool)
                showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("code", code);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postCoupon(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            Log.d("fsdadf", "onResponse: " + paramResponse);
                            CouponResponse responseCartItem = new Gson().fromJson(paramResponse, CouponResponse.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (responseCartItem.getResponse().equalsIgnoreCase("Success")) {
                                String discount = responseCartItem.getMessage();
                                double finalPrice = (Double.parseDouble(discount) * Double.parseDouble(finalAmount)) / 100;
                                double totalprice = Double.parseDouble(finalAmount) - finalPrice;
                                grossAmountTv.setText(String.format("₹ %s", (int) totalprice));
                                editText2.setEnabled(false);
                                apply_btn.setClickable(false);


                            } else {
                                showMessage(responseCartItem.getMessage());
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

    private void getOrderPlace() {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            if (paymentOption.equalsIgnoreCase("Cod")) {
                jsonObject.addProperty("method", paymentMethod_Cod);
            } else if (paymentOption.equalsIgnoreCase("Online")) {
                jsonObject.addProperty("method", paymentMethod_Online);
            }
            jsonObject.addProperty("deliveryMethod", deliveryOption);
            LoggerUtil.logItem(jsonObject);
            Call<JsonObject> loginCall = apiServices_shoopping.getOrderPlace(bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            JsonObject responseOrderPlace = new Gson().fromJson(paramResponse, JsonObject.class);
                            LoggerUtil.logItem(responseOrderPlace);
                            if (responseOrderPlace.get("response").getAsString().equalsIgnoreCase("Success")) {
                                String orderId = responseOrderPlace.get("data").getAsString();
//                                {\"data\":\"19\",\"response\":\"Success\"}
                                saveRecord(orderId);
//                                successDialog();
                            } else {
                                showMessage("Something Wrong!");
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

    private void getGuestOrderPlace() {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            JsonObject cart = new JsonObject();
            if (paymentOption.equalsIgnoreCase("Cod")) {
                cart.addProperty("method", paymentMethod_Cod);
            } else if (paymentOption.equalsIgnoreCase("Online")) {
                cart.addProperty("method", paymentMethod_Online);
            }
            jsonObject.add("paymentMethod", cart);
            LoggerUtil.logItem(jsonObject);
            Call<String> loginCall = apiGuest_Shopping.getGuestOrderPlace(jsonObject, Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            loginCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String orderId = response.body().replace("\"", "");
//                                {\"data\":\"19\",\"response\":\"Success\"}
                            Log.d("sdfsdf", "onResponse: " + orderId);
                            saveRecord(orderId);
//                                successDialog();

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
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
    }

    private void saveRecord(String orderId) {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("order_id", orderId);
            jsonObject.addProperty("created_at", Utils.getTodayDatetime());
            LoggerUtil.logItem(jsonObject);
            Call<JsonObject> loginCall = apiServices_shoopping.getaddpaymentResponse(bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            ResponseOrderPlace responseOrderPlace = new Gson().fromJson(paramResponse, ResponseOrderPlace.class);
                            LoggerUtil.logItem(responseOrderPlace);
                            if (responseOrderPlace.getResponse().equalsIgnoreCase("Success")) {
                                successDialog(orderId);
                            } else {
                                showMessage(responseOrderPlace.getMessage());
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

//    private void launchPaymentFlow() {
//        try {
//            showLoading();
////            totalAmountTv.setText("1");
//            transId = System.currentTimeMillis() + "";
//
//            PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
//            payUmoneyConfig.setPayUmoneyActivityTitle("Buy " + totalAmountTv.getText().toString().replaceAll("₹ ", ""));
//
//            PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
//            builder.setAmount(totalAmountTv.getText().toString().replaceAll("₹ ", ""))// SET Dynamically
//                    .setTxnId(transId)
//                    .setPhone(Cons.decryptMsg(PreferencesManager.getInstance(context).getMOBILE(), cross_intent))
//                    .setProductName("Meat")
//                    .setFirstName(Cons.decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent))
//                    .setEmail(Cons.decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent))
//                    .setsUrl(PayUMoneyConfig.SURL)
//                    .setfUrl(PayUMoneyConfig.FURL)
//                    .setUdf1("")
//                    .setUdf2("")
//                    .setUdf3("")
//                    .setUdf4("")
//                    .setUdf5("")
//                    .setUdf6("")
//                    .setUdf7("")
//                    .setUdf8("")
//                    .setUdf9("")
//                    .setUdf10("")
//                    .setIsDebug(PayUMoneyConfig.DEBUG)
//                    .setKey(MERCHANT_KEY)
//                    .setMerchantId(PayUMoneyConfig.MERCHANT_ID);
//
//
//            String serverCalculatedHash = PayUMoneyConfig.hashCal("SHA-512", MERCHANT_KEY + "|" +
//                    transId + "|" +
//                    Double.parseDouble(totalAmountTv.getText().toString().replaceAll("₹ ", "")) + "|" + "Meat" + "|"
//                    + Cons.decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent) + "|" + Cons.decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent) + "|||||||||||" +
//                    MERCHANT_SALT);
//
//            //declare paymentParam object
//            PayUmoneySdkInitializer.PaymentParam paymentParam = builder.build();
//            //set the hash
//            paymentParam.setMerchantHash(serverCalculatedHash);
//            hideLoading();
//            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, Checkout.this, R.style.PayUMoney, false);
//
////            PayUmoneySdkInitializer.PaymentParam mPaymentParams = builder.build();
////            calculateHashInServer(mPaymentParams);
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void launchPaymentFlow() {
        try {

            transId = System.currentTimeMillis() + "";
            String productInfo = "Meat";

            PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();
            payUmoneyConfig.setPayUmoneyActivityTitle("Buy " + totalAmountTv.getText().toString().replaceAll("₹ ", ""));

            PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
            builder.setAmount(totalAmountTv.getText().toString().replaceAll("₹ ", ""))// SET Dynamically
                    .setTxnId(transId)
                    .setPhone(Cons.decryptMsg(PreferencesManager.getInstance(context).getMOBILE(), cross_intent))
                    .setProductName(productInfo)
                    .setFirstName(Cons.decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent))
                    .setEmail(Cons.decryptMsg(PreferencesManager.getInstance(context).getEMAIL(), cross_intent))
                    .setsUrl(PayUMoneyConfig.SURL)
                    .setfUrl(PayUMoneyConfig.FURL)
                    .setUdf1("")
                    .setUdf2("")
                    .setUdf3("")
                    .setUdf4("")
                    .setUdf5("")
                    .setUdf6("")
                    .setUdf7("")
                    .setUdf8("")
                    .setUdf9("")
                    .setUdf10("")
                    .setIsDebug(PayUMoneyConfig.DEBUG)
                    .setKey(MERCHANT_KEY)
                    .setMerchantId(PayUMoneyConfig.MERCHANT_ID);

            //declare paymentParam object
            PayUmoneySdkInitializer.PaymentParam paymentParam = builder.build();
            //set the hash
//            paymentParam.setMerchantHash(serverCalculatedHash);
            paymentParam = calculateServerSideHashAndInitiatePayment1(paymentParam);

            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, context, R.style.PayUMoney, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        LoggerUtil.logItem(paymentParam.getParams().toString());
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        stringBuilder.append(MERCHANT_SALT);

        String hash = Utils.hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
                TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
                ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

                if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                    if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                        if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                            getGuestOrderPlace();
                        } else {
                            getOrderPlace();
                        }
                    } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.CANCELLED)) {
                        showAlert("Payment Cancelled");
                    } else if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.FAILED)) {
                        showAlert("Payment Failed");
                    }

                } else if (resultModel != null && resultModel.getError() != null) {
                    Toast.makeText(this, "Error check log", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Both objects are null", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_CANCELED) {
                showAlert("Payment Cancelled By Customer");
            } else if (requestCode == REQUEST_CODE) {
                String addressid = data.getStringExtra("addressid");
                getAddressListUpdate(addressid);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Double convertStringToDouble(String str) {
        return Double.parseDouble(str);
    }

    public void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            if (msg.equalsIgnoreCase("Payment Successful")) {
//                getBookingDone(String.valueOf(totalAmountTv.getText().toString().replaceAll("₹ ", "")));
            } else if (msg.equalsIgnoreCase("Booking Successful"))
                finish();
        });
        alertDialog.show();
    }


    private void successDialog(String orderId) {
        Dialog dialoglog = new Dialog(context, R.style.DialogSlideAnim);
        dialoglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglog.setContentView(R.layout.confirmation_dialog);
        Window window = dialoglog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialoglog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        AppCompatButton btn_go = dialoglog.findViewById(R.id.btn_go);
        TextView msg = dialoglog.findViewById(R.id.msg);

        msg.setText("Your Order has been confirmed.");
        dialoglog.setCancelable(false);
        btn_go.setOnClickListener(view -> {
            dialoglog.dismiss();
            if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                clearPrefrenceforTokenExpiry();
                getGuestToken(context,orderId);

            } else {
                goToActivityWithFinish(Checkout.this, OrderHistroy.class, null);
            }


        });

        //Displaying the alert dialog
        dialoglog.show();
    }

    private void getGuestToken(Context context, String orderId) {
        try {
            ((BaseActivity) context).showLoading();
            Call<JsonObject> loginCall = ((BaseActivity) context).apiServices_shoopping.getGuestToken();
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            Log.d("DATA", "onResponse: " + response.body());

                            JsonObject jsonObject = response.body().getAsJsonObject();
                            String token = jsonObject.get("token").getAsString();
                            String q_id = jsonObject.get("q_id").getAsString();
                            PreferencesManager.getInstance(context).setGuestToken(Cons.encryptMsg(String.valueOf(token), ((BaseActivity) context).cross_intent));
                            PreferencesManager.getInstance(context).setQid(Cons.encryptMsg(String.valueOf(q_id), ((BaseActivity) context).cross_intent));
                            PreferencesManager.getInstance(context).setOrderidGuest(orderId);
                            goToActivityWithFinish(Checkout.this, MainContainer.class, null);
                            finishAffinity();
                        } else {
                            ((BaseActivity) context).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((BaseActivity) context).hideLoading();
            e.printStackTrace();
        }
    }

}
