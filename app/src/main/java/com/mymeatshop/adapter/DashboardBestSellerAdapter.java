package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.dashboard.BestSellerItemItem;
import com.mymeatshop.model.response.product_detail.ResponseAddToWishList;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.utils.AddToCartInterface;
import com.mymeatshop.utils.DialogUtil;
import com.mymeatshop.utils.LoggerUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardBestSellerAdapter extends RecyclerView.Adapter<DashboardBestSellerAdapter.MzViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0.00");
    AddToCartInterface addToCartInterface;
    private Context context;
    private List<BestSellerItemItem> shoppingslideritem;
    private MvpView mvp;

    public DashboardBestSellerAdapter(Context context, List<BestSellerItemItem> shoppingslideritem, MvpView mvp, AddToCartInterface addToCartInterface) {
        this.context = context;
        this.shoppingslideritem = shoppingslideritem;
        this.mvp = mvp;
        this.addToCartInterface = addToCartInterface;
    }


    @NotNull
    @Override
    public DashboardBestSellerAdapter.MzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MzViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardBestSellerAdapter.MzViewHolder holder, final int position) {
        if (shoppingslideritem == null || shoppingslideritem.isEmpty())
            return;

        holder.itemName.setText(shoppingslideritem.get(position).getName().trim());

        String amount = formatAmount.format(Double.parseDouble(String.valueOf(shoppingslideritem.get(position).getPrice())));
        holder.price.setText(String.format("₹ %s", amount));
        holder.txt_prevprice.setPaintFlags(holder.txt_prevprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        Glide.with(context).load(BuildConfig.BASE_URL_IMAGE + shoppingslideritem.get(position).getValue())
                .apply(new RequestOptions().placeholder(R.drawable.image_not_available)
                        .error(R.drawable.image_not_available))
                .into(holder.itemImg);
        if (shoppingslideritem.get(position).getWislist().equalsIgnoreCase("true")) {
            holder.imageView12.setImageResource(R.drawable.ic_like_red);
        } else {
            holder.imageView12.setImageResource(R.drawable.ic_like_shape);
        }
        holder.imageView12.setOnClickListener(view -> {
            if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {

                if (shoppingslideritem.get(position).getWislist().equalsIgnoreCase("true")) {
                    if (shoppingslideritem.get(position).getWishlist_item_id() != null || !shoppingslideritem.get(position).getWishlist_item_id().equalsIgnoreCase(""))
                        getDeleteWishlistItem(Integer.parseInt(shoppingslideritem.get(position).getWishlist_item_id()), position);
                } else {
                    getAddProductToWishlist(shoppingslideritem.get(position).getSku(),position);
                }


            } else {
                ((BaseActivity) context).showToastS("Please Login As User to Add Product to Wishlist");
            }
        });
        holder.imageView13.setOnClickListener(view -> {
            if (!PreferencesManager.getInstance(context).getUSERID().equals("")) {
                addToCartInterface.updateCount(shoppingslideritem.get(position).getSku(), "1", "user");
            } else {
                if (!PreferencesManager.getInstance(context).getGuestToken().equals("")) {
                    addToCartInterface.updateCount(shoppingslideritem.get(position).getSku(), "1", "guest");
                } else {
                    DialogUtil.loginDialog(context);
                }

            }
        });

    }

    private void getAddProductToWishlist(String sku,int pos) {
        try {
            ((BaseActivity)context).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), ((BaseActivity) context).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((BaseActivity) context).apiServices_shoopping.getAddProductToWishlist(((BaseActivity) context).bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((BaseActivity)context).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((BaseActivity) context).cross_intent);
                            ResponseAddToWishList responseAddToWishList = new Gson().fromJson(paramResponse, ResponseAddToWishList.class);
                            LoggerUtil.logItem(responseAddToWishList);
                            if (responseAddToWishList.getResponse().equalsIgnoreCase("Success")) {
                                ((BaseActivity) context).showToastS("Item Added to wishlist Successfully");
                                shoppingslideritem.get(pos).setWislist("true");
                                shoppingslideritem.get(pos).setWishlist_item_id(String.valueOf(responseAddToWishList.getData().getWishlistItemId()));
                                notifyItemChanged(pos);
                            } else {
                                ((BaseActivity) context).showToastS("Item Not Added");
                            }
                        } else {
                            ((BaseActivity) context).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                     ((BaseActivity)context).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
             ((BaseActivity)context).hideLoading();
            e.printStackTrace();
        }
    }
    private void getDeleteWishlistItem(int item_id, int pos) {
        try {
            ((BaseActivity)context).showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(),  ((BaseActivity) context).cross_intent));
            jsonObject.addProperty("item_id", item_id);
            Call<JsonObject> loginCall =  ((BaseActivity) context).apiServices_shoopping.getDeleteWishlistItem( ((BaseActivity) context).bodyParam(jsonObject), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword responseDeleteWishlistItem;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(),  ((BaseActivity) context).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseDeleteWishlistItem = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (responseDeleteWishlistItem.getResponse().equalsIgnoreCase("Success")) {
                                shoppingslideritem.get(pos).setWislist("false");
                                notifyItemChanged(pos);
                                ((BaseActivity) context).showToastS("Item removed from wishlist successfully");
                            } else {
                                ((BaseActivity) context).showToastS(responseDeleteWishlistItem.getMessage());
                            }
                        } else if (response.code() == 403) {
                            ((BaseActivity) context).clearPrefrenceforTokenExpiry();
                            ((BaseActivity) context).getAndroidId();
                            ((BaseActivity) context).goToActivityWithFinish((Activity) context, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((BaseActivity) context).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    @Override
    public int getItemCount() {
        if (shoppingslideritem != null) {
            return shoppingslideritem.size();
        }
        return 0;
    }


    class MzViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImg, imageView12, imageView13;
        TextView itemName, txt_prevprice;
        TextView price;
        CardView cardView;

        MzViewHolder(View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.itemImg);
            imageView12 = itemView.findViewById(R.id.imageView12);
            imageView13 = itemView.findViewById(R.id.imageView13);
            txt_prevprice = itemView.findViewById(R.id.txt_prevprice);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(view1 -> mvp.getTopDeal(shoppingslideritem.get(getAdapterPosition()).getName(), shoppingslideritem.get(getAdapterPosition()).getSku()));

        }
    }

}