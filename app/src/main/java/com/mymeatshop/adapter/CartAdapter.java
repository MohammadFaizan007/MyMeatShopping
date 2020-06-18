package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mymeatshop.R;
import com.mymeatshop.activities.CartActivity;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.request.CartItem;
import com.mymeatshop.model.request.RequestUpdateCart;
import com.mymeatshop.model.response.CartitemsItem;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.utils.LoggerUtil;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context mContext;
    private List<CartitemsItem> categoryArrayList;

    public CartAdapter(Context activity, List<CartitemsItem> subCategoryList) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {
        holder.itemNameTv.setText(categoryArrayList.get(listPosition).getName());
        holder.itemRateTv.setText((String.format("₹ %s", categoryArrayList.get(listPosition).getPrice())));
        holder.categoryNameTv.setText(categoryArrayList.get(listPosition).getCatName());
        holder.amountTv.setText((String.format("₹ %s", String.valueOf((float) categoryArrayList.get(listPosition).getPrice() * (float) categoryArrayList.get(listPosition).getQty()))));
        holder.qtyBox.setValue(categoryArrayList.get(listPosition).getQty());

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private void getCartUpdate(int pos, int quantity) {
        try {
            ((CartActivity) mContext).showLoading();
            RequestUpdateCart requestCartUpdate = new RequestUpdateCart();
            requestCartUpdate.setItemId(categoryArrayList.get(pos).getItemId());
            requestCartUpdate.setToken(Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((CartActivity) mContext).cross_intent));
            CartItem cartItem = new CartItem();
            cartItem.setQty(quantity);
            cartItem.setQuoteId(Cons.decryptMsg(PreferencesManager.getInstance(mContext).getCartid(), ((CartActivity) mContext).cross_intent));
            cartItem.setSku(categoryArrayList.get(pos).getSku());
            requestCartUpdate.setCartItem(cartItem);

            Gson gson = new Gson();
            String json = gson.toJson(requestCartUpdate);
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(json);

            Call<JsonObject> loginCall = ((CartActivity) mContext).apiServices_shoopping.getCartUpdate(((CartActivity) mContext).bodyParam(jo), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((CartActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword response_addtocart;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((CartActivity) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            response_addtocart = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (response_addtocart.getResponse().equalsIgnoreCase("Success")) {
//                                ((CartActivity) mContext).showToastS("Added Successfully");
                                ((CartActivity) mContext).getCartItem(false);
                            } else {
                                ((CartActivity) mContext).getCartItem(false);
                                ((CartActivity) mContext).showToastS(response_addtocart.getMessage());
                            }
                        } else if (response.code() == 403) {
                            ((CartActivity) mContext).clearPrefrenceforTokenExpiry();
                            ((CartActivity) mContext).getAndroidId();
                            ((CartActivity) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((CartActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((CartActivity) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    private void getDeleteCartItem(int id, int pos) {
        try {
            ((CartActivity) mContext).showLoading();
            JsonObject jsonObject = new JsonObject();
            if (!PreferencesManager.getInstance(mContext).getGuestToken().equals("")) {
                jsonObject.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getQid(), ((CartActivity) mContext).cross_intent));
            } else {
                jsonObject.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getCartid(), ((CartActivity) mContext).cross_intent));
                jsonObject.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getAuthToken(), ((CartActivity) mContext).cross_intent));
            }

            jsonObject.addProperty("item_id", id);
            Call<JsonObject> loginCall = ((CartActivity) mContext).apiServices_shoopping.getDeleteCartItem(((CartActivity) mContext).bodyParam(jsonObject), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((CartActivity) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword response_addtocart;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((CartActivity) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            response_addtocart = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (response_addtocart.getResponse().equalsIgnoreCase("Success")) {
//                                ((CartActivity) mContext).showToastS("Added Successfully");
                                if (!PreferencesManager.getInstance(mContext).getGuestToken().equals("")) {
                                    ((CartActivity) mContext).getGuestCartItem(true);
                                } else {
                                    ((CartActivity) mContext).getCartItem(true);
                                }


                                notifyItemRemoved(pos);
                                categoryArrayList.remove(pos);
                            }/* else {
                                ((CartActivity) mContext).refresh();
                                ((CartActivity) mContext).showToastS(response_addtocart.getMessage());
                            }*/
                        } else if (response.code() == 403) {
                            ((CartActivity) mContext).clearPrefrenceforTokenExpiry();
                            ((CartActivity) mContext).getAndroidId();
                            ((CartActivity) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((CartActivity) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((CartActivity) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.itemNameTv)
        TextView itemNameTv;
        @BindView(R.id.itemRateTv)
        TextView itemRateTv;
        @BindView(R.id.categoryNameTv)
        TextView categoryNameTv;
        @BindView(R.id.amountTv)
        TextView amountTv;
        @BindView(R.id.qtyBox)
        NumberPicker qtyBox;
        @BindView(R.id.remove_btn)
        TextView removeBtn;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

            qtyBox.setValueChangedListener((value, action) -> {

                if (!PreferencesManager.getInstance(mContext).getUSERID().equals("")) {
                    if (action == ActionEnum.INCREMENT) {
                        getCartUpdate(getAdapterPosition(), qtyBox.getValue());
                    } else if (action == ActionEnum.DECREMENT) {
                        if (qtyBox.getValue() >= 1) {
                            getCartUpdate(getAdapterPosition(), qtyBox.getValue());
                        } else {
                            getDeleteCartItem(categoryArrayList.get(getAdapterPosition()).getItemId(), getAdapterPosition());
                        }
                    }
                } else {
                    if (!PreferencesManager.getInstance(mContext).getGuestToken().equals("")) {
                        if (action == ActionEnum.INCREMENT) {
                            getGuestEditCart("1", categoryArrayList.get(getAdapterPosition()).getSku());
                        } else if (action == ActionEnum.DECREMENT) {
                            if (qtyBox.getValue() >= 1) {
                                getGuestEditDelCart(categoryArrayList.get(getAdapterPosition()).getQty(),
                                        categoryArrayList.get(getAdapterPosition()).getItemId(),
                                        String.valueOf((float) categoryArrayList.get(getAdapterPosition()).getPrice() * (float) categoryArrayList.get(getAdapterPosition()).getQty()));
                            } else {
                                getDeleteCartItem(categoryArrayList.get(getAdapterPosition()).getItemId(), getAdapterPosition());
                            }
                        }
                    }

                }


            });
            removeBtn.setOnClickListener(view1 -> getDeleteCartItem(categoryArrayList.get(getAdapterPosition()).getItemId(), getAdapterPosition()));
        }

        private void getGuestEditDelCart(int qty, int item_id, String price) {
            try {
                (( CartActivity) mContext).showLoading();
                JsonObject param = new JsonObject();
                param.addProperty("item_id", item_id);
                param.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getQid(), ((CartActivity) mContext).cross_intent));
                param.addProperty("qty", qty);
                param.addProperty("price", price);
                LoggerUtil.logItem(param);
                Call<JsonObject> loginCall = ((CartActivity) mContext).apiServices_shoopping.getGuestDeleteItem(((CartActivity) mContext).bodyParam(param));
                loginCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        ((CartActivity) mContext).hideLoading();
                        LoggerUtil.logItem(response.body());
                        LoggerUtil.logItem(response.code());
                        try {
                            if (response.isSuccessful()) {
                                // showToastS(responseAddtoCart.getMessage());
                                ((CartActivity) mContext).getGuestCartItem(false);
                            } else {
                                ((CartActivity) mContext).showMessage("Something went wrong.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        ((CartActivity) mContext).hideLoading();
                        LoggerUtil.logItem(t.getMessage());
                    }
                });
            } catch (Error | Exception e) {
                ((CartActivity) mContext).hideLoading();
                e.printStackTrace();
            }
        }

        private void getGuestEditCart(String qty, String sku) {
            try {
                (( CartActivity) mContext).showLoading();
                JsonObject param = new JsonObject();
                JsonObject cart = new JsonObject();
                cart.addProperty("sku", sku);
                cart.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getGuestToken(), ((CartActivity) mContext).cross_intent));
                cart.addProperty("qty", qty);
                param.add("cartItem", cart);
                LoggerUtil.logItem(param);
                Call<JsonObject> loginCall = ((CartActivity) mContext).apiGuest_Shopping.getGuestEditCart(param, Cons.decryptMsg(PreferencesManager.getInstance(mContext).getGuestToken(), ((CartActivity) mContext).cross_intent));
                loginCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        ((CartActivity) mContext).hideLoading();
                        LoggerUtil.logItem(response.body());
                        LoggerUtil.logItem(response.code());
                        try {
                            if (response.isSuccessful()) {
                                // showToastS(responseAddtoCart.getMessage());
                                ((CartActivity) mContext).getGuestCartItem(false);
                            } else {
                                ((CartActivity) mContext).showMessage("Something went wrong.");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        ((CartActivity) mContext).hideLoading();
                        LoggerUtil.logItem(t.getMessage());
                    }
                });
            } catch (Error | Exception e) {
                ((CartActivity) mContext).hideLoading();
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View v) {
//            Activity activity = (Activity) mContext;
//            Bundle bundle = new Bundle();
//            bundle.putString("id", String.valueOf(categoryArrayList.get(getAdapterPosition()).getId()));
//            bundle.putString("name", categoryArrayList.get(getAdapterPosition()).getName());
//            Intent intent = new Intent(mContext, CategoryProductActivity.class);
//            intent.putExtra(PAYLOAD_BUNDLE, bundle);
//            activity.startActivity(intent);
//            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        }
    }

}