package com.albaniliu.chuangxindemo;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.albaniliu.chuangxindemo.ui.home.HomeActivity;
import com.albaniliu.chuangxindemo.ui.main.Splash;
import com.albaniliu.chuangxindemo.ui.main.ViewManager;
import com.albaniliu.chuangxindemo.util.Downloader;
import com.albaniliu.chuangxindemo.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	private boolean mTouch = false;
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTouch = false;
		mFlashThread = new PlayThread();
		mFlashThread.start();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("Main", "onStop");
		mFlashThread.setStoped(true);
	}

	private final int MSG_START_ACTIVITY = 0;
	private final int MSG_UPDATE_LOADING = 1;
	private final int MSG_EXIT_APP = 2;
	private final int MSG_NETWORK_LOAD_FAILED = 3;
	private final int MSG_HIDE_TITLE = 4;
	public final int MSG_UPDATEAPK = 5; // ��APK
	private final int MSG_NET_UNAVAILABLE = 6;
	private final int MSG_STOP_LOADING = 7;
	private final int MSG_FLASH_IMAGE = 8;
	private Splash mSplash; // splash widget
	private LinearLayout mFirstLayout;
	private LinearLayout mContentLayout;
	private ImageView mBackGround;
	private MainEventObserver mEventObserver; // control event observer
	
	private Button mImageButton;
	private Button mVideoButton;
	private ProgressDialog mLoadingDialog;
	
	private RelativeLayout mContainer; // the view container
	private ViewManager mViewManager;
	
	private PlayThread mFlashThread;
	int mResources[] = {R.drawable.meinv0, R.drawable.meinv1, R.drawable.meinv2, R.drawable.meinv3};
	int mBtnRes[] = {R.drawable.pic_manage_icon, R.drawable.video_manage_icon};
	int index = 0;
	private Rect mLeftButton = new Rect();
	private Rect mRightButton = new Rect();
	
	private MyView myView;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_START_ACTIVITY:
				mSplash.stopLoading();
				break;
			case MSG_UPDATE_LOADING:
				mSplash.updateLoading(msg.arg1);
				break;
			case MSG_EXIT_APP:
				mSplash.stopLoading();
				break;
			case MSG_STOP_LOADING:
				stopLoading();
			case MSG_FLASH_IMAGE:
				flashImage();
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initFields();
		mHandler.sendEmptyMessageDelayed(MSG_START_ACTIVITY, 2000);
		this.startService(new Intent(this , Downloader.class));
	}
	
	public class MyView extends View {
		
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			float x = event.getRawX();
			float y = event.getRawY();
			Log.i("MyView", "onTouch " + event.getRawX() + " " + event.getRawY());
			
			if (mLeftButton != null && y > mLeftButton.top && y < mLeftButton.bottom) {
    			if (x > mLeftButton.left && x < mLeftButton.right) {
    				mTouch = true;
    				Intent it = new Intent(MainActivity.this, HomeActivity.class);
    				startActivity(it);
    				return true;
    			} else if (x > mRightButton.left && x < mRightButton.right) {
    			    mTouch = true;
                    Intent it = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(it);
                    return true;
    			}
			}
			return super.onTouchEvent(event);
		}

		Context mContext;

		public MyView(Context context) {
			super(context);
			mContext = context;
			// TODO Auto-generated constructor stub
		}
		
		public void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint paint = new Paint(); 
			Rect dstRect  = new Rect(0, 0, getWidth(), getHeight());
			
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mResources[index]);
			Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
			canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

			Bitmap button = BitmapFactory.decodeResource(getResources(), mBtnRes[0]);
			srcRect = new Rect(0, 0, button.getWidth(), button.getHeight());
			int width = getWidth();
			int height = getHeight();
			int buttonSize = height > width ? 192 : 256;
			mRightButton.top = mLeftButton.top = (height - buttonSize) / 2;
			mRightButton.bottom = mLeftButton.bottom = mLeftButton.top + buttonSize;
			mLeftButton.left = (width - buttonSize * 2 - 60) / 2;
			mLeftButton.right = mLeftButton.left + buttonSize;
			mRightButton.left = mLeftButton.right + 60;
			mRightButton.right = mRightButton.left + buttonSize;
			
			canvas.drawBitmap(button, srcRect, mLeftButton, paint);
			
			button = BitmapFactory.decodeResource(getResources(), mBtnRes[1]);
            canvas.drawBitmap(button, srcRect, mRightButton, paint);
			
			index = ++index % mResources.length;
		}
	}
	
	private void initFields() {	
		mEventObserver = new MainEventObserver(this);
		mBackGround = (ImageView) findViewById(R.id.back_ground);
		mSplash = (Splash) findViewById(R.id.main_splash);
		mFirstLayout = (LinearLayout) findViewById(R.id.first_choose);
		mFirstLayout.setVisibility(View.GONE);
		mContentLayout = (LinearLayout) findViewById(R.id.content);
		mContentLayout.setVisibility(View.GONE);
		mSplash.setListener(mEventObserver);
		mSplash.setVisibility(View.VISIBLE);
		
		mImageButton = (Button) findViewById(R.id.image_button);
		mVideoButton = (Button) findViewById(R.id.video_button);
		
		mImageButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				downloadThread();
				Intent it = new Intent(MainActivity.this, HomeActivity.class);
				startActivity(it);  
			}
		});
		
		mContainer = (RelativeLayout) findViewById(R.id.containerBody);
		mViewManager = ViewManager.getInstance();
		mViewManager.setContainer(mContainer);

		// bundle data transferring doesn't support reference to object
		// unless it is serializable or parcelable. So use viewmgr to pass
		// eventobserver to next activity
		mViewManager.setMainActivity(this, mEventObserver);
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/liangdemo1/";
        File file = new File(dir);
        if (!file.exists()) {
        	file.mkdir();
        }
	}
	
	public void loading(Activity act) {
		mLoadingDialog = new ProgressDialog(act);
		mLoadingDialog.setTitle(R.string.dialog_title);
		mLoadingDialog.setMessage("请稍等..");
		mLoadingDialog.show();
	}
	
	public void stopLoading() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}
	}
	
	/**
	 * A utility function for view manager because it's not easy to pass all
	 * activity/view classes to view manager
	 * 
	 * @param aId
	 *            the activity Id
	 * @param aClass
	 *            the activity class
	 * @param aIsOnTopView
	 *            if activity is clear_top view or new_task view
	 * @param aBundle
	 *            the data bundle
	 * @return expected view instance
	 */
	public View getViewByProperties(String aId, Class<?> aClass,
			boolean aIsOnTopView, Bundle aBundle) {
		int flag = aIsOnTopView ? Intent.FLAG_ACTIVITY_CLEAR_TOP
				: Intent.FLAG_ACTIVITY_NEW_TASK;
		if (aClass == null)
			return null;
		return null;
	}
	
	/**
	 * Notified by event observer that splash screen has been closed because
	 * splash has animation
	 */
	public void onSplashClosed() {
		mSplash.setVisibility(View.GONE);
		myView = new MyView(this);
		setContentView(myView);
	}
	
	public void flashImage() {
		int r = mResources.length;
		mBackGround.setImageResource(mResources[index]);
		index = (++index % r);
	}
	
	class PlayThread extends Thread {
		private boolean isStoped = false;
		
		public boolean isStoped() {
			return isStoped;
		}

		public void setStoped(boolean isStoped) {
			this.isStoped = isStoped;
		}

		public void run() {
			while(!isStoped) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				if (myView != null)
					myView.postInvalidate();
			}
		}
		
	}
}
