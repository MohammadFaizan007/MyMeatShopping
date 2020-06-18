package com.mymeatshop.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.activities.OrderDetails;
import com.mymeatshop.model.response.my_order.DataItem;
import com.mymeatshop.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;
import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;


public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0");
    private Context mContext;
    private List<DataItem> categoryArrayList;

    public OrderHistoryAdapter(Context activity, List<DataItem> subCategoryList) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {

        holder.orderIdTv.setText(categoryArrayList.get(listPosition).getEntity_id());
        holder.itemName.setText(categoryArrayList.get(listPosition).getName());
        holder.orderOnTv.setText(String.format(":  %s", Utils.getChangeDate(categoryArrayList.get(listPosition).getCreatedAt())));

        String amount = formatAmount.format(Double.parseDouble(String.valueOf(categoryArrayList.get(listPosition).getSubtotal())));
        holder.amountPaidTv.setText((String.format(":  ₹ %s", amount)));
        String space = getColoredSpanned(":", "#000000");
//        holder.statusTv.setText(String.format(Html.fromHtml(space) + "%s", categoryArrayList.get(listPosition).getStatus()));
        if (categoryArrayList.get(listPosition).getStatus().contains("Delivered")) {
            String status = getColoredSpanned(categoryArrayList.get(listPosition).getStatus(), "#0f9d58");
            holder.statusTv.setText(Html.fromHtml(space + status));
        } else if (categoryArrayList.get(listPosition).getStatus().equalsIgnoreCase("Failed")) {
            String status = getColoredSpanned(categoryArrayList.get(listPosition).getStatus(), "#FA2625");
            holder.statusTv.setText(Html.fromHtml(space + status));
        } else {
            String status = getColoredSpanned(categoryArrayList.get(listPosition).getStatus(), "#ffdd00");
            holder.statusTv.setText(Html.fromHtml(space + "    " + status));
        }

        Glide.with(mContext).load(BuildConfig.BASE_URL_IMAGE + categoryArrayList.get(listPosition).getValue())
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available)).
                into(holder.itemImage);

        holder.trackOrderTv.setOnClickListener(view -> {

        });

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    private String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.item_image)
        ImageView itemImage;
        @BindView(R.id.amount_paid_tv)
        TextView amountPaidTv;
        @BindView(R.id.order_on_tv)
        TextView orderOnTv;
        @BindView(R.id.status_tv)
        TextView statusTv;
        @BindView(R.id.track_order_tv)
        TextView trackOrderTv;
        @BindView(R.id.order_Id_tv)
        TextView orderIdTv;
        @BindView(R.id.itemName)
        TextView itemName;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity) mContext;
            Bundle bundle = new Bundle();
            bundle.putString("order_id", String.valueOf(categoryArrayList.get(getAdapterPosition()).getEntity_id()));
            Intent intent = new Intent(mContext, OrderDetails.class);
            intent.putExtra(PAYLOAD_BUNDLE, bundle);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        }
    }


}