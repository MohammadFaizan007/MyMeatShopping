package com.mymeatshop.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.OrderHistoryAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseLogin;
import com.mymeatshop.model.response.my_order.ResponseOrderHistory;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistroy extends BaseActivity {

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
    @BindView(R.id.order_id_txt)
    TextView orderIdTxt;
    @BindView(R.id.track_order_btn)
    Button trackOrderBtn;
    @BindView(R.id.current_order_constraint_layout)
    ConstraintLayout currentOrderConstraintLayout;
    String order_id="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        ButterKnife.bind(this);
        title.setText("Order History");
        /*sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);*/
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        orderRecycler.setLayoutManager(manager);

        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getOrderHistory();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }


    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter, R.id.track_order_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.searchImg:
                break;
            case R.id.badge:
                break;
            case R.id.cart_counter:
                goToActivity(OrderHistroy.this, CartActivity.class, null);
                break;
            case R.id.track_order_btn:
                Bundle bundle = new Bundle();
                bundle.putString("order_id",order_id);
                goToActivity(OrderHistroy.this, OrderTrackActivity.class, bundle);
                break;
        }
    }

    private void getOrderHistory() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("user_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getOrderHistory(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseOrderHistory responseOrderHistory = new Gson().fromJson(paramResponse, ResponseOrderHistory.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (response.body() != null && responseOrderHistory.getData().size() > 0) {
                                OrderHistoryAdapter adapter = new OrderHistoryAdapter(context, responseOrderHistory.getData());
                                orderRecycler.setAdapter(adapter);
                                getCurrentOrder();
                            } else {
                                showMessage("No data found!");
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

    private void getCurrentOrder() {
        try {

            JsonObject param = new JsonObject();
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.postCurrentOrder(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseLogin responseOrderHistory = new Gson().fromJson(paramResponse, ResponseLogin.class);
                            LoggerUtil.logItem(responseOrderHistory);
                            if (responseOrderHistory.getResponse().equalsIgnoreCase("Success")) {
                                orderIdTxt.setText("Your order is on the way");
                                order_id = responseOrderHistory.getData().getOrder_id();
                                slideUp(currentOrderConstraintLayout);
                            }else {
                                currentOrderConstraintLayout.setVisibility(View.GONE);
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
    public void slideUp(View view){
        if (view.getVisibility() == View.GONE) {
            view.startAnimation(AnimationUtils.loadAnimation(view.getContext(), R.anim.slide_up));
                view.setVisibility(View.VISIBLE);

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
    }
}
