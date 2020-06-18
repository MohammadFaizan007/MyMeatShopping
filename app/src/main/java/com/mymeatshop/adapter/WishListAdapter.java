package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.activities.ProductDetail;
import com.mymeatshop.activities.WishList;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseAddtoCart;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.WishListItem;
import com.mymeatshop.utils.LoggerUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private Context mContext;
    private List<WishListItem> arrayList;

    public WishListAdapter(Context context, List<WishListItem> arrayList) {
        this.mContext = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whislist_adapter, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameTv.setText(arrayList.get(position).getName());
        holder.priceTv.setText(String.format("₹ %s", String.valueOf(arrayList.get(position).getPrice())));
//       holder.storeNameTv.setText(arrayList.get(position).getStorename());
//       holder.storeAddressTv.setText("NA");
        Glide.with(mContext).load(BuildConfig.BASE_URL_IMAGE + arrayList.get(position).getGallery())
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(holder.itemImg);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void getDeleteWishlistItem(int wishlist_id, int item_id) {
        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((WishList) mContext).cross_intent));
            jsonObject.addProperty("item_id", item_id);
            Call<JsonObject> loginCall = ((WishList) mContext).apiServices_shoopping.getDeleteWishlistItem(((WishList) mContext).bodyParam(jsonObject), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword responseDeleteWishlistItem;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((WishList) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseDeleteWishlistItem = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (responseDeleteWishlistItem.getResponse().equalsIgnoreCase("Success")) {
                                ((WishList) mContext).getWishList();
                                ((WishList) mContext).showToastS("Item deleted successfully");
                            } else {
                                ((WishList) mContext).getWishList();
                                ((WishList) mContext).showToastS(responseDeleteWishlistItem.getMessage());
                            }
                        } else if (response.code() == 403) {
                            ((WishList) mContext).clearPrefrenceforTokenExpiry();
                            ((WishList) mContext).getAndroidId();
                            ((WishList) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((WishList) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    private void getAddtoCart(String qty, String sku, int item_id) {
        try {
            ((WishList) mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getCartid(), ((WishList) mContext).cross_intent));
            param.addProperty("qty", qty);
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((WishList) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((WishList) mContext).apiServices_shoopping.getAddtoCart(((WishList) mContext).bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((WishList) mContext).cross_intent);
                            ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            LoggerUtil.logItem(responseAddtoCart);
                            if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {
                                ((WishList) mContext).showToastS(responseAddtoCart.getMessage());
                                getCartItem();
                                getDeleteWishlistItem(0, item_id);
                            } else {
                                ((WishList) mContext).showToastS(responseAddtoCart.getMessage());
                            }
                        } else {
                            ((WishList) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((WishList) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    private void getCartItem() {
        try {
            ((WishList) mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((WishList) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((WishList) mContext).apiServices_shoopping.getCartItem(((WishList) mContext).bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((WishList) mContext).cross_intent);
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
                            ((WishList) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((WishList) mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((WishList) mContext).hideLoading();
            e.printStackTrace();
        }
    }

    public void updateCounter() {
        ((WishList) mContext).cartCounter.setText(String.valueOf(PreferencesManager.getInstance(mContext).getCartcount()));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemImg)
        ImageView itemImg;
        @BindView(R.id.delete_image)
        ImageView deleteImage;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.price_tv)
        TextView priceTv;
        @BindView(R.id.add_to_cart_tv)
        TextView addToCartTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            deleteImage.setOnClickListener(view -> {
                getDeleteWishlistItem(arrayList.get(getAdapterPosition()).getWishlistId(), arrayList.get(getAdapterPosition()).getItemId());
            });

            addToCartTv.setOnClickListener(view -> {
                getAddtoCart("1", arrayList.get(getAdapterPosition()).getSku(), arrayList.get(getAdapterPosition()).getItemId());
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("sku", arrayList.get(getAdapterPosition()).getSku());
                    bundle.putString("name", arrayList.get(getAdapterPosition()).getName());
                    Intent intent = new Intent(mContext, ProductDetail.class);
                    intent.putExtra(PAYLOAD_BUNDLE, bundle);
                    mContext.startActivity(intent);
//                    mContext.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });
        }
    }
}
