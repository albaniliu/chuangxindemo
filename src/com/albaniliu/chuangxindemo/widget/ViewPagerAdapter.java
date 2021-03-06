package com.albaniliu.chuangxindemo.widget;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.albaniliu.chuangxindemo.ImageShow.ShowingNode;
import com.albaniliu.chuangxindemo.util.Utils;
import com.albaniliu.chuangxindemo.widget.LargePicGallery.SingleTapListner;

public class ViewPagerAdapter extends PagerAdapter {
	private static final int BUFF_MAX = 5;

	private ArrayList<ShowingNode> mShowingNodes = new ArrayList<ShowingNode>();
	private SingleTapListner mListener;
	private int mGCount = 0;

    public ViewPagerAdapter(File folder, SingleTapListner listener) {
    	mListener = listener;
    	if (folder.isDirectory()) {
    		File[] files = folder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String path = pathname.getName();
                    return path.endsWith(".jpeg") || path.endsWith(".jpg");
                }
            });
    		for (File file : files) {
    			mShowingNodes.add(new ShowingNode(file.getPath(), "", ""));
    		}
    	}
    }
    
    public ViewPagerAdapter(ArrayList<ShowingNode> nodes, SingleTapListner listener) {
        mListener = listener;
        mShowingNodes = nodes;
    }

    @Override
    public void destroyItem(View collection, int position, Object arg2) {
    	BaseImageView view = (BaseImageView) arg2;
    	view.setImageBitmap(null);
    	((ViewPager) collection).removeView(view);
    	mGCount++;
    	if (mGCount == BUFF_MAX) {
    		System.gc();
    		mGCount = 0;
    	}
    }

    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mShowingNodes.size();
    }

    @Override
    public Object instantiateItem(View collection, int position) {
    	/*
        boolean inCollection = true;
        Bitmap bitmap = Utils.createBitmapByFilePath(mTestfiles.get(alphaPosition).getPath(), 1024);
    	int viewIndex = alphaPosition %= BUFF_MAX;
        if (mBuffImage[viewIndex] == null) {
            mBuffImage[viewIndex] = new BaseImageView(
            		collection.getContext(), bitmap.getWidth(), bitmap.getHeight());
            inCollection = false;
        }
        mBuffImage[viewIndex].setImageBitmap(bitmap);
        if (!inCollection) {
            ((ViewPager) collection).addView(mBuffImage[viewIndex]);
        }
        return mBuffImage[viewIndex];
        */
    	Bitmap bitmap = Utils.createBitmapByFilePath(mShowingNodes.get(position).getPath(), 800);
    	BaseImageView view = new BaseImageView(collection.getContext());
    	view.setTapUpListener(mListener);
        view.setImageBitmap(bitmap);
        view.setTag(Integer.valueOf(position));
        ((ViewPager) collection).addView(view);
        return view;
    }

    public String getName(int index) {
    	int size = mShowingNodes.size();
    	if (index >= size) {
    		return "";
    	}
    	return mShowingNodes.get(index).getContent() + "  " + (index + 1) + "/" + getCount(); 
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position,
            Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }
}