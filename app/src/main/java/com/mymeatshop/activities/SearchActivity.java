package com.mymeatshop.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.SearchAdapter;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.ProductList;
import com.mymeatshop.model.response.SearchResponse;
import com.mymeatshop.utils.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends BaseActivity {
    public List<ProductList> productList = new ArrayList<>();
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.editText)
    EditText searchEdit;
    @BindView(R.id.searchRecycler)
    RecyclerView search_Recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        title.setText("Search Items");
        cartLo.setVisibility(View.GONE);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (searchEdit.length() > 2) {
                    getSearchedItem(true, searchEdit.getText().toString());
                }

            }
        });
    }

    @Override
    public void getTopDeal(String name, String sku) {
        super.getTopDeal(name, sku);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("sku", sku);
        goToActivity(SearchActivity.this, ProductDetail.class, bundle);
        finish();
    }

    public void getSearchedItem(boolean bool, String name) {
        try {
            JsonObject param = new JsonObject();
            param.addProperty("name", name);
            LoggerUtil.logItem(param);
            Call<JsonObject> loginCall = apiServices_shoopping.getSearchItem(bodyParam(param));
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            SearchResponse responseCartItem = new Gson().fromJson(paramResponse, SearchResponse.class);
                            productList.clear();
                            productList.addAll(responseCartItem.getProductList());
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null) {
                                SearchAdapter adapterViewAndroidMore = new SearchAdapter(SearchActivity.this, productList, SearchActivity.this);
                                LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
                                manager.setOrientation(RecyclerView.VERTICAL);
                                search_Recycler.setLayoutManager(manager);
                                search_Recycler.setAdapter(adapterViewAndroidMore);
                                search_Recycler.setHasFixedSize(true);
                            } else {

                                showMessage("Something went wrong.");
                            }
                        } else {

                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        showMessage("No data found!");

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }
    }
}
