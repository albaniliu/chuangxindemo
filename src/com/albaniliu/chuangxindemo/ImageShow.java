package com.albaniliu.chuangxindemo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
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
	
	private SingleTapListner mListener = new SingleTapListner() {

		@Override
		public void onSingleTapUp() {
			int delta = mFlowBar.getHeight();
			if (mFlowBar.getVisibility() == View.INVISIBLE
					|| mFlowBar.getVisibility() == View.GONE) {
				mFlowBar.setVisibility(View.VISIBLE);
				Animation anim = new TranslateAnimation(0, 0, -delta, 0);
				anim.setDuration(300);
				mFlowBar.startAnimation(anim);
			} else {
				mFlowBar.setVisibility(View.INVISIBLE);
				Animation anim = new TranslateAnimation(0, 0, 0, -delta);
				anim.setDuration(300);
				mFlowBar.startAnimation(anim);
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.largepic);
		
		mAdapter = new ViewPagerAdapter(mTestFolder);
		mPager = (LargePicGallery) findViewById(R.id.photo_flow);
		mPager.setOffscreenPageLimit(1);
		mPager.setPageMargin(20);
		mPager.setHorizontalFadingEdgeEnabled(true);
		mPager.setAdapter(mAdapter);
		mPager.setTapUpListener(mListener);
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
