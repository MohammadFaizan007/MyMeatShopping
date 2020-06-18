package com.mymeatshop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.CartAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.common_activities.MainContainer;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.CartitemsItem;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends BaseActivity {
    public static List<CartitemsItem> cartItemList = new ArrayList<>();
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
    @BindView(R.id.check_out_tv)
    Button checkOutTv;
    @BindView(R.id.price_tv)
    TextView priceTv;
    @BindView(R.id.items_tv)
    TextView itemsTv;
    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.checkoutLo)
    ConstraintLayout checkoutLo;
    @BindView(R.id.priceLo)
    ConstraintLayout priceLo;
    @BindView(R.id.tvemty)
    TextView tvemty;
    @BindView(R.id.remove_all)
    TextView remove_all;
    @BindView(R.id.gotoDashboard)
    TextView gotoDashboard;
    float finalAmount, courierPrice = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        ButterKnife.bind(this);
        title.setText("My Cart");

        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerCart.setLayoutManager(manager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                //Call guest Api
                getGuestCartItem(true);
            } else {
                //Call user Api
                getCartItem(true);
            }

        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }

    public void getCartItem(boolean bool) {
        try {
            if (bool)
                showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getCartItem(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            cartItemList = responseCartItem.getCartitems();
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                                showCard(true, responseCartItem);
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
                                showCard(false, responseCartItem);
                               // showMessage("No data found!");
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        PreferencesManager.getInstance(context).setCartcount("0");
                        updateCounter();
                        //showMessage("No data found!");
                        showCard(false, null);
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

    public void getGuestCartItem(boolean bool) {
        try {
            if (bool)
                showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getGuestCartItem(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            cartItemList = responseCartItem.getCartitems();
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                                showCard(true, responseCartItem);
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
                                showCard(false, responseCartItem);
                               // showMessage("No data found!");
                            }
                        } else {
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        PreferencesManager.getInstance(context).setCartcount("0");
                        updateCounter();
                       // showMessage("No data found!");
                        showCard(false, null);
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

    public void showCard(boolean show, ResponseCartItem cartList) {
        if (show) {
            CartAdapter adapter = new CartAdapter(context, cartList.getCartitems());
            recyclerCart.setAdapter(adapter);
            recyclerCart.setHasFixedSize(true);
            cartItemList = cartList.getCartitems();
            calculateTotalAmount(cartList.getCartitems());
            priceLo.setVisibility(View.VISIBLE);
            checkoutLo.setVisibility(View.VISIBLE);
            checkOutTv.setVisibility(View.VISIBLE);
            recyclerCart.setVisibility(View.VISIBLE);
            tvemty.setVisibility(View.GONE);
            gotoDashboard.setVisibility(View.GONE);
        } else {
            priceLo.setVisibility(View.GONE);
            checkoutLo.setVisibility(View.GONE);
            checkOutTv.setVisibility(View.GONE);
            recyclerCart.setVisibility(View.GONE);
            tvemty.setVisibility(View.VISIBLE);
            gotoDashboard.setVisibility(View.VISIBLE);
        }
    }

    private void calculateTotalAmount(List<CartitemsItem> cartList) {

        float ttlAmt = 0.00f;
        for (int i = 0; i < cartList.size(); i++) {
            float amount = Float.parseFloat(String.valueOf(cartList.get(i).getPrice()));
            int quantity = cartList.get(i).getQty();
            float totalAmount = amount * quantity;
            Log.e("AMOUNT", "= " + amount);
            Log.e("quantity", "= " + quantity);

            ttlAmt += totalAmount;

        }
        finalAmount = ttlAmt;
        itemsTv.setText(String.format(Locale.getDefault(), "%d Items", cartList.size()));
        priceTv.setText(String.valueOf(ttlAmt));
//        getShipmentAmount(ttlAmt);
    }

    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter, R.id.check_out_tv, R.id.gotoDashboard, R.id.remove_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.searchImg:
            case R.id.badge:
            case R.id.cart_counter:
                break;
            case R.id.check_out_tv:
                if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("finalAmount", String.valueOf(finalAmount));
                    bundle.putString("totalItems", String.valueOf(cartItemList.size()));
                    goToActivity(context, AddAddressGuestActivity.class, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("finalAmount", String.valueOf(finalAmount));
                    bundle.putString("totalItems", String.valueOf(cartItemList.size()));
                    goToActivity(context, Checkout.class, bundle);
                }

                break;
            case R.id.gotoDashboard:
                goToActivityWithFinish(CartActivity.this, MainContainer.class, null);
                break;
            case R.id.remove_all:

                if (NetworkUtils.getConnectivityStatus(context) != 0) {
                    getDeleteAllCartItem();
                } else {
                    showMessage(getResources().getString(R.string.alert_internet));
                }
                break;
        }
    }

    public void updateCounter() {
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void refresh() {
        Intent intent = getIntent();
        //overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }


    private void getDeleteAllCartItem() {
        try {
            showLoading();
            JsonObject jsonObject = new JsonObject();
            if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                jsonObject.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getQid(), cross_intent));
            } else {
                jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), ((CartActivity) context).cross_intent));
                jsonObject.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getCartid(), cross_intent));
            }

            jsonObject.addProperty("item_id", "");
            Call<JsonObject> loginCall = apiServices_shoopping.getDeleteCartItem(((CartActivity) context).bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword response_addtocart;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            response_addtocart = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (response_addtocart.getResponse().equalsIgnoreCase("Success")) {
                                getCartItem(true);
                            } else {
                                showToastS(response_addtocart.getMessage());
                            }
                        } else if (response.code() == 403) {
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
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

}
