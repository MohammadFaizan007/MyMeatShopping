package com.mymeatshop.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.common_activities.SignUpActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.request.CartItem;
import com.mymeatshop.model.request.RequestUpdateCart;
import com.mymeatshop.model.response.ResponseAddtoCart;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.product_detail.ResponseAddToWishList;
import com.mymeatshop.model.response.product_detail.ResponseProductDetail;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;

public class ProductDetail extends BaseActivity {


    private static DecimalFormat formatAmount = new DecimalFormat("0");
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
    @BindView(R.id.item_img)
    ImageView itemImg;
    @BindView(R.id.itemNameTv)
    TextView itemNameTv;
    @BindView(R.id.productDetailsTv)
    TextView productDetailsTv;
    @BindView(R.id.packTv)
    TextView packTv;
    @BindView(R.id.grossTv)
    TextView grossTv;
    @BindView(R.id.netTv)
    TextView netTv;
    @BindView(R.id.price_tv)
    TextView priceTv;
    @BindView(R.id.qtyBox)
    NumberPicker qtyBox;
    @BindView(R.id.description_tv)
    TextView descriptionTv;
    @BindView(R.id.purchase_tv)
    TextView purchaseTv;
    @BindView(R.id.priceTotal_tv)
    TextView priceTotalTv;
    @BindView(R.id.purchase_btn)
    Button purchaseBtn;
    @BindView(R.id.wishlistImg)
    ImageView wishlistImg;
    Bundle bundle;
    @BindView(R.id.OfferPriceTv)
    TextView OfferPriceTv;
    @BindView(R.id.txtOfferPrice)
    TextView txtOfferPrice;
    @BindView(R.id.textView9)
    TextView textView9;
    private ResponseProductDetail responseProductDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_layout);
        ButterKnife.bind(this);
        bundle = getIntent().getBundleExtra(PAYLOAD_BUNDLE);
        title.setText(bundle.getString("name"));
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getProductDetail(bundle.getString("sku"));

        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
        qtyBox.setValueChangedListener((value, action) -> {
            if (action == ActionEnum.INCREMENT) {
                getAddtoCart("1", "add");
            } else if (action == ActionEnum.DECREMENT) {
                if (qtyBox.getValue() >= 1) {
                    getDecrement(qtyBox.getValue());
                }
            }
        });
    }

    private void getProductDetail(String sku) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
