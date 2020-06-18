package com.mymeatshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.adapter.DashboardBestSellerAdapter;
import com.mymeatshop.adapter.DashboardTopDealAdapter;
import com.mymeatshop.adapter.SpecialOfferAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.fragment.Dashboard;
import com.mymeatshop.model.response.dashboard.ResponseBestSellerAndDeal;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.utils.AddToCartInterface;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialOfferActivity extends BaseActivity implements MvpView {
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.special_offer_recycler)
    RecyclerView specialOfferRecycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_offer_layout);
        ButterKnife.bind(this);
        title.setText("Special Offers");
        cartLo.setVisibility(View.GONE);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getBestSellerAndDeal();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
    }
    private void getBestSellerAndDeal() {
        try {
            showLoading();
           // String url = BuildConfig.BASE_URL_SHOPPING + "best_seller.php";
            JsonObject param = new JsonObject();
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            Call<JsonObject> call = apiServices_shoopping.getBestSellerAndDeal(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem("TOP DEAL");
                    LoggerUtil.logItem(response.body());
                    try {
                        if (response.isSuccessful()) {
                            ResponseBestSellerAndDeal responseBestSellerAndDeal = new Gson().fromJson(Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent), ResponseBestSellerAndDeal.class);
                            if (response.body() != null) {
                                if (responseBestSellerAndDeal.getDayDeal().getDayDealItem().size() > 0) {
                                    SpecialOfferAdapter adapterViewAndroidMore = new SpecialOfferAdapter(SpecialOfferActivity.this, responseBestSellerAndDeal.getDayDeal().getDayDealItem(), SpecialOfferActivity.this);
                                    GridLayoutManager manager = new GridLayoutManager(SpecialOfferActivity.this,2);
                                   // manager.setOrientation(RecyclerView.HORIZONTAL);
                                    specialOfferRecycler.setLayoutManager(manager);
                                    specialOfferRecycler.setAdapter(adapterViewAndroidMore);
                                    specialOfferRecycler.setHasFixedSize(true);
                                }

                            } else {
                                showMessage("Something went wrong.");
                            }
                        } else {
                            clearPrefrenceforTokenExpiry();
                            getAndroidId();
                            goToActivityWithFinish(LoginActivity.class, null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void getTopDeal(String name, String sku) {
        super.getTopDeal(name, sku);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("sku", sku);
        goToActivity(SpecialOfferActivity.this,ProductDetail.class, bundle);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
