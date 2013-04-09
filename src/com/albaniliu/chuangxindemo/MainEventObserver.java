package com.albaniliu.chuangxindemo;

import com.albaniliu.chuangxindemo.ui.main.IInitListener;
import com.albaniliu.chuangxindemo.ui.main.IWidgetEventListener;
import com.albaniliu.chuangxindemo.ui.main.Splash;
import com.albaniliu.chuangxindemo.ui.main.WidgetEventArgs;

/**
 * Event observer of main activity, the wrapper of all kinds of events
 * 
 * @author neusoft
 * @see IWidgetEventListener, IInitListener
 */
public class MainEventObserver implements IWidgetEventListener, IInitListener {
    private MainActivity mMainAct; // the only event listener

    // constructor
    public MainEventObserver( MainActivity aMainAct ) {
        mMainAct = aMainAct;
    }

    /**
     * From IWidgetEventListener
     */
    @Override
    public void onEventOccured( WidgetEventArgs aEventArgs ) {
        Object sender = aEventArgs.Sender;

        if ( sender == null ) {
            return;
        }
        // splash animation ended
        if ( aEventArgs.Sender instanceof Splash ) {
            mMainAct.onSplashClosed();
        }
    }

    /**
     * From IInitListener, initialization started
     */
    @Override
    public void onInitStarted() {
        // no implementation
    }

    /**
     * From IInitListener, initialization message updated
     */
    @Override
    public void updateMessage( String aMessage ) {
        // no implementation
    }

    /**
     * From IInitListener, initialization ended
     */
    @Override
    public void onInitEnded() {
        // no implementation
    }
}
