package com.albaniliu.chuangxindemo.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * LargePicGallery 大图页界面gallery
 * 
 * @author Liang
 */
public class LargePicGallery extends ViewPager {
	private GestureDetector gestureScanner;
	private BaseImageView imageView;
	public int mScreenWidth;
	public int mScreenHeight;
	private SingleTapListner mTapUpListener;

	/**
	 * 监听左右滑动事件
	 */
	public static interface SingleTapListner {
		void onSingleTapUp();
	}

	public LargePicGallery(Context context) {
		super(context);
		gestureScanner = new GestureDetector(new MySimpleGesture());
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
	}

	public void setTapUpListener(SingleTapListner listener) {
		mTapUpListener = listener;
	}

	BaseImageView getImageView() {
		int pos = this.getCurrentItem();
		int count = getChildCount();
		for (int i = 0; i < count; ++i) {
			int index = (Integer) getChildAt(i).getTag();
			if (pos == index) {
				return (BaseImageView) getChildAt(i);
			}
		}
		return null;
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
		gestureScanner = new GestureDetector(new MySimpleGesture());
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
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					imageView.zoomTo(imageView.getScaleRate(),
							mScreenWidth / 2, mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				}

			} else {
				imageView.zoomTo(imageView.getFullScreenScaleRate(),
						mScreenWidth / 2, mScreenHeight / 2, 200f);
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
			if (mTapUpListener != null) {
				mTapUpListener.onSingleTapUp();
				return true;
			}
			return super.onSingleTapUp(e);
		}
	}

	float baseValue = 0;
	float originalScale = 1;
	float prePosX = 0f;
	float prePosY = 0f;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 双击事件和单击事件有冲突,当双击事件触发之后,单击ACTION_UP也同时触发,因为双击之后有一个"弹回"的效果,而弹回效果有200毫秒的间隔,
		// 这时单击事件已执行完毕(执行的时间又系统决定,所以就会出现误差时大时小),而双击之后的缩放没有完全,单击事件就认为此时已经缩放完毕,之后取现在的值进行计算,导致误差

		gestureScanner.onTouchEvent(event);
		imageView = getImageView();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			prePosX = event.getX();
			prePosY = event.getX();
			baseValue = 0;
			originalScale = imageView.getScale();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d(VIEW_LOG_TAG, "prePosX = " + prePosX + " prePosY = " + prePosY);

			if (event.getPointerCount() == 2) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				float value = (float) Math.sqrt(x * x + y * y);
				if (baseValue == 0) {
					baseValue = value;
				} else {
					float scale = value / baseValue;
					// scale the image
					imageView.zoomTo(originalScale * scale, mScreenWidth / 2f,
							mScreenHeight / 2f);
				}
				return true;
			} else {
				
				float curX = event.getX();
				float curY = event.getY();
				Log.d(VIEW_LOG_TAG, "curX = " + curX + " curY = " + curY);
				/*
				if (moveImagePosition((int)(curX - prePosX), (int) (curY - prePosY))) {
					prePosX = curX;
					prePosY = curY;
					return true;
				}
				*/
			}
			break;
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			BaseImageView imageView = getImageView();
			if (imageView.getScale() < imageView.getScaleRate()) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					imageView.zoomTo(imageView.getScaleRate(),
							mScreenWidth / 2, mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				} else {
					imageView.zoomTo(imageView.getFullScreenScaleRate(),
							mScreenWidth / 2, mScreenHeight / 2, 200f);
					imageView.layoutToCenter();
				}

			} else {
				float width = imageView.getScale() * imageView.getImageWidth();
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
						imageView
								.postTranslateDur(mScreenHeight - bottom, 200f);
					}
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean moveImagePosition(int disX, int disY) {
		
		float v[] = new float[9];
		Matrix m = imageView.getImageMatrix();
		m.getValues(v);

		// 图片实时的上下左右坐标
		float left, right;
		// 图片的实时宽，高
		float width, height;
		width = imageView.getScale() * imageView.getImageWidth();
		height = imageView.getScale() * imageView.getImageHeight();
		if ((int) height <= mScreenHeight) {
			disY = 0;
		}

		left = v[Matrix.MTRANS_X];
		Log.d(VIEW_LOG_TAG, "left = " + left);
		right = left + width;
		Rect r = new Rect();
		imageView.getGlobalVisibleRect(r);
		Log.d(VIEW_LOG_TAG, "rect = " + r.toShortString());
		if (disX > 1) {
			// 向左滑动

			if (r.left > 0) {// 判断当前ImageView是否显示完全
				return false;
			} else if (right <= mScreenWidth) {
				return false;
			} else {
				float maxDistance = right - mScreenWidth;
				if (disX > maxDistance) {
					disX = (int) maxDistance;
				}
				Log.d(VIEW_LOG_TAG, "postTranslate x = " + disX + " y = " + disY);
				imageView.postTranslate(-disX, -disY);
			}
			return true;
		} else if (disX < -1) {
			// 向右滑动

			if (r.right < mScreenWidth) {
				return false;
			} else if (left >= 0) {
				return false;
			} else {
				if (disX < left) {
					disX = (int) left;
				}
				Log.d(VIEW_LOG_TAG, "postTranslate x = " + disX + " y = " + disY);
				imageView.postTranslate(-disX, -disY);
			}
			return true;
		}
		return false;
	}
}
