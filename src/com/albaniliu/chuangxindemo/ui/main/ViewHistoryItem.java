package com.albaniliu.chuangxindemo.ui.main;

/**
 * View history item
 * 
 * @author liuzhao
 * 
 */
public class ViewHistoryItem {
    private Class<?>     mType;
    private boolean      mIsOnTop; // is view a on top view
    private BaseActivity mActivity; // reference to activity
    private String       mViewId;  // activity Id

    public Class<?> getType() {
        return mType;
    }

    /**
     * @return yes/no
     */
    public boolean getIsOnTop() {
        return mIsOnTop;
    }

    /**
     * 
     * @return reference to activity
     */
    public BaseActivity getActivity() {
        return mActivity;
    }

    /**
     * 
     * @return activity Id
     */
    public String getViewId() {
        return mViewId;
    }

    /**
     * Constructor
     * 
     * @param aActivity reference to activity
     * @param aViewId activity Id
     * @param aIsOnTop is view a on top view
     */
    public ViewHistoryItem( BaseActivity aActivity, Class<?> aType, String aViewId, boolean aIsOnTop ) {
        mActivity = aActivity;
        mType = aType;
        mViewId = aViewId;
        mIsOnTop = aIsOnTop;
    }

    /**
     * Copy constructor
     * 
     * @param aItem
     */
    public ViewHistoryItem( ViewHistoryItem aItem ) {
        mActivity = aItem.getActivity();
        mType = aItem.getType();
        mViewId = aItem.getViewId();
        mIsOnTop = aItem.getIsOnTop();
    }

}
