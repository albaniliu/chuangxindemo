package com.albaniliu.chuangxindemo.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class UIWidget extends LinearLayout {
    protected Context              mContext; // reference to parent
    protected IWidgetEventListener mListener; // now support only one event listener

    /**
     * Constructor
     * 
     * @param context control context
     */
    public UIWidget( Context context ) {
        super( context );
        mContext = context;
        loadResources();
    }

    /**
     * constructor
     * 
     * @param context control context
     * @param attrs attributes
     */
    public UIWidget( Context context, AttributeSet attrs ) {
        super( context, attrs );
        mContext = context;
        loadResources();
    }

    /**
     * Set event listener
     * 
     * @param aListener
     */
    public void setListener( IWidgetEventListener aListener ) {
        mListener = aListener;
    }

    /**
     * Reset the event listener
     */
    public void removeListener() {
        mListener = null;
    }

    /**
     * Notify event for the listener
     * 
     * @param aData
     */
    protected void notifyEvent( Object aData ) {
        if ( mListener != null ) {
            mListener.onEventOccured( new WidgetEventArgs( this, aData ) );
        }
    }

    // virtual
    /**
     * Initialize the widget itself
     */
    protected abstract void loadResources();
}
