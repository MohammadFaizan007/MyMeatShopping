package com.mymeatshop.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.constants.UrlConstants;
import com.mymeatshop.fragment.ProductListFragmentCommon;
import com.mymeatshop.model.response.ResponseCartItem;
import com.mymeatshop.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mymeatshop.app.AppConfig.PAYLOAD_BUNDLE;

public class ProductListActivity extends BaseActivity {

    @BindView(R.id.cart_counter)
    public TextView cartCounter;
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.searchImg)
    ImageView searchImg;
    @BindView(R.id.badge)
    ImageView badge;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    Bundle param;
    ViewPagerAdapter adapter;
    int currentPosition = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);
        ButterKnife.bind(this);

        param = getIntent().getBundleExtra(PAYLOAD_BUNDLE);
        title.setText(param.getString("product_name"));
        title.setAllCaps(true);
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());


        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        try {
            TabLayout.Tab tab = tabLayout.getTabAt(param.getInt("position"));
            tab.select();
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPosition = i;
                title.setText(UrlConstants.tabNameList.get(currentPosition).getName());
                title.setAllCaps(true);
                ((ProductListFragmentCommon) adapter.getItem(i)).getProductList(String.valueOf(UrlConstants.tabNameList.get(currentPosition).getId()));
                LoggerUtil.logItem(currentPosition + " selected");
                LoggerUtil.logItem(UrlConstants.tabNameList.get(currentPosition).getName() + " name");
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        viewPager.postDelayed(() -> {
            ((ProductListFragmentCommon) adapter.getItem(param.getInt("position"))).getProductList(String.valueOf(UrlConstants.tabNameList.get(param.getInt("position")).getId()));
        }, 100);


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(UrlConstants.tabNameList!=null){
            if(UrlConstants.tabNameList.size()>0){
                for (int i = 0; i < UrlConstants.tabNameList.size(); i++) {
                    adapter.addFragment(new ProductListFragmentCommon(), UrlConstants.tabNameList.get(i).getName());
                }
            }
        }
        viewPager.setAdapter(adapter);
    }

    @OnClick({R.id.side_menu, R.id.searchImg, R.id.badge, R.id.cart_counter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
            case R.id.searchImg:
                break;
            case R.id.badge:
            case R.id.cart_counter:
                goToActivity(ProductListActivity.this, CartActivity.class, null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartCounter.setText(PreferencesManager.getInstance(context).getCartcount());
    }

    public void getCartItem() {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("token", Cons.decryptMsg(PreferencesManager.getInstance(context).getAuthToken(), cross_intent));
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getCartItem(bodyParam(param), PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        hideLoading();
                        LoggerUtil.logItem(response.body());
                        LoggerUtil.logItem(response.code());

                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ResponseCartItem responseCartItem = new Gson().fromJson(paramResponse, ResponseCartItem.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null && responseCartItem.getCartitems().size() > 0) {
                                PreferencesManager.getInstance(context).setCartcount(String.valueOf(responseCartItem.getCartitems().size()));
                                updateCounter();
                            } else {
                                PreferencesManager.getInstance(context).setCartcount("0");
                                updateCounter();
                            }
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

    public void updateCounter() {
        cartCounter.setText(String.valueOf(PreferencesManager.getInstance(context).getCartcount()));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
