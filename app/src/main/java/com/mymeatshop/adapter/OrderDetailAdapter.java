package com.mymeatshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mymeatshop.R;
import com.mymeatshop.model.response.order_detail.DataItem;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0");


    private Context mContext;
    private List<DataItem> categoryArrayList;

    public OrderDetailAdapter(Context activity, List<DataItem> subCategoryList) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {


        holder.itemName.setText(categoryArrayList.get(listPosition).getName());
        String QTY = formatAmount.format(Double.parseDouble(String.valueOf(categoryArrayList.get(listPosition).getQtyOrdered())));
        holder.itemPrice.setText(QTY+" X "+"₹ "+categoryArrayList.get(listPosition).getBasePrice());
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_price)
        TextView itemPrice;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
