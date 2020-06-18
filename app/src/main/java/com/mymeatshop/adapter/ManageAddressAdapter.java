package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.activities.ManageAddress;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ResponseChangePassword;
import com.mymeatshop.model.response.address.DataItem;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.utils.LoggerUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ManageAddressAdapter extends RecyclerView.Adapter<ManageAddressAdapter.ViewHolder> {

    MvpView mvp;
    String from;
    private Context mContext;
    private List<DataItem> categoryArrayList;

    public ManageAddressAdapter(Context activity, List<DataItem> subCategoryList, MvpView mvp, String from) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
        this.mvp = mvp;
        this.from = from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {

        holder.tvName.setText(String.format("%s %s", categoryArrayList.get(listPosition).getFirstname(), categoryArrayList.get(listPosition).getLastname()));
        holder.streetTv.setText(categoryArrayList.get(listPosition).getStreet());
        holder.cityTv.setText(String.format("%s - %s", categoryArrayList.get(listPosition).getCity(), categoryArrayList.get(listPosition).getPostcode()));
        holder.stateTv.setText(categoryArrayList.get(listPosition).getRegion());
        holder.mobileNoTv.setText((String.format("Mobile No:  %s", categoryArrayList.get(listPosition).getTelephone())));

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private void getDeleteAddress(String address_id) {
        try {
            ((ManageAddress) mContext).showLoading();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(mContext).getUSERID(), ((ManageAddress) mContext).cross_intent));
            jsonObject.addProperty("address_id", address_id);
            LoggerUtil.logItem("DELETE" + jsonObject.toString());
            Call<JsonObject> loginCall = ((ManageAddress) mContext).apiServices_shoopping.getDeleteAddress(((ManageAddress) mContext).bodyParam(jsonObject), PreferencesManager.getInstance(mContext).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((ManageAddress) mContext).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    ResponseChangePassword responseDeleteWishlistItem;
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((ManageAddress) mContext).cross_intent);
                            LoggerUtil.logItem(paramResponse);
                            responseDeleteWishlistItem = new Gson().fromJson(paramResponse, ResponseChangePassword.class);
                            if (responseDeleteWishlistItem.getResponse().equalsIgnoreCase("Success")) {
                                ((ManageAddress) mContext).refresh();
                                ((ManageAddress) mContext).showToastS(responseDeleteWishlistItem.getMessage());
                            }
//                            } else {
//                                ((ManageAddress) mContext).refresh();
//                                ((ManageAddress) mContext).showToastS(responseDeleteWishlistItem.getMessage());
//                            }
                        } else if (response.code() == 403) {
                            ((ManageAddress) mContext).clearPrefrenceforTokenExpiry();
                            ((ManageAddress) mContext).getAndroidId();
                            ((ManageAddress) mContext).goToActivityWithFinish((Activity) mContext, LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoggerUtil.logItem(e.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((ManageAddress) mContext).hideLoading();
                    LoggerUtil.logItem(t.toString());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
            ((ManageAddress) mContext).hideLoading();
            LoggerUtil.logItem(e.toString());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.streetTv)
        TextView streetTv;
        @BindView(R.id.cityTv)
        TextView cityTv;
        @BindView(R.id.stateTv)
        TextView stateTv;
        @BindView(R.id.mobileNoTv)
        TextView mobileNoTv;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.radioButton)
        RadioButton radioButton;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);

            if (from.equalsIgnoreCase("my_profile")) {
                delete.setVisibility(View.VISIBLE);
                radioButton.setVisibility(View.GONE);
            } else {
                delete.setVisibility(View.GONE);
                radioButton.setVisibility(View.VISIBLE);
            }
            radioButton.setOnClickListener(view13 -> mvp.getTopDeal(String.valueOf(categoryArrayList.get(getAdapterPosition()).getAddressId()), ""));
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete:
                    getDeleteAddress(categoryArrayList.get(getAdapterPosition()).getAddressId());
                    break;
                case R.id.edit:
                    mvp.getClickPosition(getAdapterPosition(), "");
                    break;
                default:
                    mvp.getTopDeal(String.valueOf(categoryArrayList.get(getAdapterPosition()).getAddressId()), "");
                    break;

            }

        }
    }
}