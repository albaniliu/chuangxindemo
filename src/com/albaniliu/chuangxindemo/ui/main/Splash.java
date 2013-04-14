package com.albaniliu.chuangxindemo.ui.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.albaniliu.chuangxindemo.R;

/**
 * Splash screen widget
 */
public class Splash extends UIWidget {
    private TextView  mTxtSplash; // information text
    private TextView  mTxtUpdate;
    private Animation mAnimation; // animation instance, now is pulling up in 2 seconds
                                  // private RatingBar mLoadingRatingBar;
                                  // constructor

    public Splash( Context context ) {
        super( context );
    }

    // constructor
    public Splash( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    @Override
    protected void loadResources() {
        LayoutInflater inflater = ( LayoutInflater ) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate( R.layout.splash, this, true);
    }

    /**
     * Set information text
     * 
     * @param aText text string
     */
    public void setText( String aText ) {
        mTxtSplash.setText( aText );
    }

    public void setUpdateText( String aText ) {
        mTxtUpdate.setText( aText );
    }

    /**
     * Start to close splash with custom animation
     * after closed notifyEvent() of listener will be invoked
     */
    public void startClosing() {
        mAnimation = AnimationUtils.loadAnimation( getContext(), R.anim.anim_splash );
        mAnimation.setAnimationListener( new AnimationListener() {
            @Override
            public void onAnimationEnd( Animation animation ) {
                notifyEvent( null );
            }

            @Override
            public void onAnimationRepeat( Animation animation ) {
            }

            @Override
            public void onAnimationStart( Animation animation ) {
            }
        } );
        this.startAnimation( mAnimation );
    }

    /**
     * the welcome stopped
     */
    public void stopLoading() {
        notifyEvent( null );
    }

    public void updateLoading( int step ) {
    }
}
