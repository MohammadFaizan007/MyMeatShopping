package com.mymeatshop.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.BuildConfig;
import com.mymeatshop.R;
import com.mymeatshop.activities.AddAddressActivity;
import com.mymeatshop.activities.ProductDetail;
import com.mymeatshop.activities.ProductListActivity;
import com.mymeatshop.adapter.DashboardBestSellerAdapter;
import com.mymeatshop.adapter.DashboardSliderAdapter;
import com.mymeatshop.adapter.DashboardTopDealAdapter;
import com.mymeatshop.adapter.PopularCategoriesAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.common_activities.MainContainer;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.BaseFragment;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.constants.UrlConstants;
import com.mymeatshop.model.response.ResponseAddtoCart;
import com.mymeatshop.model.response.dashboard.ChildrenDataItem;
import com.mymeatshop.model.response.dashboard.ResponseBestSellerAndDeal;
import com.mymeatshop.model.response.dashboard.ResponseCategoriesAndSlider;
import com.mymeatshop.retrofit.MvpView;
import com.mymeatshop.utils.AddToCartInterface;
import com.mymeatshop.utils.BannerLayout;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Dashboard extends BaseFragment implements MvpView, AddToCartInterface {
    // TODO: Rename parameter arguments, choose names that match

    Unbinder unbinder;
    @BindView(R.id.txtUpdate)
    TextView txtUpdate;

    @BindView(R.id.topDealTxt)
    TextView topDealTxt;
    @BindView(R.id.bestSellerTxt)
    TextView bestSellerTxt;
    @BindView(R.id.edit_location)
    ImageView editLocation;
    @BindView(R.id.popularCategoryRv)
    RecyclerView popularCategoryRv;
    @BindView(R.id.topDealRv)
    RecyclerView topDealRv;
    @BindView(R.id.recyclerSlider)
    BannerLayout recyclerSlider;
    @BindView(R.id.recyclerBestSeller)
    RecyclerView recyclerBestSeller;
    @BindView(R.id.mainScroll)
    NestedScrollView mainScroll;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreatedStuff(View view, @Nullable Bundle savedInstanceState) {
        mainScroll.setSmoothScrollingEnabled(true);

        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getPopularCategory();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }
        String text = "<font color=#0f9d58><b>Special</b></font> <font color=#f7921b><b>Offer</b></font>";
        topDealTxt.setText(Html.fromHtml(text));

        String text_ = "<font color=#0f9d58><b>Popular</b></font> <font color=#f7921b><b>Items</b></font>";
        bestSellerTxt.setText(Html.fromHtml(text_));

    }


    private void getPopularCategory() {
        try {
            showLoading();
            String url = BuildConfig.BASE_URL_SHOPPING + "slider.php";
            Call<JsonObject> call = apiServices_shoopping.getPopularCategories(url, PreferencesManager.getInstance(context).getANDROIDID());
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    try {
                        if (response.isSuccessful()) {
                            LoggerUtil.logItem(Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent));
                            ResponseCategoriesAndSlider response_new = new Gson().fromJson(Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent), ResponseCategoriesAndSlider.class);
                            if (response.body() != null) {
                                if (response_new.getSliderlist().getSliderlistdata().size() > 0) {
                                    DashboardSliderAdapter webBannerAdapter = new DashboardSliderAdapter(context, response_new.getSliderlist().getSliderlistdata(), Dashboard.this);
                                    recyclerSlider.setAdapter(webBannerAdapter);
                                    recyclerSlider.setAutoPlaying(true);
                                }
                                if (response_new.getPCat().getChildrenData().size() > 0) {
//                                    UrlConstants.tabNameList = response_new.getPCat().getChildrenData();
                                    UrlConstants.tabNameList = new ArrayList<>();
                                    for (int i = 0; i < response_new.getPCat().getChildrenData().size(); i++) {
                                        ChildrenDataItem dataItem = response_new.getPCat().getChildrenData().get(i);
                                        if (dataItem.isIsActive()) {
                                            UrlConstants.tabNameList.add(dataItem);
                                        }
                                    }
                                    PopularCategoriesAdapter adapterViewAndroidMore = new PopularCategoriesAdapter(getActivity(), UrlConstants.tabNameList, Dashboard.this);
                                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                                    manager.setOrientation(RecyclerView.HORIZONTAL);
                                    popularCategoryRv.setLayoutManager(manager);
                                    popularCategoryRv.setAdapter(adapterViewAndroidMore);
                                    popularCategoryRv.setHasFixedSize(true);
                                }

                            } else {
                                showMessage("Something went wrong.");
                            }
                        } else {
                            clearPrefrenceforTokenExpiry();
                            getAndroidId();
                            goToActivityWithFinish(LoginActivity.class, null);
                        }
                        getBestSellerAndDeal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                    getBestSellerAndDeal();
                }
            });

        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }

    private void getBestSellerAndDeal() {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), cross_intent));
            Call<JsonObject> call = apiServices_shoopping.getBestSellerAndDeal(((BaseActivity) context).bodyParam(param),PreferencesManager.getInstance(context).getANDROIDID());
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
                                    DashboardTopDealAdapter adapterViewAndroidMore = new DashboardTopDealAdapter(getActivity(), responseBestSellerAndDeal.getDayDeal().getDayDealItem(), Dashboard.this, Dashboard.this);
                                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                                    manager.setOrientation(RecyclerView.HORIZONTAL);
                                    topDealRv.setLayoutManager(manager);
                                    topDealRv.setAdapter(adapterViewAndroidMore);
                                    topDealRv.setHasFixedSize(true);
                                }
                                if (responseBestSellerAndDeal.getBestSeller().getBestSellerItem().size() > 0) {
                                    DashboardBestSellerAdapter dashboardBestSellerAdapter = new DashboardBestSellerAdapter(context, responseBestSellerAndDeal.getBestSeller().getBestSellerItem(), Dashboard.this, Dashboard.this);
                                    LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                                    manager.setOrientation(RecyclerView.HORIZONTAL);
                                    recyclerBestSeller.setLayoutManager(manager);
                                    recyclerBestSeller.setAdapter(dashboardBestSellerAdapter);
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
    public void getClickPosition(int position, String name) {
        super.getClickPosition(position, name);
        PreferencesManager.getInstance(context).setProductid(String.valueOf(UrlConstants.tabNameList.get(position).getId()));
        Bundle bundle = new Bundle();
        bundle.putString("product_name", name);
        bundle.putInt("position", position);
        goToActivity(ProductListActivity.class, bundle);
    }

    @Override
    public void getTopDeal(String name, String sku) {
        super.getTopDeal(name, sku);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("sku", sku);
        goToActivity(ProductDetail.class, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.txtUpdate, R.id.edit_location, R.id.imageLocation, R.id.location_tv, R.id.addressEdit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txtUpdate:
                break;
            case R.id.edit_location:
            case R.id.imageLocation:
            case R.id.location_tv:
            case R.id.addressEdit_tv:
                goToActivity(AddAddressActivity.class, null);
                break;
        }
    }

    private void getAddtoCart(String sku, String qty) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("sku", sku);
            param.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getCartid(), ((BaseActivity) context).cross_intent));
            param.addProperty("qty", qty);
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((BaseActivity) context).apiServices_shoopping.getAddtoCart(((BaseActivity) context).bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), ((BaseActivity) context).cross_intent);
                            ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            LoggerUtil.logItem(responseAddtoCart);
                            if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {
                               /* if (from.equalsIgnoreCase("purchase_now")) {
                                    //goToActivity(context, CartActivity.class, null);
                                } else {*/
                                ((BaseActivity) context).showToastS(responseAddtoCart.getMessage());
                                ((MainContainer) context).getCartItem();
//                                    priceTotalTv.setText(String.valueOf((float) qtyBox.getValue() * (float) responseProductDetail.getPrice()));*/
                                //  }
                            } else {
                                ((BaseActivity) context).showToastS(responseAddtoCart.getMessage());
                            }
                        } else {
                            ((BaseActivity) context).showMessage("Something went wrong.");
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
            hideLoading();
            e.printStackTrace();
        }
    }


    private void getGuestAddToCart(String sku, String qty) {
        try {
            showLoading();
            JsonObject param = new JsonObject();
            JsonObject cart = new JsonObject();
            cart.addProperty("sku", sku);
            cart.addProperty("quote_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            cart.addProperty("qty", qty);
            param.add("cartItem", cart);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = ((BaseActivity) context).apiGuest_Shopping.getGuestAddToCart(param, Cons.decryptMsg(PreferencesManager.getInstance(context).getGuestToken(), cross_intent));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                           /* String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseAddtoCart responseAddtoCart = new Gson().fromJson(paramResponse, ResponseAddtoCart.class);
                            LoggerUtil.logItem(responseAddtoCart);
                            if (responseAddtoCart.getResponse().equalsIgnoreCase("Success")) {*/
                            ((BaseActivity) context).showToastS("Item Added To Cart");
                            ((MainContainer) context).getGuestCartItem();
                            /*} else {
                                ((BaseActivity) context).showToastS(responseAddtoCart.getMessage());
                            }*/
                        } else if (response.code() == 400) {
                            showMessage("Product that you are trying to add is out of stock.");
                        } else {
                            showMessage("Something went wrong.");
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
            hideLoading();
            e.printStackTrace();
        }
    }

    @Override
    public void updateCount(String sku, String s, String type) {
        if (type.equalsIgnoreCase("user")) {
            getAddtoCart(sku, s);
        } else {
            getGuestAddToCart(sku, s);
        }


    }
}
