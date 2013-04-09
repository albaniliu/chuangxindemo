package com.albaniliu.chuangxindemo.ui.main;

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Stack;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.albaniliu.chuangxindemo.MainActivity;

/**
 * View manager of whole UI structure<br/>
 * <br/>
 * All views in this application are created from corresponding activity
 * instance so to speak no matter what the starting type is, the view will be
 * alive during the whole life of this application<br/>
 * <br/>
 * In order to manage every activity starting actions, this view manager is
 * designed as a singleton instance which can be accessed from all activities
 * Once view manager gets the requests from activities, it retrieves view
 * instance from MainActivity and adds the view instance to mainAct's view
 * container.<br/>
 * <br/>
 * Also this view manager records a view stack for multiple level navigation
 * 
 * @author liuzhao
 * @see MainActivity
 */
public class ViewManager {
    private static ViewManager mInstance = null; // singleton instance
    private RelativeLayout mContainer; // reference to mainAct's
                                       // container
    private MainActivity mMainAct; // reference to main activity
    private ViewHistoryItem mCurrentView; // current view item
    private HashMap< Integer , Stack< ViewHistoryItem>> mViewHistory; // history
                                                                      // of
                                                                      // navigated
                                                                      // view
                                                                      // items
    private BaseActivity mNextActivity; // reference to the activity
                                        // which is going to be
                                        // switched to
    private IInitListener mInitListener; // reference to
                                         // initialization listener

    // constructor
    private ViewManager() {
        mViewHistory = new HashMap< Integer , Stack< ViewHistoryItem>>();
        mCurrentView = new ViewHistoryItem( null, null, "", false );
    }