//            param.addProperty("sku", Cons.encryptMsg(sku, cross_intent));
            param.addProperty("sku", Cons.encryptMsg(sku, cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getProductDetail(param, PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            responseProductDetail = new Gson().fromJson(paramResponse, ResponseProductDetail.class);
                            LoggerUtil.logItem(responseProductDetail);
                            if (response.body() != null) {
                                setData(responseProductDetail);
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

    private void setData(ResponseProductDetail data) {
        try {
            Glide.with(context).load(BuildConfig.BASE_URL_IMAGE + data.getMediaGallery().get(0).getFile())
                    .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                    .into(itemImg);

            itemNameTv.setText(data.getName());
            productDetailsTv.setText(data.getShortDescription());
            priceTotalTv.setText(String.format("₹ %s", String.valueOf(data.getPrice())));
            if (data.getOfferprice() != null && !data.getOfferprice().equalsIgnoreCase("")) {
                String offer_price = formatAmount.format(Double.parseDouble(String.valueOf(data.getOfferprice())));
                txtOfferPrice.setText(String.format("₹ %s", offer_price));
                textView9.setVisibility(View.VISIBLE);
                txtOfferPrice.setVisibility(View.VISIBLE);
                priceTotalTv.setPaintFlags(priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                textView9.setVisibility(View.GONE);
                txtOfferPrice.setVisibility(View.GONE);
//                txtOfferPrice.setText(String.format("₹ %s", String.valueOf(data.getPrice())));
            }

            descriptionTv.setText(data.getDescription());
            packTv.setText(String.format("%s Pieces", data.getPieces()));
            grossTv.setText(String.format("%sgm", data.getGross()));
            netTv.setText(String.format("%sgm", data.getNet()));
            String offer_price = formatAmount.format(Double.parseDouble(String.valueOf(data.getOfferprice())));
            OfferPriceTv.setText(offer_price);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter, R.id.purchase_btn, R.id.wishlistImg})
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
                goToActivity(ProductDetail.this, CartActivity.class, null);
                break;
            case R.id.purchase_btn:
                // getAddtoCart("1", "purchase_now");
                //guestDialog(context);
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    getAddtoCart("1", "purchase_now");
                } else {
                    if (PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                        loginDialog(context);
                    } else {
                        //getGuestToken();
                        getGuestAddToCart("1", "purchase_now");
                    }
                    //  DialogUtil.loginDialog(context);

                    /*goToActivityWithFinish(ProductDetail.this, LoginActivity.class, null);
//                    goToActivityWithFinish( ActivitySplash.this, MainContainer.class, null );
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);*/
                }
                break;
            case R.id.wishlistImg:
                if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                    getAddProductToWishlist(responseProductDetail.getSku());
                } else {
                    showToastS("Please Login To Continue");
                }

                break;
        }
    }

    public void loginDialog(Context context) {
        Dialog dialoglog = new Dialog(context, R.style.DialogSlideAnim);
        dialoglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglog.setContentView(R.layout.dialog_login_signup);
        Window window = dialoglog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialoglog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        AppCompatButton btn_login = dialoglog.findViewById(R.id.btn_login);
        AppCompatButton btn_signup = dialoglog.findViewById(R.id.btn_signup);
        AppCompatButton btn_guest = dialoglog.findViewById(R.id.btn_guest);
        dialoglog.show();
        btn_login.setOnClickListener(view -> {
            dialoglog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, LoginActivity.class, null);
        });
        btn_signup.setOnClickListener(view -> {
            dialoglog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, SignUpActivity.class, null);
        });
        btn_guest.setOnClickListener(view -> {
            //Toast.makeText(context,"Feature coming soon",Toast.LENGTH_SHORT).show();
            getGuestToken();
            dialoglog.dismiss();
        });

    }

    private void getGuestToken() {
        try {
            showLoading();
            Call<JsonObject> loginCall = apiServices_shoopping.getGuestToken();
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            Log.d("DATA", "onResponse: " + response.body());

                            JsonObject jsonObject = response.body().getAsJsonObject();
                            String token = jsonObject.get("token").getAsString();
                            String q_id = jsonObject.get("q_id").getAsString();
                            PreferencesManager.getInstance(context).setGuestToken(Cons.encryptMsg(String.valueOf(token), cross_intent));
                            PreferencesManager.getInstance(context).setQid(Cons.encryptMsg(String.valueOf(q_id), cross_intent));
                            getGuestAddToCart("1", "purchase_now");
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

    private void getAddtoCart(String qty, String from) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", responseProductDetail.getSku());
            param.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getCartid(), cross_intent));
            param.addProperty("qty", qty);
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getAddtoCart(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            LoggerUtil.logItem(responseAddtoCart);
                            if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {
                                if (from.equalsIgnoreCase("purchase_now")) {
                                    goToActivity(context, CartActivity.class, null);
                                } else {
                                    showToastS(responseAddtoCart.getMessage());
                                    getCartItem();
                                    priceTotalTv.setText(String.valueOf((float) qtyBox.getValue() * (float) responseProductDetail.getPrice()));
                                }
                            } else {
                                showToastS(responseAddtoCart.getMessage());
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

    private void getGuestAddToCart(String qty, String from) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            JsonObject cart = new JsonObject();
            cart.addProperty("sku", responseProductDetail.getSku());
            cart.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            cart.addProperty("qty", qty);
            param.add("cartItem", cart);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiGuest_Shopping.getGuestAddToCart(param, Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            //String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            //ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            // LoggerUtil.logItem(responseAddtoCart);
                            //if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {
                            if (from.equalsIgnoreCase("purchase_now")) {
                                goToActivity(context, CartActivity.class, null);
                            } else {
                                // showToastS(responseAddtoCart.getMessage());
                                getCartItem();
                                priceTotalTv.setText(String.valueOf((float) qtyBox.getValue() * (float) responseProductDetail.getPrice()));
                            }
                            //  } else {
                            //     showToastS(responseAddtoCart.getMessage());
                            // }
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

    private void getDecrement(int quantity) {
        try {
            showLoading();
            RequestUpdateCart requestCartUpdate = new RequestUpdateCart();
            requestCartUpdate.setItemId(responseProductDetail.getStockItem().getItemId());
            requestCartUpdate.setToken(Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            CartItem cartItem = new CartItem();
            cartItem.setQty(quantity);
            cartItem.setQuoteId(Cons.decryptMsg(PreferencesManager.getInstance(context).getCartid(), cross_intent));
            cartItem.setSku(responseProductDetail.getSku());
            requestCartUpdate.setCartItem(cartItem);
            Gson gson = new Gson();
            String json = gson.toJson(requestCartUpdate);
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(json);

            Call<JsonObject> loginCall = apiServices_shoopping.getCartUpdate(bodyParam(jo), PreferencesManager.getInstance(context).getANDROIDID());
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
                                showToastS(response_addtocart.getMessage());
                                getCartItem();
                                priceTotalTv.setText(String.valueOf((float) qtyBox.getValue() * (float) responseProductDetail.getPrice()));
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

    private void getCartItem() {
        try {
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
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
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

    public void updateCounter() {
        cartCounter.setText(String.valueOf(PreferencesManager.getInstance(context).getCartcount()));
    }

    private void getAddProductToWishlist(String sku) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getAddProductToWishlist(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseAddToWishList responseAddToWishList = new Gson().fromJson(paramResponse, ResponseAddToWishList.class);
                            LoggerUtil.logItem(responseAddToWishList);
                            if (responseAddToWishList.getResponse().equalsIgnoreCase("Success")) {
                                showToastS("Added Successfully");
                                wishlistImg.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                            } else {
                                showToastS("Item Not Added");
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCounter();
    }
}
