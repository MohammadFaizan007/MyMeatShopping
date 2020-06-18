package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.activities.ProductDetail;
import com.mymeatshop.activities.ProductListActivity;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.request.CartItem;
import com.mymeatshop.model.request.RequestUpdateCart;
import com.mymeatshop.model.response.ProductListItem;
import com.mymeatshop.model.response.ResponseAddtoCart;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.product_detail.ResponseAddToWishList;
import com.mymeatshop.utils.DialogUtil;
import com.mymeatshop.utils.LoggerUtil;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;

import java.text.DecimalFormat;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;


public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0");
    private Context mContext;
    private List<ProductListItem> categoryArrayList;

    public ProductListAdapter(Context activity, List<ProductListItem> subCategoryList) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {

        holder.productNameTv.setText(categoryArrayList.get(listPosition).getName());
        holder.productDetailsTv.setText(categoryArrayList.get(listPosition).getSku());
        holder.priceTv.setText(String.format("₹ %s", categoryArrayList.get(listPosition).getPrice()));
        if (categoryArrayList.get(listPosition).getWislist().equalsIgnoreCase("true")) {
            holder.wishlistImg.setColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.wishlistImg.setColorFilter(mContext.getResources().getColor(R.color.text_color_light), PorterDuff.Mode.SRC_ATOP);
        }
        if (categoryArrayList.get(listPosition).getOfferprice() != null) {
            String offer_price = formatAmount.format(Double.parseDouble(String.valueOf(categoryArrayList.get(listPosition).getOfferprice())));
            holder.OfferPriceTv.setText(String.format("₹ %s", offer_price));
            holder.priceTv.setPaintFlags(holder.priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.packValueTv.setText(String.format("%s Pieces", categoryArrayList.get(listPosition).getPieces()));
        holder.grossValueTv.setText(String.format("%sgm", categoryArrayList.get(listPosition).getGross()));
        holder.netValueTv.setText(String.format("%sgm", categoryArrayList.get(listPosition).getNet()));

        Glide.with(mContext).load(BuildConfig.BASE_URL_IMAGE + categoryArrayList.get(listPosition).getGallery())
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private void getAddProductToWishlist(String sku, int pos) {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getUSERID(), ((ProductListActivity) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getAddProductToWishlist(((ProductListActivity) mContext).bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {

                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            ResponseAddToWishList responseAddToWishList = new Gson().fromJson(paramResponse, ResponseAddToWishList.class);
                            LoggerUtil.logItem(responseAddToWishList);
                            if (responseAddToWishList.getResponse().equalsIgnoreCase("Success")) {
                                categoryArrayList.get(pos).setWislist("true");
                                categoryArrayList.get(pos).setWislist_item_id(String.valueOf(responseAddToWishList.getData().getWishlistItemId()));
                                notifyItemChanged(pos);
                                ((ProductListActivity) mContext).showToastS("Item Added to wishlist Successfully");
                            } else {
                                ((ProductListActivity) mContext).showToastS("Item Not Added");
                            }
                        } else {
                            ((ProductListActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((ProductListActivity) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    private void getDeleteWishlistItem(int item_id, int pos) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((ProductListActivity) mContext).cross_intent));
            jsonObject.addProperty("item_id", item_id);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getDeleteWishlistItem(((ProductListActivity) mContext).bodyParam(jsonObject), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword responseDeleteWishlistItem;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseDeleteWishlistItem = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (responseDeleteWishlistItem.getResponse().equalsIgnoreCase("Success")) {
                                categoryArrayList.get(pos).setWislist("false");
                                notifyItemChanged(pos);
                                ((ProductListActivity) mContext).showToastS("Item removed from wishlist successfully");
                            } else {
                                ((ProductListActivity) mContext).showToastS(responseDeleteWishlistItem.getMessage());
                            }
                        } else if (response.code() == 403) {
                            ((ProductListActivity) mContext).clearPrefrenceforTokenExpiry();
                            ((ProductListActivity) mContext).getAndroidId();
                            ((ProductListActivity) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((ProductListActivity) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    private void getAddtoCart(String qty, String sku) {
        try {
            ((ProductListActivity) mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getCartid(), ((ProductListActivity) mContext).cross_intent));
            param.addProperty("qty", qty);
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((ProductListActivity) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getAddtoCart(((ProductListActivity) mContext).bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            LoggerUtil.logItem(responseAddtoCart);
                            if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {
                                ((ProductListActivity) mContext).showToastS(responseAddtoCart.getMessage());
                                getCartItem();
                            } else {
                                ((ProductListActivity) mContext).showToastS(responseAddtoCart.getMessage());
                            }
                        } else {
                            ((ProductListActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((ProductListActivity) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    private void getGuestAddToCart(String qty, String sku) {
        try {
            ((ProductListActivity) mContext).showLoading();
            JsonObject param = new JsonObject();
            JsonObject cart = new JsonObject();
            cart.addProperty("sku", sku);
            cart.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getGuestToken(), ((ProductListActivity) mContext).cross_intent));
            cart.addProperty("qty", qty);
            param.add("cartItem", cart);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiGuest_Shopping.getGuestAddToCart(param, Cons.decryptMsg(PreferencesManager.getInstance(mContext).getGuestToken(), ((ProductListActivity) mContext).cross_intent));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            ((ProductListActivity) mContext).showToastS("Product Added to Cart");
                            getGuestCartItem(true);

                        } else {
                            ((ProductListActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((ProductListActivity) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    public void getGuestCartItem(boolean bool) {
        try {
            if (bool)
                ((ProductListActivity) mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getGuestToken(), ((ProductListActivity) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getGuestCartItem(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(mContext).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(mContext).setCartcount("0");
                                updateCounter();
                                ((ProductListActivity) mContext).showMessage("No data found!");
                            }
                        } else {
                            ((ProductListActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        PreferencesManager.getInstance(mContext).setCartcount("0");
                        updateCounter();
                        ((ProductListActivity) mContext).showMessage("No data found!");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((ProductListActivity) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    private void getDecrement(int quantity, int item_id, String sku) {
        try {
            ((ProductListActivity) mContext).showLoading();
            RequestUpdateCart requestCartUpdate = new RequestUpdateCart();
            requestCartUpdate.setItemId(item_id);
            requestCartUpdate.setToken(Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((ProductListActivity) mContext).cross_intent));
            CartItem cartItem = new CartItem();
            cartItem.setQty(quantity);
            cartItem.setQuoteId(Cons.decryptMsg(PreferencesManager.getInstance(mContext).getCartid(), ((ProductListActivity) mContext).cross_intent));
            cartItem.setSku(sku);
            requestCartUpdate.setCartItem(cartItem);
            Gson gson = new Gson();
            String json = gson.toJson(requestCartUpdate);
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(json);

            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getCartUpdate(((ProductListActivity) mContext).bodyParam(jo), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword response_addtocart;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            response_addtocart = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (response_addtocart.getResponse().equalsIgnoreCase("Success")) {
                                ((ProductListActivity) mContext).showToastS(response_addtocart.getMessage());
                                getCartItem();
                            } else {
                                ((ProductListActivity) mContext).showToastS(response_addtocart.getMessage());
                            }
                        } else if (response.code() == 403) {
                            ((ProductListActivity) mContext).clearPrefrenceforTokenExpiry();
                            ((ProductListActivity) mContext).getAndroidId();
                            ((ProductListActivity) mContext).goToActivityWithFinish(((ProductListActivity) mContext), LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((ProductListActivity) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    public JsonObject bodyParam(JsonObject param) {
        SecretKey cross_intent;
        cross_intent = new SecretKeySpec(BuildConfig.MYMEATSHOP_COMPARED.getBytes(), "AES");
        JsonObject body = new JsonObject();
        try {
            body.addProperty("body", Cons.encryptMsg(param.toString(), cross_intent));
            LoggerUtil.logItem(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    private void getCartItem() {
        try {
            ((ProductListActivity) mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((ProductListActivity) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((ProductListActivity) mContext).apiServices_shoopping.getCartItem(bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ProductListActivity) mContext).cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(mContext).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(mContext).setCartcount("0");
                                updateCounter();
                            }
                        } else {
                            ((ProductListActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ProductListActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((ProductListActivity) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    public void updateCounter() {
        ((ProductListActivity) mContext).cartCounter.setText(String.valueOf(PreferencesManager.getInstance(mContext).getCartcount()));
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.productImage)
        ImageView productImage;
        @BindView(R.id.wishlistImg)
        ImageView wishlistImg;
        @BindView(R.id.productNameTv)
        TextView productNameTv;
        @BindView(R.id.productDetailsTv)
        TextView productDetailsTv;
        @BindView(R.id.packValue_tv)
        TextView packValueTv;
        @BindView(R.id.grossValue_tv)
        TextView grossValueTv;
        @BindView(R.id.netValue_tv)
        TextView netValueTv;
        @BindView(R.id.qtyBox)
        NumberPicker qtyBox;
        @BindView(R.id.price_tv)
        TextView priceTv;
        @BindView(R.id.OfferPriceTv)
        TextView OfferPriceTv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            qtyBox.setValueChangedListener((value, action) -> {
                if (!PreferencesManager.getInstance(mContext).getUSERID().equals("")) {
                    if (action == ActionEnum.INCREMENT) {
                        getAddtoCart("1", categoryArrayList.get(getAdapterPosition()).getSku());
                    } else if (action == ActionEnum.DECREMENT) {
                        if (qtyBox.getValue() >= 1) {
                            getDecrement(qtyBox.getValue(), categoryArrayList.get(getAdapterPosition()).getId(), categoryArrayList.get(getAdapterPosition()).getSku());
                        }
                    }
                } else {
                    if (PreferencesManager.getInstance(mContext).getGuestToken().equals("")) {
                        qtyBox.setValue(0);
                        DialogUtil.loginDialog(mContext);
                    } else {
                        if (action == ActionEnum.INCREMENT) {
                            getGuestAddToCart("1", categoryArrayList.get(getAdapterPosition()).getSku());
                        } else if (action == ActionEnum.DECREMENT) {
                            if (qtyBox.getValue() >= 1) {
                                getDecrement(qtyBox.getValue(), categoryArrayList.get(getAdapterPosition()).getId(), categoryArrayList.get(getAdapterPosition()).getSku());
                            }
                        }
                    }

                }

            });

            wishlistImg.setOnClickListener(view1 -> {
                if (!PreferencesManager.getInstance(mContext).getUSERID().equals("")) {
                    if (categoryArrayList.get(getAdapterPosition()).getWislist().equalsIgnoreCase("true")) {
                        if (categoryArrayList.get(getAdapterPosition()).getWislist_item_id() != null || !categoryArrayList.get(getAdapterPosition()).getWislist_item_id().equalsIgnoreCase(""))
                            getDeleteWishlistItem(Integer.parseInt(categoryArrayList.get(getAdapterPosition()).getWislist_item_id()), getAdapterPosition());
                    } else {
                        getAddProductToWishlist(categoryArrayList.get(getAdapterPosition()).getSku(), getAdapterPosition());
                    }

                } else {
                    ((ProductListActivity) mContext).showToastS("Please Login To Continue");
                }

            });
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mContext;
            Bundle bundle = new Bundle();
            bundle.putString("sku", categoryArrayList.get(getAdapterPosition()).getSku());
            bundle.putString("name", categoryArrayList.get(getAdapterPosition()).getName());
            Intent intent = new Intent(mContext, ProductDetail.class);
            intent.putExtra(PAYLOAD_BUNDLE, bundle);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        }
    }

}