package com.example;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * application的单例，建议每个application在oncreate时调用setInstance将值设置进来
 */
public class ApplicationContext {

    @SuppressLint("StaticFieldLeak")
    private static Application sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    public static void setInstance(Application application) {
        sInstance = application;
    }
}
