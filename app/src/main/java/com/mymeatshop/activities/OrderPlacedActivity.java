package com.mymeatshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.MainContainer;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.address.ResponseGetAddressList;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPlacedActivity extends BaseActivity {


    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.price_tv)
    TextView priceTv;
    @BindView(R.id.view_details_tv)
    TextView viewDetailsTv;
    @BindView(R.id.delivery_tv)
    TextView deliveryTv;
    @BindView(R.id.order_date_tv)
    TextView orderDateTv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.streetTv)
    TextView streetTv;
    @BindView(R.id.city_tv)
    TextView cityTv;
    @BindView(R.id.stateTv)
    TextView stateTv;
    @BindView(R.id.pincode_tv)
    TextView pincodeTv;
    @BindView(R.id.phone_number_tv)
    TextView phoneNumberTv;
    Bundle param;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_placed);
        ButterKnife.bind(this);

        param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
        String totalAmt = param.getString("finalAmount");
        String totalItems = param.getString("totalItems");
        priceTv.setText(String.format("Total price for %s items %s", totalItems, totalAmt));

        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getAddressList();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
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
                            hideLoading();
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            ResponseGetAddressList responseGetAddressList = new Gson().fromJson(paramResponse, ResponseGetAddressList.class);
                            LoggerUtil.logItem(responseGetAddressList);
                            if (response.body() != null && responseGetAddressList.getData().size() > 0) {
                                for (int i = 0; i <= responseGetAddressList.getData().size(); i++) {
                                    if (responseGetAddressList.getData().get(i).getAddressId().equalsIgnoreCase("Default")) {
                                        nameTv.setText(String.format("%s %s", responseGetAddressList.getData().get(i).getFirstname(), responseGetAddressList.getData().get(i).getLastname()));
                                        streetTv.setText(responseGetAddressList.getData().get(i).getStreet());
                                        cityTv.setText(responseGetAddressList.getData().get(i).getCity());
                                        stateTv.setText(String.format("%s  -", responseGetAddressList.getData().get(i).getRegion()));
                                        pincodeTv.setText(responseGetAddressList.getData().get(i).getPostcode());
                                        phoneNumberTv.setText(responseGetAddressList.getData().get(i).getTelephone());
                                    }
                                }
                            } else {
//                                showMessage("No data found!");
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

    @OnClick(R.id.side_menu)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(context, MainContainer.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
        OrderPlacedActivity.this.finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

}
