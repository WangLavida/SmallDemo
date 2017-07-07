package com.wolf.smalldemo;

import android.app.Application;

import net.wequick.small.Small;

/**
 * Created by W.J on 2017/7/7.
 */

public class MainApplication extends Application {
    public MainApplication() {
        Small.preSetUp(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
