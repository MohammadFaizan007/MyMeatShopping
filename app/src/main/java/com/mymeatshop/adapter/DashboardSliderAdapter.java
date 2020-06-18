package com.mymeatshop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.model.response.dashboard.SliderlistdataItem;
import com.mymeatshop.retrofit.MvpView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class DashboardSliderAdapter extends RecyclerView.Adapter<DashboardSliderAdapter.MzViewHolder> {

    private Context context;
    private List<SliderlistdataItem> shoppingslideritem;
    private MvpView mvp;

    public DashboardSliderAdapter(Context context, List<SliderlistdataItem> shoppingslideritem, MvpView mvp) {
        this.context = context;
        this.shoppingslideritem = shoppingslideritem;
        this.mvp = mvp;
    }


    @NotNull
    @Override
    public DashboardSliderAdapter.MzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MzViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_slider_item_image,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardSliderAdapter.MzViewHolder holder, final int position) {
        if (shoppingslideritem == null || shoppingslideritem.isEmpty())
            return;

        Glide.with(context).load(BuildConfig.BASE_URL_IMAGE_BANNER + shoppingslideritem.get(position).getImage())
                .apply(new RequestOptions().placeholder(R.drawable.image_not_available)
                        .error(R.drawable.image_not_available))
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        if (shoppingslideritem != null) {
            return shoppingslideritem.size();
        }
        return 0;
    }


    class MzViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MzViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageTimer);
            imageView.setOnClickListener(v -> {
                mvp.getClickPosition(getAdapterPosition(), shoppingslideritem.get(getAdapterPosition()).getName());
//                if (shoppingslideritem.get(getAdapterPosition()).getOfferOn().equalsIgnoreCase("iiashopping")) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("shopping", "Shopping");
//                    ((MainContainer) context).goToActivity((Activity) context, ShoppingActivityMain.class, bundle);
//                } else {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("from", "Main");
//                    bundle.putString("link", shoppingslideritem.get(getAdapterPosition()).getOfferURL());
//                    ((MainContainer) context).goToActivity((Activity) context, WebViewActivity.class, bundle);
//                }

            });
        }
    }

}