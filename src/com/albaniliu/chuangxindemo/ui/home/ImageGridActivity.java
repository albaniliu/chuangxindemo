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

	public static final String Id = "HomeActivity";
	private LayoutParams bottomParams;

	private ExecutorService service = null;

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
		service = Executors.newSingleThreadExecutor();
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
		if (screenWidth > screenHeight) num = 3;
		for (int i = 0; i < num; i++) {
			LinearLayout classfiImage = (LinearLayout) getLayoutInflater().inflate(R.layout.classfi_image, null);
			FrameLayout frame = (FrameLayout) classfiImage.findViewById(R.id.left);
			LinearLayout des = (LinearLayout) classfiImage.findViewById(R.id.des_layout);
			des.setVisibility(View.GONE);
			frame.setPadding(5, 1, 5, 1);
			classfiLine.addView(classfiImage);
			frame.setOnClickListener(this);
		}
		
		
		
//		ImageView imageLeft = (ImageView) classfiLine
//				.findViewById(R.id.image_left);
//		ImageView imageRight = (ImageView) classfiLine
//				.findViewById(R.id.image_right);
//
//		TextView des1 = (TextView) classfiLine.findViewById(R.id.des1);
//		if (des1 != null)
//			des1.setText(contentDesID[(line - 1) * 2]);
//		TextView des2 = (TextView) classfiLine.findViewById(R.id.des2);
//		if (des2 != null)
//			des2.setText(contentDesID[(line - 1) * 2 + 1]);
//		if (imageLeft != null)
//			imageLeft.setImageBitmap(ResourceUtils.getDefaultClassfiBitmap());
//		if (imageRight != null)
//			imageRight.setImageBitmap(ResourceUtils.getDefaultClassfiBitmap());
//		FrameLayout left = (FrameLayout) classfiLine.findViewById(R.id.left);
//		if (left != null) {
//			left.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Bundle bundle = new Bundle();
//					bundle.putInt(BundleKeyWord.KEY_TYPE, (line - 1) * 2 + 1);
//					bundle.putString(BundleKeyWord.KEY_TITLE, getResources()
//							.getString(contentDesID[(line - 1) * 2]));
////					bundle.putInt(BundleKeyWord.KEY_DATA_SOURCE,
////							ImageDataManager.DATASORT);
//					bundle.putBoolean(BundleKeyWord.KEY_NOT_RETRIEVED, true);
//					bundle.putBoolean(BundleKeyWord.KEY_NEEDBACK, true);
//					bundle.putBoolean(BundleKeyWord.KEY_NEEDBACK_BUTTON, true);
//					bundle.putBoolean(BundleKeyWord.KEY_RESET_COLOR, true);
//					// iViewManager.getMainActivity().getTitleBar()
//					// .setColorButtonSrc(0);
////					iViewManager.showView(RecommendActivity.Id + SortView.Id,
////							RecommendActivity.class, true, bundle);
//
////					BaseActivity curActivity = (BaseActivity) iViewManager
////							.getCurrentActivity();
////					if (curActivity != null) {
////						int viewid = curActivity.getTraceViewID();
////						UserOperateTraceEngine.Trace(new UserOperation(
////								UserOperation.KENTERTHUMBVIEW).setView(viewid));
////					}
//				}
//
//			});
//		}
//		FrameLayout right = (FrameLayout) classfiLine.findViewById(R.id.right);
//		if (right != null) {
//			right.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Bundle bundle = new Bundle();
//					bundle.putInt(BundleKeyWord.KEY_TYPE, (line - 1) * 2 + 2);
//					bundle.putString(BundleKeyWord.KEY_TITLE, getResources()
//							.getString(contentDesID[(line - 1) * 2 + 1]));
////					bundle.putInt(BundleKeyWord.KEY_DATA_SOURCE,
////							ImageDataManager.DATASORT);
//					bundle.putBoolean(BundleKeyWord.KEY_NOT_RETRIEVED, true);
//					bundle.putBoolean(BundleKeyWord.KEY_NEEDBACK, true);
//					bundle.putBoolean(BundleKeyWord.KEY_NEEDBACK_BUTTON, true);
//					bundle.putBoolean(BundleKeyWord.KEY_RESET_COLOR, true);
//					// iViewManager.getMainActivity().getTitleBar()
//					// .setColorButtonSrc(0);
//
////					iViewManager.showView(RecommendActivity.Id + SortView.Id,
////							RecommendActivity.class, true, bundle);
//
////					BaseActivity curActivity = (BaseActivity) iViewManager
////							.getCurrentActivity();
////					if (curActivity != null) {
////						int viewid = curActivity.getTraceViewID();
////						UserOperateTraceEngine.Trace(new UserOperation(
////								UserOperation.KENTERTHUMBVIEW).setView(viewid));
////					}
//				}
//
//			});
//		}
		
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
