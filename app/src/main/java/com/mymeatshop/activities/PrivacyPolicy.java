package com.mymeatshop.activities;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mymeatshop.R;
import com.mymeatshop.constants.BaseActivity;
import com.mymeatshop.utils.LoggerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyPolicy extends BaseActivity {
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cartLo)
    RelativeLayout cartLo;
    @BindView(R.id.side_menu)
    ImageView sideMenu;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ButterKnife.bind(this);
        title.setText("Privacy Policy");
        cartLo.setVisibility(View.GONE);
        sideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        String url = "https://mymeatshops.com/api/privacy_policy.html";
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
            } else {
                CookieManager.getInstance().setAcceptCookie(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setAppCacheEnabled(true);
//        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
//        Log.e("Link==>", param.getString("link"));
        loadLink(url);


    }

    public void loadLink(String link) {
        webview.setWebViewClient(new WebViewClientNormal());
        webview.loadUrl(link);
    }


    public class WebViewClientNormal extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LoggerUtil.logItem(url);
            if (url.contains("http"))
                view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }


    }
}
