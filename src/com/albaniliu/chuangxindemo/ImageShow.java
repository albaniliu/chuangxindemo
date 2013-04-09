package com.albaniliu.chuangxindemo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.albaniliu.chuangxindemo.widget.ViewPagerAdapter;

public class ImageShow extends Activity {
	private String mPath = Environment.getExternalStorageDirectory() + "/liangdemo1";
	private File mTestFolder = new File(mPath);
	private ViewPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.largepic);
		
		mAdapter = new ViewPagerAdapter(mTestFolder);
		ViewPager pager = (ViewPager) findViewById(R.id.photo_flow);
		pager.setOffscreenPageLimit(1);
		pager.setPageMargin(20);
		pager.setHorizontalFadingEdgeEnabled(true);
		pager.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_image_show, menu);
		return true;
	}

	public void onBackClick(View view) {
		finish();
	}
	
	public void onSlideClick(View view) {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), SlideShowActivity.class);
		startActivity(intent);
	}
}
