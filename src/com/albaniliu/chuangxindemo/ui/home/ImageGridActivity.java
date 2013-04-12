package com.albaniliu.chuangxindemo.ui.home;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.albaniliu.chuangxindemo.ImageShow;
import com.albaniliu.chuangxindemo.R;
import com.albaniliu.chuangxindemo.util.HTTPClient;
import com.albaniliu.chuangxindemo.util.ResourceUtils;

public class ImageGridActivity extends Activity implements View.OnClickListener {
	private static String TAG = "ImageGridActivity";
	
	private Thread downloadThread;

	public static final int MSG_CHECK_HOME_RESOURCE_LOADING = 1001;

	public static  int DEFAULT_BANNER_COUNT = 5;

	public static final String Id = "ImageGridActivity";

	private LinearLayout classfiView;

//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (msg.what == MSG_CHECK_HOME_RESOURCE_LOADING) {
//				if (homeData.isLoaded()) {
//					setClassfiView();
//				} else if (!homeData.isLoading()) {
//					homeData.loadHomeResource(mHandler,HomeActivity.this);
//				} else
//					chechHomeResource();
//			} else {
//				// if (bannerAdapter != null)
//				// bannerAdapter.notifyDataSetChanged();
//				setClassfiView();
//			}
//		}
//
//	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("HomeActivity", "onCreate");
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.home_activity);
		ResourceUtils.setContext(this);
		classfiView = (LinearLayout) this.findViewById(R.id.classfi_view);
		setDefaultClassfiView();
		downloadThread = new DownloadThread();
		downloadThread.start();
	}

	private void setDefaultClassfiView() {
		classfiView.removeAllViews();
		for (int i = 0; i < 7; i++) {
			setDefaultClassfiLine(i + 1);
		}
	}
	
	private void setDefaultClassfiLine(final int line) {
		LinearLayout classfiLine = (LinearLayout) getLayoutInflater().inflate(
				R.layout.classfi_line, null);
		int screenWidth = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		int screenHeight = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		int num = 2;
		if (screenWidth > screenHeight) num = 4;
		for (int i = 0; i < num; i++) {
			LinearLayout classfiImage = (LinearLayout) getLayoutInflater().inflate(R.layout.classfi_image, null);
			FrameLayout frame = (FrameLayout) classfiImage.findViewById(R.id.left);
			LinearLayout des = (LinearLayout) classfiImage.findViewById(R.id.des_layout);
			des.setVisibility(View.GONE);
			frame.setPadding(5, 1, 5, 1);
			classfiLine.addView(classfiImage);
			frame.setOnClickListener(this);
		}
		
		classfiView.addView(classfiLine);
	}

	
	class DownloadThread extends Thread {
		public void run() {
			try {
				JSONArray allDir = HTTPClient.getJSONArrayFromUrl(HTTPClient.URL_INDEX);
				for (int i = 0; i < allDir.length(); i++) {
					JSONObject obj = (JSONObject) allDir.get(i);
					Log.v(TAG, obj.getString("id"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            default:
                if (downloadThread.isAlive()) {
                    break;
                }
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), ImageShow.class);
                startActivity(intent);
                break;
        }
        
    }
}
