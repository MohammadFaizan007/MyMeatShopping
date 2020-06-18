package com.mymeatshop.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.utils.LoggerUtil;
import com.mymeatshop.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BulkOrderActivity extends BaseActivity {
    @BindView(R.id.edt_Name)
    EditText edtName;
    @BindView(R.id.edt_contactno)
    EditText edtContactno;
    @BindView(R.id.edt_pincode)
    EditText edtPincode;
    @BindView(R.id.edt_desc)
    EditText edtDesc;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.side_menu)
    ImageView sideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bulk_order);
        ButterKnife.bind(this);
        title.setText("Bulk Order");
        cartLo.setVisibility(View.GONE);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

    }

    @OnClick(R.id.btn_submit)
    public void onViewClicked() {
        if (validate()) {
            submitEnquiry(true, edtName.getText().toString(), edtContactno.getText().toString(), edtPincode.getText().toString(),
                    edtDesc.getText().toString());


        }
    }

    private boolean validate() {
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Please Enter Your Name");
            edtName.requestFocus();
            return false;
        } else if (edtContactno.getText().toString().isEmpty() || (edtContactno.getText().toString().length() < 10)) {
            edtContactno.setError("Please Enter Valid Phone Number");
            edtContactno.requestFocus();
            return false;
        } else if (edtPincode.getText().toString().isEmpty() || edtPincode.getText().toString().length() < 6) {
            edtPincode.setError("Please Enter Valid Pincode");
            edtPincode.requestFocus();
            return false;
        } else if (edtDesc.getText().toString().isEmpty()) {
            edtDesc.setError("Please Enter Description");
            edtDesc.requestFocus();
            return false;
        }
        return true;

    }

    public void submitEnquiry(boolean bool, String name, String mob, String pin, String desc) {
        try {
            if (bool)
                showLoading();
            JsonObject param = new JsonObject();
            param.addProperty("name", name);
            param.addProperty("mobile", mob);
            param.addProperty("pin", pin);
            param.addProperty("des", desc);
            LoggerUtil.logItem(param);
            Call<String> loginCall = apiServices_shoopping.postEnquiry(param);
            loginCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    hideLoading();
                    try {
                        if (response.isSuccessful()) {
                            String msg = response.body().replace("\"", "");
//                                {\"data\":\"19\",\"response\":\"Success\"}
                            Log.d("sdfsdf", "onResponse: " + msg);
//                                successDialog();
                            Utils.hideSoftKeyboard(context);
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            finish();

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
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
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
