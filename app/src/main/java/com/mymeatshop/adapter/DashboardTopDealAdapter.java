package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import com.mymeatshop.model.response.dashboard.DayDealItemItem;
import com.mymeatshop.model.response.product_detail.ResponseAddToWishList;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.utils.AddToCartInterface;
import com.mymeatshop.utils.DialogUtil;
import com.mymeatshop.utils.LoggerUtil;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;

public class DashboardTopDealAdapter extends RecyclerView.Adapter<DashboardTopDealAdapter.ViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0.00");
    List<DayDealItemItem> itemList;
    AddToCartInterface addToCartInterface;
    private Context mContext;
    private MvpView mvp;

    public DashboardTopDealAdapter(Context context, List<DayDealItemItem> list, MvpView mvp, AddToCartInterface addToCartInterface) {
        mContext = context;
        itemList = list;
        this.mvp = mvp;
        this.addToCartInterface = addToCartInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.special_offer_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {
        holder.itemNameTv.setText(itemList.get(listPosition).getName());
        Glide.with(mContext).load(BuildConfig.BASE_URL_IMAGE + itemList.get(listPosition).getImage())
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(holder.itemImg);
        if (itemList.get(listPosition).getPrice() != null) {
            String price = formatAmount.format(Double.parseDouble(String.valueOf(itemList.get(listPosition).getPrice())));
            holder.priceTv.setText(String.format("₹ %s", price));
        }
        if (itemList.get(listPosition).getWislist().equalsIgnoreCase("true")) {
            holder.imageView3.setImageResource(R.drawable.ic_like_red);
        } else {
            holder.imageView3.setImageResource(R.drawable.ic_like_shape);
        }

        holder.priceTv.setPaintFlags(holder.priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String offer_price = formatAmount.format(Double.parseDouble(String.valueOf(itemList.get(listPosition).getOfferprice())));
        holder.offerPriceTv.setText(String.format("₹ %s", offer_price));
        holder.imageView3.setOnClickListener(view -> {
            if (!PreferencesManager.getInstance(mContext).getUSERID().equals("")) {
                if (itemList.get(listPosition).getWislist().equalsIgnoreCase("true")) {
                    if (itemList.get(listPosition).getWishlist_item_id() != null || !itemList.get(listPosition).getWishlist_item_id().equalsIgnoreCase(""))
                        getDeleteWishlistItem(Integer.parseInt(itemList.get(listPosition).getWishlist_item_id()), listPosition);
                } else {
                    getAddProductToWishlist(itemList.get(listPosition).getSku(),listPosition);
                }

            } else {
                ((BaseActivity) mContext).showToastS("Please Login As User to Add Product to Wishlist");
            }
        });
        holder.addToCart.setOnClickListener(view -> {
            if (!PreferencesManager.getInstance(mContext).getUSERID().equals("")) {
                addToCartInterface.updateCount(itemList.get(listPosition).getSku(), "1", "user");
            } else {
                if (!PreferencesManager.getInstance(mContext).getGuestToken().equals("")) {
                    addToCartInterface.updateCount(itemList.get(listPosition).getSku(), "1", "guest");
                } else {
                    DialogUtil.loginDialog(mContext);
                }

            }

        });


    }

    private void getAddProductToWishlist(String sku,int pos) {
        try {
            ((BaseActivity)mContext).showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getUSERID(), ((BaseActivity) mContext).cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((BaseActivity) mContext).apiServices_shoopping.getAddProductToWishlist(((BaseActivity) mContext).bodyParam(param), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                     ((BaseActivity)mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((BaseActivity) mContext).cross_intent);
                            ResponseAddToWishList responseAddToWishList = new Gson().fromJson(paramResponse, ResponseAddToWishList.class);
                            LoggerUtil.logItem(responseAddToWishList);
                            if (responseAddToWishList.getResponse().equalsIgnoreCase("Success")) {
                                ((BaseActivity) mContext).showToastS("Item Added to wishlist Successfully");
                                itemList.get(pos).setWislist("true");
                                itemList.get(pos).setWishlist_item_id(String.valueOf(responseAddToWishList.getData().getWishlistItemId()));
                                notifyItemChanged(pos);
                            } else {
                                ((BaseActivity) mContext).showToastS("Item Not Added");
                            }
                        } else {
                            ((BaseActivity) mContext).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                     ((BaseActivity)mContext).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((BaseActivity)mContext).hideLoading();
            e.printStackTrace();
        }
    }
    private void getDeleteWishlistItem(int item_id, int pos) {
        try {
            ((BaseActivity)mContext).showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(),  ((BaseActivity) mContext).cross_intent));
            jsonObject.addProperty("item_id", item_id);
            Call<JsonObject> loginCall =  ((BaseActivity) mContext).apiServices_shoopping.getDeleteWishlistItem( ((BaseActivity) mContext).bodyParam(jsonObject), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((BaseActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword responseDeleteWishlistItem;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(),  ((BaseActivity) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseDeleteWishlistItem = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (responseDeleteWishlistItem.getResponse().equalsIgnoreCase("Success")) {
                                itemList.get(pos).setWislist("false");
                                notifyItemChanged(pos);
                                 ((BaseActivity) mContext).showToastS("Item removed from wishlist successfully");
                            } else {
                                 ((BaseActivity) mContext).showToastS(responseDeleteWishlistItem.getMessage());
                            }
                        } else if (response.code() == 403) {
                             ((BaseActivity) mContext).clearPrefrenceforTokenExpiry();
                             ((BaseActivity) mContext).getAndroidId();
                             ((BaseActivity) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                     ((BaseActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
             ((BaseActivity) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name_tv)
        TextView itemNameTv;
        @BindView(R.id.card_main)
        CardView card_main;
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.imageView3)
        ImageView imageView3;
        @BindView(R.id.imageView4)
        ImageView addToCart;
        @BindView(R.id.priceTv)
        TextView priceTv;
        @BindView(R.id.offerPriceTv)
        TextView offerPriceTv;
        @BindView(R.id.txt_desc)
        TextView txt_desc;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            card_main.setOnClickListener(view1 -> mvp.getTopDeal(itemList.get(getAdapterPosition()).getName(), itemList.get(getAdapterPosition()).getSku()));
        }
    }

}