    // Singleton portal
    public static ViewManager getInstance() {
        if ( mInstance == null ) {
            synchronized ( ViewManager.class ) {
                if ( mInstance == null ) {
                    mInstance = new ViewManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * Set view container
     * 
     * @param aContainer reference to view container from mainAct
     */
    public void setContainer( RelativeLayout aContainer ) {
        mContainer = aContainer;
    }

    /**
     * Set main activity
     * 
     * @param aMainAct reference to main activity
     * @param aInitListener reference to initialization listener
     */
    public void setMainActivity( MainActivity aMainAct,
            IInitListener aInitListener ) {
        mMainAct = aMainAct;
        mInitListener = aInitListener;
    }

    /**
     * Set the current activity by the activity that rightly switched to
     * 
     * @param aActivity this pointer of current activity
     */
    public void setCurrentActivity( BaseActivity aActivity ) {
        // this function is called earlier than showViewById, where we save the
        // current view to
        // history
        // that is, we have temporarily save the new activity info
        // interestingly (and sadly) during the first running of new_task view,
        // actually this function will be the later one!
        // so to speak this can cause null pointer crash...because no chance to
        // set _currentView
        mNextActivity = aActivity;
        // workaround for problem above
        if ( mCurrentView.getActivity() == null ) {
            mCurrentView = new ViewHistoryItem( mNextActivity,
                    mCurrentView.getType(), mCurrentView.getViewId(),
                    mCurrentView.getIsOnTop() );
        }
    }

    /**
     * Get reference to current activity
     * 
     * @return current activity
     */
    public Activity getCurrentActivity() {
        return mCurrentView.getActivity();
    }

    /**
     * Show a new view with new task property
     * 
     * @param aId activity Id
     * @param aNeedNavigatedBack if current view will be navigated back
     * @param aBundle data bundle
     */
    public void showView( String aId, Class< ? > aType,
            boolean aNeedNavigatedBack, Bundle aBundle ) {
        showViewById( aId, aType, false, aNeedNavigatedBack, aBundle, false,
                false );
    }

    public void showView( String aId, Class< ? > aType,
            boolean aNeedNavigatedBack, Bundle aBundle, boolean aTabChanging ) {
        showViewById( aId, aType, false, aNeedNavigatedBack, aBundle, false,
                aTabChanging );
    }

    /**
     * Show a new view with clear on top property
     * 
     * @param aId activity Id
     * @param aNeedNavigatedBack if current view will be navigated back
     * @param aBundle data bundle
     */
    public void showClearOnTopView( String aId, Class< ? > aType,
            boolean aNeedNavigatedBack, Bundle aBundle ) {
        showViewById( aId, aType, true, aNeedNavigatedBack, aBundle, false,
                false );
    }

    /**
     * 
     * @param aId
     * @param aType
     * @param aBundle
     * @param aTo
     */
    public void showViewAndChangeTab( String aId, Class< ? > aType,
            Bundle aBundle, int aTo ) {
        getCurrentHistory().push( new ViewHistoryItem( mCurrentView ) );
        Bundle bundle = new Bundle();
//        bundle.putInt( "from", getFocusedTabBarButton() );

        Stack< ViewHistoryItem> targetStack = mViewHistory.get( aTo );
        if ( targetStack.size() > 0 && targetStack.peek().getViewId() == aId ) {
            ViewHistoryItem viewItem = mViewHistory.get( aTo ).pop();
            if ( viewItem != null ) {
                showViewById( viewItem.getViewId(), viewItem.getType(), false,
                        false, bundle, false, false );
            }
        } else {
            showViewById( aId, aType, true, false, bundle, false, false );
        }
    }

    /**
     * If navigating back is allowed
     * 
     * @return yes or no
     */
    public boolean canGoBack() {
        boolean ret = false;
        if ( getCurrentHistory().size() > 0 ) {
            ret = true;
        }
        return ret;
    }

    /**
     * Go back to previous view with data bundle
     * 
     * @param aBundle data bundle
     * @throws EmptyStackException
     */
    public void goBack( Bundle aBundle ) throws EmptyStackException {
        try {
            ViewHistoryItem viewItem = getCurrentHistory().pop();
            addTraceLog( viewItem );
            showViewById( viewItem.getViewId(), viewItem.getType(),
                    viewItem.getIsOnTop(), false, aBundle, true, false );
        } catch ( EmptyStackException ex ) {
            throw ex;
        }
    }

    /**
     * 添加返回trace log
     * 
     * @param vhi 返回的界�?
     */
    private void addTraceLog( ViewHistoryItem vhi ) {

        if ( null == mCurrentView.getType() || null == vhi.getType() ) {
            return;
        }

//        if ( mCurrentView.getType().toString()
//                .equals( LargePiclActivity.class.toString() ) ) {
//            // 从大图页�?��
//            int historyId = getCurrentHistory().size() - 1;
//            if ( vhi.getType().toString()
//                    .equals( RecommendActivity.class.toString() ) ) {
//                // 返回推荐页面
//                if ( 0 > historyId ) {
//                    UserOperateTraceEngine.Trace( new UserOperation(
//                            UserOperation.KLOGRECTOLPA ) );
//                } else {
//
//                    if ( null == getCurrentHistory().get( historyId )
//                            || null == getCurrentHistory().get( historyId )
//                                    .getType() ) {
//                        return;
//                    }
//
//                    if ( getCurrentHistory().get( historyId ).getType()
//                            .toString().equals( SortView.class.toString() ) ) {
//                        // 由分类页面进�?
//                        UserOperateTraceEngine.Trace( new UserOperation(
//                                UserOperation.KLOGSORTTOLPA ) );
//                    } else if ( getCurrentHistory().get( historyId ).getType()
//                            .toString().equals( SearchView.class.toString() ) ) {
//                        // 由搜索�?进入
//                        UserOperateTraceEngine.Trace( new UserOperation(
//                                UserOperation.KLOGSERACHTOLPA ) );
//                    }
//                }
//            }
//        } else if ( mCurrentView.getType().toString()
//                .equals( RecommendActivity.class.toString() ) ) {
//            // 从推荐页面�?�?
//            if ( vhi.getType().toString().equals( SortView.class.toString() ) ) {
//                // 返回分类界面
//                UserOperateTraceEngine.Trace( new UserOperation(
//                        UserOperation.KLOGRECTOSORT ) );
//            } else if ( vhi.getType().toString()
//                    .equals( SearchView.class.toString() ) ) {
//                // 返回搜索界面
//                UserOperateTraceEngine.Trace( new UserOperation(
//                        UserOperation.KLOGSERACHTOSORT ) );
//            }
//        } else if ( mCurrentView.getType().toString()
//                .equals( HelpView.class.toString() ) ) {
//            // 帮助界面返回更多
//            UserOperateTraceEngine.Trace( new UserOperation(
//                    UserOperation.KLOGHELPBACK ) );
//        } else if ( mCurrentView.getType().toString()
//                .equals( AboutView.class.toString() ) ) {
//            // 关于界面返回更多
//            UserOperateTraceEngine.Trace( new UserOperation(
//                    UserOperation.KLOGABOUTBACK ) );
//        } else if ( mCurrentView.getType().toString()
//                .equals( FeedbackView.class.toString() ) ) {
//            // 反馈返回到更�?
//            UserOperateTraceEngine.Trace( new UserOperation(
//                    UserOperation.KLOGFEEDBACKBACK ) );
//        } else if ( mCurrentView.getType().toString()
//                .equals( AutoChangeView.class.toString() ) ) {
//            // 自动切换返回到更�?
//            UserOperateTraceEngine.Trace( new UserOperation(
//                    UserOperation.KLOGATUOCHANGEBACK ) );
//        }

    }

    /**
     * Go back to previous view without data bundle
     */
    public void goBack() {
        goBack( null );
    }

    /**
     * 
     * @param aTo
     */
    public void goBackAndChangeTab( int aTo ) {
        goBack();
    }

    /**
     * Clear view history
     */
    public void clearViewHistory() {
        if ( mViewHistory.size() > 0 ) {
            mViewHistory.clear();
        }
    }

    /**
     * Remove one view from current view container
     * 
     * @param aView reference to view
     */
    public void destroyView( View aView ) {
        mContainer.removeView( aView );
    }

    /**
     * Remove all views from current view container
     */
    public void removeAllViews() {
        mContainer.removeAllViews();
    }

    /**
     * Clear all data in view container and current view instance
     */
    public void finish() {
        removeAllViews();
        mCurrentView = new ViewHistoryItem( null, null, "", false );
    }

    /**
     * Get the initialization listener
     * 
     * @return reference to initialization listener
     */
    public IInitListener getInitListener() {
        return mInitListener;
    }

    /**
     * Set tab bar visibility
     * 
     * @param aVisible
     */
    public void setTabBarVisibility( boolean aVisible ) {
//        mMainAct.setTabBarVisibility( aVisible );
    }

    /**
     * Set the focused tab bar button
     * 
     * @param aButtonIndex
     */
    public void setFocusedButton( int aButtonIndex ) {
//        mMainAct.setFocusedButton( aButtonIndex );
    }

    /**
     * 
     * @return
     */
//    public int getFocusedTabBarButton() {
//        return mMainAct.getFocusedTabBarButtonIndex();
//    }

    /**
     * Tab changed handler, start activity if history got one (should always
     * be!) or let main activity handle this
     * 
     * @param aToIndex
     * @return
     */
    public boolean onTabBarFocusedButtonChanged( int aToIndex ) {
        boolean handled = false;
        getCurrentHistory().push( new ViewHistoryItem( mCurrentView ) );
        ViewHistoryItem viewItem = null;
        try {
            viewItem = mViewHistory.get( aToIndex ).pop();
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        if ( viewItem != null ) {
//            mMainAct.setFocusedButton( aToIndex );
            showViewById( viewItem.getViewId(), viewItem.getType(),
                    viewItem.getIsOnTop(), false, null, false, true );
            handled = true;
        }
        return handled;
    }

    public void restoreTab( int aIndex ) {
        mViewHistory.get( aIndex ).clear();
    }

    /**
     * Get view instance by Id
     * 
     * @param aId activity Id
     * @param aIsOnTopView if activity is clear_top view or new_task view
     * @param aBundle the data bundle
     * @return view instance
     */
    private View getViewById( String aId, Class< ? > aType,
            boolean aIsOnTopView, Bundle aBundle ) {
        return mMainAct.getViewByProperties( aId, aType, aIsOnTopView, aBundle );
    }

    /**
     * Show a new view with multiple properties
     * 
     * @param aId activity Id
     * @param aType
     * @param aIsOnTopView clearn on top view or new task view
     * @param aNeedNavigatedBack if current view will be navigated back
     * @param aBundle data bundle
     * @param isBacking
     * @param isTabChanging
     */
    private void showViewById( String aId, Class< ? > aType,
            boolean aIsOnTopView, boolean aNeedNavigatedBack, Bundle aBundle,
            boolean isBacking, boolean isTabChanging ) {
        if ( aId.equals( mCurrentView.getViewId() ) ) {
            // already showing the same activity, ignore request
            return;
        }

        mContainer.clearDisappearingChildren();
        
        // old view animation transitions
        if ( mCurrentView != null && mCurrentView.getActivity() != null
                && !isTabChanging ) { // we're
                                      // not
                                      // showing
                                      // animation
                                      // when
                                      // tab
                                      // changing
            View oldView = mCurrentView.getActivity().getWindow()
                    .getDecorView();
            if ( isBacking ) {
                animateCloseExit( oldView );
            } else {
                animateOpenExit( oldView );
            }
        }

        removeAllViews(); // = LinearLayout::removeAllViews();
        View view = getViewById( aId, aType, aIsOnTopView, aBundle );
        if ( view != null ) {
            // now handles new view's animation transition
            if ( !isTabChanging ) { // we're not showing animation when tab
                                    // changing
                if ( isBacking ) {
                    animateCloseEnter( view );
                } else {
                    animateOpenEnter( view );
                }
            }
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT,
                    RelativeLayout.LayoutParams.FILL_PARENT );
            mContainer.addView( view, p );
        }
        // for view won't need to be navigated back, it's not necessary to add
        // them to history stack
        if ( aNeedNavigatedBack ) {
            // create an exact copy of current view because stack only saves
            // references to objects.
            getCurrentHistory().push( new ViewHistoryItem( mCurrentView ) );
            // later pointer to current activity will be set later during
            // Activity::onCreated();
        }
        mCurrentView = new ViewHistoryItem( mNextActivity, aType, aId,
                aIsOnTopView );

    }

    /**
     * Get current history stack
     * 
     * @return
     */
    private Stack< ViewHistoryItem> getCurrentHistory() {
//        int index = getFocusedTabBarButton();
        return mViewHistory.get( 0 );
    }

    /**
     * Called to animate appearance of this activity as if somebody clicked next
     * on previous activity and ended up to this activity.
     * 
     * Animation: |<--
     */
    private void animateOpenEnter( View aView ) {
        animateTransition( aView, android.R.attr.activityOpenEnterAnimation );
    }

    /**
     * Called to animate appearance of this activity as if somebody clicked back
     * on previous activity and ended up to this activity.
     * 
     * Animation: -->|
     */
    private void animateCloseEnter( View aView ) {
        animateTransition( aView, android.R.attr.activityCloseEnterAnimation );
    }

    /**
     * Called to animate disappearance of this activity when "next" button was
     * clicked
     * 
     * Animation: <--|
     */
    private void animateOpenExit( View aView ) {
        animateTransition( aView, android.R.attr.activityOpenExitAnimation );
    }

    /**
     * Called to animate disappearance of this activity when "back" button was
     * clicked
     * 
     * Animation: |-->
     */
    private void animateCloseExit( View aView ) {
        animateTransition( aView, android.R.attr.activityCloseExitAnimation );
    }

    /**
     * 
     * @param animAttributeId
     */
    private void animateTransition( View aView, int aAnimAttributeId ) {
        TypedValue animations = new TypedValue();
        Theme theme = mMainAct.getTheme();

        theme.resolveAttribute( android.R.attr.windowAnimationStyle,
                animations, true );
        TypedArray animationArray = mMainAct.obtainStyledAttributes(
                animations.resourceId, new int[] { aAnimAttributeId } );

        int animResId = animationArray.getResourceId( 0, 0 );
        animationArray.recycle();

        if ( animResId != 0 ) {
            try {
                Animation anim = AnimationUtils.loadAnimation( mMainAct,
                        animResId );
                aView.startAnimation( anim );
            } catch ( Resources.NotFoundException ex ) {
            }
        }
    }

    public MainActivity getMainActivity() {
        return mMainAct;
    }

    public Activity getLastActivtiy() {
        Stack< ViewHistoryItem> curStack = getCurrentHistory();
        if ( curStack.empty() ) {
            return null;
        } else {
            return curStack.peek().getActivity();
        }
    }

    public Activity getPreActivtiy( BaseActivity activity ) {
        if ( getCurrentActivity() == activity ) {
            Stack< ViewHistoryItem> curStack = getCurrentHistory();
            if( curStack.empty()) {
                return null;
            } else {
                return curStack.peek().getActivity();
            }

        } else {

            Stack< ViewHistoryItem> curStack = getCurrentHistory();
            if ( curStack.empty() ) {
                return null;
            } else {
      
                ListIterator< ViewHistoryItem> iterators = curStack.listIterator();
                while ( iterators.hasNext() ) {
                    if ( iterators.next().getActivity() == activity ) {
                        iterators.previous();
                        if ( iterators.hasPrevious() ) {
                            return iterators.previous().getActivity();
                        }
                        break;
  
                    }
                }

                return null;
            }
        }
    }
}
