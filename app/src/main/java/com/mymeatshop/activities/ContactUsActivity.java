package com.mymeatshop.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.adapter.SearchAdapter;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;
import com.mymeatshop.model.response.SearchResponse;
import com.mymeatshop.model.response.contactus.ContactusResponse;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.mobile_txt)
    TextView mobile_txt;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        ButterKnife.bind(this);
        title.setText("Contact Us");
        cartLo.setVisibility(View.GONE);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                // emailIntent.putExtra(Intent.EXTRA_TEXT, body);
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });
        if (NetworkUtils.getConnectivityStatus(context) != 0) {
            getContactUs();
        } else {
            showMessage(getResources().getString(R.string.alert_internet));
        }

    }
    public void getContactUs() {
        try {
            showLoading();
            Call<JsonObject> loginCall = apiServices_shoopping.getContactUs();
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            hideLoading();
                            String paramResponse = Cons.decryptMsg(response.body().get("body").getAsString(), cross_intent);
                            ContactusResponse responseCartItem = new Gson().fromJson(paramResponse, ContactusResponse.class);
                            LoggerUtil.logItem(responseCartItem);
                            if (response.body() != null&&responseCartItem.getMessage().equalsIgnoreCase("success")) {
                                email=responseCartItem.getData().get(0).getEmail();
                                txtEmail.setText(responseCartItem.getData().get(0).getEmail());
                                mobile_txt.setText(responseCartItem.getData().get(0).getMobile());

                            } else {

                                showMessage("Something went wrong.");
                            }
                        } else {
                            hideLoading();
                            showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoading();

                        showMessage("No data found!");

                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    LoggerUtil.logItem(t.getMessage());
                    hideLoading();
                }
            });
        } catch (Error | Exception e) {
            hideLoading();
            e.printStackTrace();
        }
    }
}
