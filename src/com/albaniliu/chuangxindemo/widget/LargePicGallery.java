package com.albaniliu.chuangxindemo.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

/**
 * LargePicGallery 大图页界面gallery
 * 
 * @author Liang
 */
public class LargePicGallery extends ViewPager {
	private GestureDetector gestureScanner;
	private OnScrollChangeLisener mOnScrollChangeLisener = null;
	private BaseImageView imageView;
	private Context mContext;
	public int mScreenWidth;
	public int mScreenHeight;
	private ViewPagerAdapter mAdapter;

	/**
	 * 监听左右滑动事件
	 */
	public interface OnScrollChangeLisener {
		void OnScrollChange(int kEvent);
	}

	public void setOnScrollChangeLisener(OnScrollChangeLisener lisener) {
		mOnScrollChangeLisener = lisener;
	}

	public LargePicGallery(Context context) {
		super(context);
		gestureScanner = new GestureDetector(new MySimpleGesture());
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
	}
	
	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (ViewPagerAdapter) adapter;
	}

	BaseImageView getImageView() {
		int pos = this.getCurrentItem();
		return mAdapter.getCurrentImage(pos);
	}

	/**
	 * Constructor
	 * 
	 */
	public LargePicGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		mContext = context;
		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				BaseImageView imageView = getImageView();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					baseValue = 0;
					originalScale = imageView.getScale();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (event.getPointerCount() == 2) {
						float x = event.getX(0) - event.getX(1);
						float y = event.getY(0) - event.getY(1);
						float value = (float) Math.sqrt(x * x + y * y);
						if (baseValue == 0) {
							baseValue = value;
						} else {
							float scale = value / baseValue;
							// scale the image
							imageView.zoomTo(originalScale * scale,
									mScreenWidth / 2f,
									mScreenHeight / 2f);
						}
					}
				}
			
				return false;
			}
		});
	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > e1.getX();
	}


	private class MySimpleGesture extends SimpleOnGestureListener {

		// 按两下的第二下Touch down时触发
		public boolean onDoubleTap(MotionEvent e) {
			// // 此处将return true改为让父类去处理相关方法,而不是自己去处理
			// View view = getGallerySelectedView();
			// view.setLayoutParams(new
			// Gallery.LayoutParams(mScreenWidth*2,mScreenHeight*2));
			// return super.onDoubleTap(e);
			BaseImageView imageView = getImageView();
			if (imageView.getScale() >= imageView.getFullScreenScaleRate()) {
				if (getResources().getConfiguration().orientation
		                == Configuration.ORIENTATION_PORTRAIT) {
					imageView.zoomTo(imageView.getScaleRate(),
							mScreenWidth / 2,
							mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				}

			} else {
				// loadLargePic(imageView);
				imageView.zoomTo(imageView.getFullScreenScaleRate(),
						mScreenWidth / 2,
						mScreenHeight / 2, 200f);
				imageView.layoutToCenter();
			}
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 双击事件和单击事件有冲突,当双击事件触发之后,单击ACTION_UP也同时触发,因为双击之后有一个"弹回"的效果,而弹回效果有200毫秒的间隔,
		// 这时单击事件已执行完毕(执行的时间又系统决定,所以就会出现误差时大时小),而双击之后的缩放没有完全,单击事件就认为此时已经缩放完毕,之后取现在的值进行计算,导致误差
		// float mtrans_x = values[Matrix.MTRANS_X];//左上顶点X坐标
		// float mtrans_y = values[Matrix.MTRANS_Y];//左上顶点Y坐标
		// float mscale_x = values[Matrix.MSCALE_X] ;//宽度缩放倍数
		// float mscale_y = values[Matrix.MSCALE_Y] ;//高度缩放位数

		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			BaseImageView imageView = getImageView();
			if (imageView.getScale() < imageView.getScaleRate()) {
				if (getResources().getConfiguration().orientation
		                == Configuration.ORIENTATION_PORTRAIT) {
					imageView.zoomTo(imageView.getScaleRate(),
							mScreenWidth / 2, mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				} else {
					imageView.zoomTo(imageView.getFullScreenScaleRate(),
							mScreenWidth / 2,
							mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				}

			} else {
				float width = imageView.getScale()
						* imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				if ((int) width <= mScreenWidth
						&& (int) height <= mScreenHeight) {
					// 如果图片当前大小<屏幕大小，判断边界
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (height > mScreenHeight) {
					if (top > 0) {
						imageView.postTranslateDur(-top, 200f);
					}
					if (bottom < mScreenHeight) {
						imageView.postTranslateDur(mScreenHeight - bottom,	200f);
					}
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
