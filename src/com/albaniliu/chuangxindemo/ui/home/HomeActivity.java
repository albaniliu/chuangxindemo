
package com.albaniliu.chuangxindemo.ui.home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albaniliu.chuangxindemo.R;
import com.albaniliu.chuangxindemo.data.FInode;
import com.albaniliu.chuangxindemo.util.Downloader;
import com.albaniliu.chuangxindemo.util.ResourceUtils;
import com.albaniliu.chuangxindemo.util.Utils;

public class HomeActivity extends Activity implements View.OnClickListener {
    private static String TAG = "HomeActivity";

    int[] contentDesID = new int[] {
            R.string.beauty, R.string.creative,
            R.string.nonmainstream, R.string.cartoon, R.string.love,
            R.string.landscape, R.string.animation, R.string.star,
            R.string.plant, R.string.art, R.string.animal, R.string.colorful,
            R.string.car, R.string.man, R.string.sky, R.string.sport
    };

    public static final int MSG_CHECK_HOME_RESOURCE_LOADING = 1001;
    public static final int MSG_DOWNLOAD_FINISHED = 1002;
    public static final int MSG_DOWNLOAD_FAILED = 1003;
    public static int DEFAULT_BANNER_COUNT = 5;

    public static final String Id = "HomeActivity";
    private LayoutParams bottomParams;

    private LinearLayout classfiView;
    private ProgressDialog  dialog;
    private JSONArray allDir;
    private FInode currentInode;
    private int totalIndex;
    private Downloader downloader;
    
    private boolean mPopupVisible = false;
    private LinearLayout mPopup;
    private ImageButton mMenuBtn;

    private ScaleAnimation mInAnimation;
    private ScaleAnimation mOutAnimation;
    
    private MyReceiver receiver;

