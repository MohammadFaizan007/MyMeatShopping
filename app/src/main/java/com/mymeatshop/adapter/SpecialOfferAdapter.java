package com.mymeatshop.adapter;

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
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
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

public class SpecialOfferAdapter  extends RecyclerView.Adapter<SpecialOfferAdapter.ViewHolder> {
    private static DecimalFormat formatAmount = new DecimalFormat("0.00");
    List<DayDealItemItem> itemList;
    private Context mContext;
    private MvpView mvp;

    public SpecialOfferAdapter(Context context, List<DayDealItemItem> list, MvpView mvp) {
        mContext = context;
        itemList = list;
        this.mvp = mvp;
    }

    @NonNull
    @Override
    public SpecialOfferAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.special_row, parent, false);
        return new SpecialOfferAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SpecialOfferAdapter.ViewHolder holder, int listPosition) {
        holder.itemNameTv.setText(itemList.get(listPosition).getName());
        Glide.with(mContext).load(BuildConfig.BASE_URL_IMAGE + itemList.get(listPosition).getImage())
                .apply(new RequestOptions().diskCacheStrategy(AUTOMATIC).placeholder(R.drawable.image_not_available).error(R.drawable.image_not_available))
                .into(holder.itemImg);
        if (itemList.get(listPosition).getPrice() != null) {
            String price = formatAmount.format(Double.parseDouble(String.valueOf(itemList.get(listPosition).getPrice())));
            holder.priceTv.setText(String.format("₹ %s", price));
        }

        holder.priceTv.setPaintFlags(holder.priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        String offer_price = formatAmount.format(Double.parseDouble(String.valueOf(itemList.get(listPosition).getOfferprice())));
        holder.offerPriceTv.setText(String.format("₹ %s", offer_price));


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
