package com.mymeatshop.app;

import android.app.Application;

import com.mymeatshop.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppController extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/font_regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}