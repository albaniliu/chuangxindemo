package com.albaniliu.chuangxindemo;

import android.app.Application;

public class DemoApplication extends Application {

    @Override
    public void onTerminate() {
        super.onTerminate();
        // stop download service.
    }
}
