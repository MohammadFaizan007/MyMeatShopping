package com.mymeatshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mymeatshop.R;
import com.mymeatshop.model.response.ProductList;
import com.mymeatshop.retrofit.MvpView;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context mContext;
    private List<ProductList> categoryArrayList;
    private MvpView mvp;

    public SearchAdapter(Context activity, List<ProductList> subCategoryList, MvpView mvp) {
        mContext = activity;
        this.categoryArrayList = subCategoryList;
        this.mvp = mvp;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_row, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchAdapter.ViewHolder holder, int listPosition) {

        holder.name.setText(categoryArrayList.get(listPosition).getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mvp.getTopDeal(categoryArrayList.get(listPosition).getName(), categoryArrayList.get(listPosition).getSku());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);

        }


    }


}
