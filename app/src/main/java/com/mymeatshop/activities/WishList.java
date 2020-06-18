package com.mymeatshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.WishListAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseWishList;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishList extends BaseActivity {
    @BindView(R.id.cart_counter)
    public TextView cartCounter;
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.badge)
    ImageView badge;
    @BindView(R.id.order_recycler)
    RecyclerView orderRecycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history);
        ButterKnife.bind(this);
        title.setText("Wishlist");
        orderRecycler.setLayoutManager(new LinearLayoutManager(context));
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getWishList();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }

    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.searchImg:
                break;
            case R.id.badge:
            case R.id.cart_counter:
                goToActivity(WishList.this, CartActivity.class, null);
                break;
        }
    }

    public void getWishList() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("body", Cons.encryptMsg(Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getWishList(param, PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseWishList responseWishList = new Gson().fromJson(paramResponse, ResponseWishList.class);
                            LoggerUtil.logItem(responseWishList);
                            if (response.body() != null && responseWishList.getWishList().size() > 0) {
                                WishListAdapter adapter = new WishListAdapter(context, responseWishList.getWishList());
                                orderRecycler.setVisibility(View.VISIBLE);
                                orderRecycler.setAdapter(adapter);
                            } else {
                                orderRecycler.setVisibility(View.GONE);
                                showMessage("No data found!");
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        orderRecycler.setVisibility(View.GONE);
                        showMessage("No data found!");
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }
}
