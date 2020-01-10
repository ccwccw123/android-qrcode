package com.example;

import android.app.Application;
import android.content.Context;

/**
 * Created by ccw on 2019/12/15.
 */
public class eApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationContext.setInstance(this);
    }
}
