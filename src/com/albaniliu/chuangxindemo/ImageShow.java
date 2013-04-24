
package com.albaniliu.chuangxindemo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albaniliu.chuangxindemo.data.FInode;
import com.albaniliu.chuangxindemo.data.RandomDataSource;
import com.albaniliu.chuangxindemo.util.Downloader;
import com.albaniliu.chuangxindemo.util.Utils;
import com.albaniliu.chuangxindemo.widget.LargePicGallery;
import com.albaniliu.chuangxindemo.widget.LargePicGallery.SingleTapListner;
import com.albaniliu.chuangxindemo.widget.SlideShow;
import com.albaniliu.chuangxindemo.widget.ViewPagerAdapter;

public class ImageShow extends Activity implements View.OnClickListener {
    private static final String TAG = "ImageShow";
    private static final int GET_NODE_DONE = 1;
    private ViewPagerAdapter mAdapter;
    private LinearLayout mFlowBar;
    private TextView mFooter;
    private LargePicGallery mPager;
    private String mInodePath = "3,3";
    private int mCurIndex = 0;
    private int mSkipCount = 0;
    private SlideShow mSlideshow;
    private boolean mSlideShowMode = false;
    private RandomDataSource mRandomDataSource;
    
    private boolean mPopupVisible = false;
    private LinearLayout mPopup;
    private ImageButton mMenuBtn;
    private ScaleAnimation mInAnimation;
    private ScaleAnimation mOutAnimation;

    public static class ShowingNode {
        private String mPath;
        private String mName;
        private String mContent;

        public ShowingNode(String path, String name, String content) {
            mPath = path;
            mName = name;
            mContent = content;
        }

        public String getPath() {
            return mPath;
        }

        public String getName() {
            return mName;
        }

        public String getContent() {
            return mContent;
        }
    }

    private Handler mHanler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_NODE_DONE:
                    ArrayList<ShowingNode> nodes = parseFilesPath(mCurrentInode);
                    mCurIndex -= mSkipCount;
                    mRandomDataSource = new RandomDataSource(nodes, mCurIndex);
                    if (mSlideShowMode) {
                        mSlideshow.setDataSource(mRandomDataSource);
                    } else {
                        mAdapter = new ViewPagerAdapter(nodes, mListener);
                        mPager.setAdapter(mAdapter);
                        mFooter.setText(mAdapter.getName(mCurIndex));
                        mPager.setCurrentItem(mCurIndex);
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private Downloader mDownloadService;
    private FInode mCurrentInode;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        // 当我bindService时，让TextView显示MyService里getSystemTime()方法的返回值
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadService = ((Downloader.MyBinder) service).getService();
            Log.v(TAG, Boolean.toString(mDownloadService.isFinished()));
            mCurrentInode = mDownloadService.getLeaf(mInodePath);
            mHanler.sendEmptyMessage(GET_NODE_DONE);
        }

        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }
    };

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
        mHanler.removeCallbacks(mToggleRunnable);

        if (mFlowBar.getVisibility() == View.INVISIBLE
                || mFlowBar.getVisibility() == View.GONE) {
            mFlowBar.setVisibility(View.VISIBLE);
            mFooter.setVisibility(View.VISIBLE);
            Animation anim = new TranslateAnimation(0, 0, -delta, 0);
            anim.setDuration(300);
            mFlowBar.startAnimation(anim);
            Animation animDown = new TranslateAnimation(0, 0, delta, 0);
            animDown.setDuration(300);
            mFooter.startAnimation(animDown);
            mHanler.postDelayed(mToggleRunnable, 5000);
        } else {
            mFlowBar.setVisibility(View.INVISIBLE);
            mFooter.setVisibility(View.INVISIBLE);
            Animation anim = new TranslateAnimation(0, 0, 0, -delta);
            anim.setDuration(300);
            mFlowBar.startAnimation(anim);

            Animation animDown = new TranslateAnimation(0, 0, 0, delta);
            animDown.setDuration(300);
            mFooter.startAnimation(animDown);
        }
    }
    
    private void showBarDirectly() {
        mFlowBar.setVisibility(View.VISIBLE);
        mFooter.setVisibility(View.VISIBLE);
        mHanler.postDelayed(mToggleRunnable, 5000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.largepic);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mSlideshow = (SlideShow) findViewById(R.id.slideshow);
        mSlideShowMode = getIntent().getBooleanExtra("slideshow", false);
        if (mSlideShowMode) {
            mSlideshow.setVisibility(View.VISIBLE);
            mSlideshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            mSlideshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSlideshow.setVisibility(View.GONE);
                    showBarDirectly();
                }
            });
        }

        mPager = (LargePicGallery) findViewById(R.id.photo_flow);
        mPager.setOffscreenPageLimit(1);
        mPager.setPageMargin(20);
        mPager.setHorizontalFadingEdgeEnabled(true);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mFooter.setText(mAdapter.getName(position));
                mCurIndex = position;
            }
        });
        mFlowBar = (LinearLayout) findViewById(R.id.flow_bar);
        mFooter = (TextView) findViewById(R.id.footer_bar);

        Intent i = new Intent();
        i.setClass(this, Downloader.class);
        this.bindService(i, mServiceConnection, BIND_AUTO_CREATE);
        mInodePath = getIntent().getStringExtra("inode_path");
        
        Log.d(TAG, "~~ InodePath = " + mInodePath);
        if (!mSlideShowMode) {
            mCurIndex  = getIntent().getIntExtra("index", 0);
        } else {
            mCurIndex = 0;
        }
        
        mHanler.postDelayed(mToggleRunnable, 5000);
        
        mPopup = (LinearLayout) findViewById(R.id.menu_pop_up);
        Button refresh = (Button) findViewById(R.id.menu_refresh);
        refresh.setVisibility(View.GONE);
        findViewById(R.id.line).setVisibility(View.GONE);
        int popupButtonCount = mPopup.getChildCount();
        for (int index = 0; index < popupButtonCount; index++) {
            mPopup.getChildAt(index).setOnClickListener(this);
        }
        mMenuBtn = (ImageButton) findViewById(R.id.menu_btn);
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

    @Override
    public void onStop() {
        super.onStop();
        getIntent().putExtra("index", mCurIndex);
    }

    @Override
    public void onBackPressed() {
        if (!mSlideShowMode && mSlideshow.getVisibility() == View.VISIBLE) {
            mSlideshow.setVisibility(View.GONE);
            showBarDirectly();
            return;
        }
        super.onBackPressed();
    }

    public void onSlideClick(View view) {
        mSlideshow.setDataSource(mRandomDataSource);
        mSlideshow.setVisibility(View.VISIBLE);
    }

    private ArrayList<ShowingNode> parseFilesPath(FInode inode) {
        ArrayList<ShowingNode> nodes = new ArrayList<ShowingNode>();
        mSkipCount = 0;
        JSONArray array = inode.getDirs();
        if (array != null) {
            int count  = array.length();
            for (int i = 0; i < count; ++i) {
                JSONObject obj;
                try {
                    obj = array.getJSONObject(i);
                    boolean image = TextUtils.equals("image", obj.getString("attrib"));
                    if (image) {
                        String path = obj.getString("path");
                        String name = obj.getString("name");
                        String content = obj.getString("content");
                        int start = path.lastIndexOf('/') + 1;
                        String nodePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/liangdemo1/" + path.substring(start);
                        nodes.add(new ShowingNode(nodePath, name, content));
                    } else {
                        mSkipCount++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return nodes;
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
                break;
            case R.id.menu_more:
            	onSlideClick(v);
                break;
            case R.id.whole:
                break;
        }
        
    }
}
