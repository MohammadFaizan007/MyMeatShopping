package com.mymeatshop.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mymeatshop.R;
import com.mymeatshop.app.PreferencesManager;
import com.mymeatshop.common_activities.LoginActivity;
import com.mymeatshop.constants.BaseActivity;

import java.security.GeneralSecurityException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends BaseActivity {

    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.emailId_tv)
    TextView emailIdTv;
    @BindView(R.id.circleImageView2)
    CircleImageView circleImageView2;
    @BindView(R.id.myOrder_img)
    ImageView myOrderImg;
    @BindView(R.id.wishList_img)
    ImageView wishListImg;
    @BindView(R.id.rateUs_Img)
    ImageView rateUsImg;
    @BindView(R.id.myOrder_tv)
    TextView myOrderTv;
    @BindView(R.id.wishList_tv)
    TextView wishListTv;
    @BindView(R.id.rateUs_tv)
    TextView rateUsTv;
    @BindView(R.id.persondetails_tv)
    TextView persondetailsTv;
    @BindView(R.id.password_tv)
    TextView passwordTv;
    @BindView(R.id.address_tv)
    TextView addressTv;
    @BindView(R.id.singout_tv)
    TextView singoutTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        ButterKnife.bind(this);
        try {
            nameTv.setText(String.format("%s %s", decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent), decryptMsg(PreferencesManager.getInstance(context).getLNAME(), cross_intent)));
            emailIdTv.setText(decryptMsg(PreferencesManager.getInstance(context).getLoginID(), cross_intent));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.myOrder_img, R.id.wishList_img, R.id.rateUs_Img, R.id.myOrder_tv, R.id.wishList_tv, R.id.rateUs_tv, R.id.persondetails_tv, R.id.password_tv, R.id.address_tv, R.id.singout_tv, R.id.side_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.myOrder_img:
            case R.id.myOrder_tv:
                goToActivity(MyProfile.this, OrderHistroy.class, null);
                break;
            case R.id.wishList_img:
            case R.id.wishList_tv:
                goToActivity(MyProfile.this, WishList.class, null);
                break;
            case R.id.rateUs_Img:
            case R.id.rateUs_tv:
                Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                }
               // startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.mymeatshop")));
                break;
            case R.id.persondetails_tv:
                goToActivity(MyProfile.this, ChangePersonalDetails.class, null);
                break;
            case R.id.password_tv:
                goToActivity(MyProfile.this, ChangePassword.class, null);
                break;
            case R.id.address_tv:
                Bundle bundle = new Bundle();
                bundle.putString("from", "my_profile");
                goToActivity(MyProfile.this, ManageAddress.class, bundle);
                break;
            case R.id.singout_tv:
                logoutDialog(context, LoginActivity.class);
                break;
            case R.id.side_menu:
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                break;
        }
    }

    public void logoutDialog(final Context context, final Class activity) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Logout");
        builder1.setMessage("Do you really want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                try {
                    MyProfile.this.clearPrefrenceforTokenExpiry();
                    Intent intent1 = new Intent(context, activity);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                    MyProfile.this.finish();
                    dialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            nameTv.setText(String.format("%s %s", decryptMsg(PreferencesManager.getInstance(context).getNAME(), cross_intent), decryptMsg(PreferencesManager.getInstance(context).getLNAME(), cross_intent)));
            emailIdTv.setText(decryptMsg(PreferencesManager.getInstance(context).getLoginID(), cross_intent));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
