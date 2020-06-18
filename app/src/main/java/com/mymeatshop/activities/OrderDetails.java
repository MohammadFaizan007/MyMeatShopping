package com.mymeatshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.OrderDetailAdapter;
import com.mymeatshop.adapter.OrderHistoryAdapter;
import com.mymeatshop.app.AppConfig;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.order_detail.ResponseOrderDetails;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetails extends BaseActivity {
    Bundle param;
    String order_id = "";
    double tot_price=0.0;
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
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.date_txt)
    TextView dateTxt;
    @BindView(R.id.items_recycler)
    RecyclerView itemsRecycler;
    @BindView(R.id.promo_txt)
    TextView promoTxt;
    @BindView(R.id.promo_value_txt)
    TextView promoValueTxt;
    @BindView(R.id.taxes_value_txt)
    TextView taxesValueTxt;
    @BindView(R.id.delivery_txt)
    TextView deliveryTxt;
    @BindView(R.id.total_txt)
    TextView totalTxt;
    @BindView(R.id.saving_txt)
    TextView savingTxt;
    @BindView(R.id.order_txt)
    TextView orderTxt;
    @BindView(R.id.payment_txt)
    TextView paymentTxt;
    @BindView(R.id.phone_txt)
    TextView phoneTxt;
    @BindView(R.id.delivery_add_txt)
    TextView deliveryAddTxt;
    @BindView(R.id.call_txt)
    TextView callTxt;
    @BindView(R.id.button3)
    Button button3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_details);
        ButterKnife.bind(this);
        title.setText("Order Details");
        cartLo.setVisibility(View.GONE);
        param = getIntent().getBundleExtra(AppConfig.PAYLOAD_BUNDLE);
        order_id = param.getString("order_id");
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        itemsRecycler.setLayoutManager(manager);
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getOrderDetail(order_id);
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }

    private void getOrderDetail(String order_id) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("order_id", order_id);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getOrderDetails(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseOrderDetails responseOrderHistory = new Gson().fromJson(paramResponse, ResponseOrderDetails.class);
                            LoggerUtil.logItem(responseOrderHistory);

                            if (responseOrderHistory.getMessage().equalsIgnoreCase("Success")) {
                                if (response.body() != null && responseOrderHistory.getData().size() > 0) {
                                    OrderDetailAdapter adapter = new OrderDetailAdapter(context, responseOrderHistory.getData());
                                    itemsRecycler.setAdapter(adapter);
                                    promoValueTxt.setText("N/A");
                                    taxesValueTxt.setText(responseOrderHistory.getData().get(0).getBaseTaxAmount());
                                    deliveryTxt.setText("0");
                                    deliveryAddTxt.setText(responseOrderHistory.getData().get(0).getStreet());

                                    savingTxt.setText("0");
                                    orderTxt.setText(responseOrderHistory.getData().get(0).getOrderId());
                                    paymentTxt.setText("COD");
                                    dateTxt.setText(responseOrderHistory.getData().get(0).getCreatedAt());
                                    phoneTxt.setText(responseOrderHistory.getData().get(0).getTelephone());
                                    for (int i = 0; i <responseOrderHistory.getData().size() ; i++) {
                                        tot_price = tot_price+Double.parseDouble(responseOrderHistory.getData().get(i).getBaseRowTotal());
                                    }
                                    totalTxt.setText((String.format(" ₹ %s", tot_price)));
                                } else {
                                    showMessage("No record found!");
                                }
                            }
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
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
    }

    @OnClick({R.id.side_menu, R.id.button3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                break;
            case R.id.button3:
                break;
        }
    }
}
