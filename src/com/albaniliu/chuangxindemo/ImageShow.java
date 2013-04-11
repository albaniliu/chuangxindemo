package com.albaniliu.chuangxindemo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.albaniliu.chuangxindemo.widget.LargePicGallery;
import com.albaniliu.chuangxindemo.widget.LargePicGallery.SingleTapListner;
import com.albaniliu.chuangxindemo.widget.ViewPagerAdapter;

public class ImageShow extends Activity {
	private String mPath = Environment.getExternalStorageDirectory() + "/liangdemo1";
	private File mTestFolder = new File(mPath);
	private ViewPagerAdapter mAdapter;
	private LinearLayout mFlowBar;
	private LargePicGallery mPager;
	private Handler mHanler = new Handler();
	
	private SingleTapListner mListener = new SingleTapListner() {
		@Override
		public void onSingleTapUp() {
			toggleFlowBar();
		}
	};
	
	private Runnable mToggleRunnable = new Runnable() {
        @Override
        public void run() {
            toggleFlowBar();
        }
    };

	private void toggleFlowBar() {
	    int delta = mFlowBar.getHeight();
	    if (mFlowBar.getVisibility() == View.INVISIBLE
                || mFlowBar.getVisibility() == View.GONE) {
            mFlowBar.setVisibility(View.VISIBLE);
            Animation anim = new TranslateAnimation(0, 0, -delta, 0);
            anim.setDuration(300);
            mFlowBar.startAnimation(anim);
            mHanler.removeCallbacks(mToggleRunnable);
            mHanler.postDelayed(mToggleRunnable, 3000);
        } else {
            mFlowBar.setVisibility(View.INVISIBLE);
            Animation anim = new TranslateAnimation(0, 0, 0, -delta);
            anim.setDuration(300);
            mFlowBar.startAnimation(anim);
        }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.largepic);
		
		mAdapter = new ViewPagerAdapter(mTestFolder, mListener);
		mPager = (LargePicGallery) findViewById(R.id.photo_flow);
		mPager.setOffscreenPageLimit(1);
		mPager.setPageMargin(20);
		mPager.setHorizontalFadingEdgeEnabled(true);
		mPager.setAdapter(mAdapter);
		mFlowBar = (LinearLayout) findViewById(R.id.flow_bar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_image_show, menu);
		return true;
	}

	public void onBackClick(View view) {
		onBackPressed();
	}
	
	public void onSlideClick(View view) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), SlideShowActivity.class);
		startActivity(intent);
	}
}
