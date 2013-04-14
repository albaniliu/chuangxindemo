package com.albaniliu.chuangxindemo;

import com.albaniliu.chuangxindemo.util.Downloader;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class DemoApplication extends Application {

    @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		this.startService(new Intent(this, Downloader.class));
		Log.v("DemoApplication", "start downloader");
	}

	@Override
    public void onTerminate() {
        super.onTerminate();
        // stop download service.
        this.stopService(new Intent(this, Downloader.class));
        Log.v("DemoApplication", "stop downloader");
    }
}
