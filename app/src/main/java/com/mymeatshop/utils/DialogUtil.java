package com.mymeatshop.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.JsonObject;
import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.common_activities.SignUpActivity;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.constants.Cons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DialogUtil {
    private static final String TAG = "DialogUtil";

    public static ProgressDialog progressDialog;

    public DialogUtil(Activity activity) {
        // This utility class is not publicly instantiable
//        progressDialog = new ProgressDialog(activity);
    }

    public static ProgressDialog showLoadingDialog(Activity activity, String callingPlace) {
        //Log.d(TAG, "showLoadingDialog: " + callingPlace);
//        if (!progressDialog.isShowing())
        progressDialog = new ProgressDialog(activity);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog_layout);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        return progressDialog;


    }

    public static void showInfoDialog(Context context, String title, String info) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(info);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public static void login_Dialog(Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View confirmDialog = li.inflate(R.layout.dialog_login_signup, null);
        AppCompatButton btn_login = confirmDialog.findViewById(R.id.btn_login);
        AppCompatButton btn_signup = confirmDialog.findViewById(R.id.btn_signup);
        AppCompatButton btn_guest = confirmDialog.findViewById(R.id.btn_guest);
        //Creating an alertdialog builder
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        //Adding our dialog box to the view of alert dialog
        alert.setView(confirmDialog);

        //Creating an alert dialog
        AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(true);


        btn_login.setOnClickListener(view -> {
            alertDialog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, LoginActivity.class, null);
        });
        btn_signup.setOnClickListener(view -> {
            alertDialog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, SignUpActivity.class, null);
        });
        btn_guest.setOnClickListener(view -> {
            alertDialog.dismiss();
            Toast.makeText(context, "Feature coming soon", Toast.LENGTH_SHORT).show();
        });

        //Displaying the alert dialog
        alertDialog.show();
    }

    public static void loginDialog(Context context) {
        Dialog dialoglog = new Dialog(context, R.style.DialogSlideAnim);
        dialoglog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialoglog.setContentView(R.layout.dialog_login_signup);
        Window window = dialoglog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        dialoglog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        AppCompatButton btn_login = dialoglog.findViewById(R.id.btn_login);
        AppCompatButton btn_signup = dialoglog.findViewById(R.id.btn_signup);
        AppCompatButton btn_guest = dialoglog.findViewById(R.id.btn_guest);
        dialoglog.show();
        btn_login.setOnClickListener(view -> {
            dialoglog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, LoginActivity.class, null);
        });
        btn_signup.setOnClickListener(view -> {
            dialoglog.dismiss();
            ((BaseActivity) context).goToActivityWithFinish((Activity) context, SignUpActivity.class, null);
        });
        btn_guest.setOnClickListener(view -> {
            getGuestToken(context);
            dialoglog.dismiss();
        });

    }

    private static void getGuestToken(Context context) {
        try {
            ((BaseActivity) context).showLoading();
            Call<JsonObject> loginCall = ((BaseActivity) context).apiServices_shoopping.getGuestToken();
            loginCall.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(response.body());
                    LoggerUtil.logItem(response.code());
                    try {
                        if (response.isSuccessful()) {
                            Log.d("DATA", "onResponse: " + response.body());

                            JsonObject jsonObject = response.body().getAsJsonObject();
                            String token = jsonObject.get("token").getAsString();
                            String q_id = jsonObject.get("q_id").getAsString();
                            PreferencesManager.getInstance(context).setGuestToken(Cons.encryptMsg(String.valueOf(token), ((BaseActivity) context).cross_intent));
                            PreferencesManager.getInstance(context).setQid(Cons.encryptMsg(String.valueOf(q_id), ((BaseActivity) context).cross_intent));
                        } else {
                            ((BaseActivity) context).showMessage("Something went wrong.");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    ((BaseActivity) context).hideLoading();
                    LoggerUtil.logItem(t.getMessage());
                }
            });
        } catch (Error | Exception e) {
            ((BaseActivity) context).hideLoading();
            e.printStackTrace();
        }
    }


    public static void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}