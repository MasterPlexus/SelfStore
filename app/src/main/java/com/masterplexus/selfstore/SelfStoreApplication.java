package com.masterplexus.selfstore;

import android.app.Application;
import android.content.Context;

public class SelfStoreApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        SelfStoreApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return SelfStoreApplication.context;
    }
}