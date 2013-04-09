package com.albaniliu.chuangxindemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.albaniliu.chuangxindemo.R;

/**
 *
 * 
 * @author liuzhao
 * @since 1.0
 */
public class ResourceUtils {
	private static Context mMainContext; // resource handler
	private static Bitmap defaultClassfiBitmap;
	
	private static int classfiHeight = 0;
	private static int classfiWidth = 0;
	private static int originalClassfiHeight = 158;
	private static int originalClassfiWidth = 237;


	public static void setContext(Context aContext) {
		mMainContext = aContext;
	}

	public static Bitmap getDefaultClassfiBitmap() {
		if (defaultClassfiBitmap == null) {
			Bitmap bitmap = BitmapFactory.decodeResource(
					mMainContext.getResources(), R.drawable.grid_item_bg);
			defaultClassfiBitmap = Bitmap.createScaledBitmap(bitmap,
					getClassfiWidth(), getClassfiHeight(), false);
		}
		return defaultClassfiBitmap;
	}
	
	public static int getClassfiHeight() {
		if (classfiHeight == 0) {
			classfiHeight = getClassfiWidth() * originalClassfiHeight
					/ originalClassfiWidth;
		}
		return classfiHeight;
	}

	public static int getClassfiWidth() {
		if (classfiWidth == 0) {
			classfiWidth = mMainContext.getResources().getDisplayMetrics().widthPixels / 2 - 3;
		}
		return classfiWidth;

	}

}