    private Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
		    super.handleMessage(msg);
		    if (msg.what == MSG_DOWNLOAD_FINISHED) {
		    	currentInode = downloader.getRoot();
		    	setDefaultClassfiView();
		    	if (dialog.isShowing()) {
		    		dialog.dismiss();
		    	}
		    	classfiView.setVisibility(View.VISIBLE);
		    } else if (msg.what == MSG_DOWNLOAD_FAILED) {
		        Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_LONG).show();
		    }
	    }
    };
    
    public class MyReceiver extends BroadcastReceiver {
		//自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.v(TAG, "OnReceiver");
			Bundle bundle = intent.getExtras();
			int a = bundle.getInt("process");
			if (a == 100) {
				mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD_FINISHED, 1000);
			} else {
				mHandler.sendEmptyMessage(MSG_DOWNLOAD_FAILED);
			}
			//处理接收到的内容

		}
	}
    
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        //当我bindService时，让TextView显示MyService里getSystemTime()方法的返回值   
        public void onServiceConnected(ComponentName name, IBinder service) {  
            // TODO Auto-generated method stub
        	downloader = ((Downloader.MyBinder)service).getService();
        	downloader.refresh();
        	Log.v(TAG, Boolean.toString(downloader.isFinished()));
        	if (downloader.isFinished()) {
	        	allDir = downloader.getAllDir();
	        	currentInode = downloader.getRoot();
	        	mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD_FINISHED, 200);
        	}
        }
          
        public void onServiceDisconnected(ComponentName name) {  
            // TODO Auto-generated method stub  
              
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("HomeActivity", "onCreate");
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.home_activity);
        ResourceUtils.setContext(this);
        classfiView = (LinearLayout) this.findViewById(R.id.classfi_view);
        
        classfiView.setVisibility(View.GONE);
        if (dialog == null) {
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setTitle(R.string.dialog_title);
            dialog.setMessage("请稍等..");
        }
        dialog.show();
        allDir = new JSONArray();
        currentInode = new FInode();
        this.startService(new Intent(this , Downloader.class));
        Intent i  = new Intent();
        i.setClass(HomeActivity.this, Downloader.class);
        this.bindService(i, mServiceConnection, BIND_AUTO_CREATE);
        
        mPopup = (LinearLayout) findViewById(R.id.menu_pop_up);
        int popupButtonCount = mPopup.getChildCount();
        for (int index = 0; index < popupButtonCount; index++) {
            mPopup.getChildAt(index).setOnClickListener(this);
        }
        mMenuBtn = (ImageButton) findViewById(R.id.menu_btn);

        receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.albaniliu.chuangxindemo.action.downloader");
		this.registerReceiver(receiver, filter);
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(receiver);
		this.unbindService(mServiceConnection);
	}

    private void setDefaultClassfiView() {
        classfiView.removeAllViews();
        totalIndex = 0;
        int line = 0;
        Log.v(TAG, Boolean.toString(downloader.isFinished()));
        allDir = currentInode.getDirs();
        while (totalIndex < allDir.length()) {
            setDefaultClassfiLine(line++);
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
        int padding = 24;
        if (screenWidth > screenHeight) {
        	num = 4;
        	padding = 12;
        }
        classfiLine.setPadding(padding, 1, padding, 0);
        for (int i = 0; i < num && totalIndex < allDir.length(); i++, totalIndex++) {
            LinearLayout classfiImage = (LinearLayout) getLayoutInflater().inflate(
                    R.layout.classfi_image, null);
            FrameLayout frame = (FrameLayout) classfiImage.findViewById(R.id.left);
            MyOnClickListener listener = new MyOnClickListener();
            listener.setIndex(totalIndex);
            frame.setOnClickListener(listener);
            try {
            	JSONObject obj = (JSONObject) allDir.get(totalIndex);
	            ImageView image = (ImageView) classfiImage.findViewById(R.id.image_left);
	            String coverPath = obj.getString("cover");
	            Log.v(TAG, coverPath);
                String coverName = coverPath.substring(coverPath.lastIndexOf('/') + 1);
	            String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/liangdemo1/"
	                    + coverName;
	            Bitmap bitmap = Utils.createBitmapByFilePath(fileName, 200);
	            image.setImageBitmap(bitmap);
	            
	            TextView txt = (TextView) classfiImage.findViewById(R.id.des);
				txt.setText(obj.getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            classfiLine.addView(classfiImage);
        }

        classfiView.addView(classfiLine);
    }
    
    public void onMenuClick(View view) {
        if (mPopupVisible) {
            hidePopup();
        } else {
            showPopup();
        }
    }

    private void showPopup() {
        if (!mPopupVisible) {
            mPopupVisible = true;
            mMenuBtn.setImageResource(R.drawable.titlebar_icon_more_hl);
            if (mInAnimation == null) {
                mInAnimation = new ScaleAnimation(
                        0, 1, 0, 1, mPopup.getWidth() - Utils.dip2px(this, 19), 0);
                mInAnimation.setDuration(300);
                mInAnimation.setInterpolator(this, android.R.anim.accelerate_interpolator);
                mInAnimation.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        mPopup.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPopup.setVisibility(View.VISIBLE);
                    }
                });
            }
            mPopup.setAnimation(null);
            mPopup.startAnimation(mInAnimation);
        }
    }

    private void hidePopup() {
        if (mPopupVisible) {
            mPopupVisible = false;
            mMenuBtn.setImageResource(R.drawable.titlebar_icon_more);
            if (mOutAnimation == null) {
                mOutAnimation = new ScaleAnimation(
                        1, 0, 1, 0, mPopup.getWidth() - Utils.dip2px(this, 19), 0);
                mOutAnimation.setDuration(300);
                mOutAnimation.setInterpolator(this, android.R.anim.accelerate_interpolator);
                mOutAnimation.setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mPopup.setVisibility(View.INVISIBLE);
                        mMenuBtn.setImageResource(R.drawable.more_btn_selector);
                    }
                });
            }
            mPopup.setAnimation(null);
            mPopup.startAnimation(mOutAnimation);
        }
    }

    @Override
    public void onClick(View v) {
        hidePopup();
        switch(v.getId()) {
            case R.id.menu_refresh:
                downloader.refreshForce();
                dialog.show();
                break;
            case R.id.menu_more:
                break;
            case R.id.whole:
                break;
        }
        
    }
    
    class MyOnClickListener implements OnClickListener {

    	private int index;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			currentInode = currentInode.getChildren().get(index);
			if (currentInode.isLeaf()) {
                Bundle bundle = new Bundle();
                FInode parent = currentInode.getParent();
                StringBuilder value = new StringBuilder();
                if (!currentInode.isRoot())
                	value.append(currentInode.getIndex());
                while (!parent.isRoot()) {
                	value.append(",");
                	value.append(parent.getIndex());
                	parent = parent.getParent();
                }
                Log.v(TAG, "inode_path: " + value.toString());
                bundle.putString("inode_path", value.toString());
                Intent it = new Intent(HomeActivity.this, ImageGridActivity.class);
                it.putExtras(bundle);
                startActivity(it);
        	} else {
        		setDefaultClassfiView();
        	}
		}
    	
    }
}
