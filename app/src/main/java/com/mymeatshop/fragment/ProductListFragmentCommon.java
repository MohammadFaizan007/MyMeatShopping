package com.mymeatshop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.activities.ProductListActivity;
import com.mymeatshop.adapter.ProductListAdapter;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.constants.BaseFragment;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ProductListItem;
import com.mymeatshop.model.response.ResponseProductListByCategory;
import com.mymeatshop.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductListFragmentCommon extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match

    Unbinder unbinder;
    @BindView(R.id.productList_recycler)
    RecyclerView productListRecycler;
    List<ProductListItem> list = new ArrayList<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreatedStuff(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        productListRecycler.setLayoutManager(manager);
        productListRecycler.removeAllViews();

    }

    public void getProductList(String id) {
        try {
            LoggerUtil.logItem(id);
//            showLoading();
            progressBar.setVisibility(View.VISIBLE);
            JsonObject param = new JsonObject();
            param.addProperty("id", Cons.encryptMsg(id, cross_intent));
            param.addProperty("customer_id", Cons.decryptMsg(PreferencesManager.getInstance(context).getUSERID(), ((ProductListActivity) context).cross_intent));
            LoggerUtil.logItem(PreferencesManager.getInstance(context).getProductid());
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getProductListByCategory(param, PreferencesManager.getInstance(context).getANDROIDID());
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    try {
                        hideLoading();
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                            LoggerUtil.logItem(response.body());
                            LoggerUtil.logItem(response.code());

                            if (response.isSuccessful()) {
                                String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                                ResponseProductListByCategory responseProductList = new Gson().fromJson(paramResponse, ResponseProductListByCategory.class);
                                LoggerUtil.logItem(responseProductList);
                                if (response.body() != null && responseProductList.getProductList().size() > 0) {
                                    for (int i = 0; i < responseProductList.getProductList().size(); i++) {
                                        if (responseProductList.getProductList().get(i).getStatus() == 1) {
                                            list.add(responseProductList.getProductList().get(i));
                                        }
                                    }
                                    ProductListAdapter productListAdapter = new ProductListAdapter(context, list);
                                    productListRecycler.setAdapter(productListAdapter);
                                    productListAdapter.notifyDataSetChanged();
                                } else {
                                    showMessage("No Data Found.");
                                }
                            } else {
                                showMessage("Something went wrong.");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
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
}
