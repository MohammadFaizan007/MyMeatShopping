package com.mymeatshop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.model.response.dashboard.ChildrenDataItem;
import com.mymeatshop.retrofit.MvpView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.AUTOMATIC;

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.ViewHolder> {


    private List<ChildrenDataItem> itemList;
    private Context mContext;
    private MvpView mvp;

    public PopularCategoriesAdapter(Context context, List<ChildrenDataItem> list, MvpView mvp) {
        this.mContext = context;
        this.itemList = list;
        this.mvp = mvp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int listPosition) {
        holder.procate.setText(itemList.get(listPosition).getName());

        Glide.with(mContext).load(BuildConfig.BASE_URL_CATEGORY_IMAGE + itemList.get(listPosition).getName() + ".png")
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(holder.popularCategoryImg);
        Log.d("<><", "onBindViewHolder: " + BuildConfig.BASE_URL_CATEGORY_IMAGE + itemList.get(listPosition).getName() + ".png");

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.popularCategory_img)
        ImageView popularCategoryImg;
        @BindView(R.id.popularCategory_tv)
        TextView popularCategoryTv;
        @BindView(R.id.procate)
        TextView procate;
        @BindView(R.id.consLayout)
        LinearLayout consLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            consLayout.setOnClickListener(v -> mvp.getClickPosition(getAdapterPosition(), itemList.get(getAdapterPosition()).getName()));
        }
    }

}