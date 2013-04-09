package com.albaniliu.chuangxindemo.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * 基础Activity 其它�?��子Activity都继承于该类
 * 
 * @author neusoft
 */
public abstract class BaseActivity extends Activity {
	public static String Id;
	protected ViewManager iViewManager = ViewManager.getInstance();
	protected String iBackText;
	static public boolean mbIsEditPage = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("BaseActivity", "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		Log.i("BaseActivity", "onResume");
		super.onResume();
		iViewManager.setCurrentActivity(this);
//		iViewManager.getMainActivity().getTitleBar()
//				.setVisibility(View.VISIBLE);
//		if (iBackText != null) {
//			iViewManager.getMainActivity().getTitleBar()
//					.setBackButton(iBackText, new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							goBack();
//						}
//					});
//		}
	}

	/**
	 * 响应按键消息
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("BaseActivity", "onKeyDown");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (iBackText != null) {
				goBack();
				return true;
			} else {
				return false;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 响应返回消息
	 */
	protected void goBack() {
		Log.i("BaseActivity", "goBack");
		if (iViewManager.canGoBack()) {
			try {
				iViewManager.goBack();
				iBackText = null;
			} catch (Exception nsee) {
				nsee.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */
	protected void onTitleBarButtonClick() {
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

	/**
	 * 获取view的trace id
	 * 
	 */
	public abstract int getTraceViewID();
}
