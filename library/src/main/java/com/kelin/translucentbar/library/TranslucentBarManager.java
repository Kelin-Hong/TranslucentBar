package com.kelin.translucentbar.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by kelin on 2017/5/8.
 */

public class TranslucentBarManager {
    private static final String TAG_STATUS_BAR_VIEW = "status_bar_view";
    private Toolbar mToolbar;
    private ViewGroup mRoot;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    //window flags
    private boolean mTranslucentStatusBar;
    //a tag mark whether have view would show behind status bar or not
    private boolean mIsNeedOverStatusBar = false;
    //mStatusBarView will put the place behind status bar and it to be a child of ContentFrameLayout(android:id/content)
    private View mStatusBarView;

    private SystemConfig mSystemConfig;


    public TranslucentBarManager(Activity activity) {
        Window win = activity.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // check theme attrs
            int[] attrs = {android.R.attr.windowTranslucentStatus,
                    android.R.attr.windowTranslucentNavigation};
            TypedArray a = activity.obtainStyledAttributes(attrs);
            try {
                mTranslucentStatusBar = a.getBoolean(0, false);
            } finally {
                a.recycle();
            }

            // check window flags
            WindowManager.LayoutParams winParams = win.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((winParams.flags & bits) != 0) {
                mTranslucentStatusBar = true;
            }

        }
        mSystemConfig = new SystemConfig(activity, mTranslucentStatusBar);
        mRoot = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        findToolbar(mRoot);
        findCollapsingToolbarLayout(mRoot);
    }


    public TranslucentBarManager(Fragment fragment) {
        Window win = fragment.getActivity().getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // check theme attrs
            int[] attrs = {android.R.attr.windowTranslucentStatus,
                    android.R.attr.windowTranslucentNavigation};
            TypedArray a = fragment.getActivity().obtainStyledAttributes(attrs);
            try {
                mTranslucentStatusBar = a.getBoolean(0, false);
            } finally {
                a.recycle();
            }

            // check window flags
            WindowManager.LayoutParams winParams = win.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((winParams.flags & bits) != 0) {
                mTranslucentStatusBar = true;
            }

        }
        mSystemConfig = new SystemConfig(fragment.getActivity(), mTranslucentStatusBar);

        //remove view behind status bar when fragment may be has change,
        ViewGroup decorViewGroup = (ViewGroup) fragment.getActivity().getWindow().getDecorView()
                .findViewById(android.R.id.content);
        for (int i = 0; i < decorViewGroup.getChildCount(); i++) {
            Object tag = decorViewGroup.getChildAt(i).getTag();
            if (tag != null && tag instanceof String && tag.toString().equals(TAG_STATUS_BAR_VIEW)) {
                decorViewGroup.removeViewAt(i);
                break;
            }
        }
    }

    /**
     * tint toolbar and status to specified color
     *
     * @param activity activity
     * @param color    color to tint status bar and toolbar
     */
    private void tintColor(Activity activity, @ColorRes int color) {
        if (!mIsNeedOverStatusBar) {
            ViewGroup contentLayout = (ViewGroup) activity.getWindow().getDecorView().
                    findViewById(android.R.id.content);
            setupStatusBarView(activity, contentLayout);
            mStatusBarView.setBackgroundResource(color);
            mStatusBarView.setVisibility(View.VISIBLE);
            mToolbar.setBackgroundResource(color);
        } else {
            mCollapsingToolbarLayout.setContentScrimResource(color);
            mCollapsingToolbarLayout.setStatusBarScrimResource(color);
        }
    }

    /**
     * make status bar translucent and tint toolbar and status to specified color
     * this method must call when onCreateView in fragment to take effect.
     *
     * @param fragment fragment
     * @param root     the root view of fragment
     * @param color    color to tint status bar and toolbar
     */
    public void translucent(Fragment fragment, View root, @ColorRes int color) {
        mRoot = (ViewGroup) root;
        findToolbar(mRoot);
        findCollapsingToolbarLayout(mRoot);
        translucent(fragment.getActivity(), color);
        addStatusBarShade(fragment.getActivity());
    }

    /**
     * make status bar translucent and tint toolbar and status to specified color
     *
     * @param activity activity
     * @param color    toolbar and status bar would be tinted this color
     */
    public void translucent(Activity activity, @ColorRes int color) {
        if (!mTranslucentStatusBar || mToolbar == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setFitsSystemWindows(mRoot, false);
            mToolbar.getLayoutParams().height = mSystemConfig.getActionBarHeight() +
                    mSystemConfig.getStatusBarHeight();
            mToolbar.setPadding(mToolbar.getPaddingLeft(), mSystemConfig.getStatusBarHeight(),
                    mToolbar.getPaddingRight(), mToolbar.getPaddingBottom());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setFitsSystemWindows(mRoot, true);
        }
        if (color <= 0) {
            TypedValue tv = new TypedValue();
            activity.getTheme().resolveAttribute(android.R.attr.colorPrimary, tv, true);
            tintColor(activity, tv.resourceId);
        } else {
            tintColor(activity, color);
        }

    }

    /**
     * make status bar translucent and tint color,if you don't set any colors,toolbar would be tinted with @color/colorPrimary
     * and status bar would be tinted with @color/colorPrimaryDark
     *
     * @param activity acivity
     */
    public void translucent(Activity activity) {
        translucent(activity, -1);
    }

    /**
     * when set windowTranslucentStatus true, system will add translucent gray shade above of status bar,this is
     * standard style of material design，but some times，you may want to remove the shade, for example: if the
     * color of ActionBar is white and now show a gray shape above of white status bar is so ugly exactly！
     * make status bar transparent,if you don't set colors,toolbar would be tinted whit @color/colorPrimary
     * and status bar would be tinted with @color/colorPrimaryDark
     *
     * @param activity activity
     * @param color    color to tint status bar and toolbar
     */
    public void transparent(Activity activity, @ColorRes int color) {
        translucent(activity, color);
        removeStatusBarShade(activity);
    }


    /**
     * when set windowTranslucentStatus true, system will add translucent gray shade above of status bar,this is
     * standard style of material design，but some times，you may want to remove the shade, for example: if the
     * color of ActionBar is white and now show a gray shape above of white status bar is so ugly exactly！
     * make status bar transparent,if you don't set colors,toolbar would be tinted with @color/colorPrimary
     * and status bar would be tinted with @color/colorPrimaryDark
     *
     * @param activity activity
     */
    public void transparent(Activity activity) {
        translucent(activity, -1);
        removeStatusBarShade(activity);

    }


    /**
     * make status bar transparent and tint toolbar and status to specified color
     * this method must call when onCreateView in fragment to take effect.
     *
     * @param fragment fragment
     * @param root     the root view of fragment
     * @param color    color to tint status bar and toolbar
     */
    public void transparent(Fragment fragment, View root, @ColorRes int color) {
        mRoot = (ViewGroup) root;
        findToolbar(mRoot);
        findCollapsingToolbarLayout(mRoot);
        translucent(fragment.getActivity(), color);
        removeStatusBarShade(fragment.getActivity());
    }


    /**
     * contrary to {@link TranslucentBarManager#transparent(Activity)}  }
     *
     * @param activity
     */
    private void removeStatusBarShade(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }


    /**
     * @param activity
     */
    private void addStatusBarShade(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    /**
     * @param context            Context to create view
     * @param contentFrameLayout child of DecorView,get by decorView.findViewById(android.R.id.content)
     */
    private void setupStatusBarView(Context context, ViewGroup contentFrameLayout) {
        mStatusBarView = new View(context);
        //set tag to mark view,Chances are mStatusBarView will be remove.
        mStatusBarView.setTag(TAG_STATUS_BAR_VIEW);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                mSystemConfig.getStatusBarHeight());
        params.gravity = Gravity.TOP;
        mStatusBarView.setLayoutParams(params);
        mStatusBarView.setVisibility(View.GONE);
        contentFrameLayout.addView(mStatusBarView);
    }

    /**
     * Recursive process view tree to setFitsSystemWindows（true or false）until find specify views
     *
     * @param view root view
     * @param b    setFitsSystemWindows（true or false）
     */
    private void setFitsSystemWindows(View view, boolean b) {
        view.setFitsSystemWindows(b);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.getChildCount() > 0) {
                View child = viewGroup.getChildAt(0);
                // if find CollapsingToolbarLayout then return,the deeper view setFitsSystemWindows is not work any morel!
                if (viewGroup instanceof CollapsingToolbarLayout) {
                    child.setFitsSystemWindows(b);
                    return;
                }
                // if find AppBarLayout or Toolbar then return,the deeper view setFitsSystemWindows(true) would get UI bugs!
                if (!mIsNeedOverStatusBar && (child instanceof AppBarLayout || child instanceof Toolbar)) {
                    return;
                }

                setFitsSystemWindows(child, b);

            }
        }
    }

    /**
     * Recursive query to find toolbar in view tree
     *
     * @param view root view
     */
    private void findToolbar(View view) {
        if (mToolbar != null) {
            return;
        }
        if (view instanceof Toolbar) {
            mToolbar = (Toolbar) view;
            return;
        } else {
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View child = viewGroup.getChildAt(i);
                    findToolbar(child);
                }
            }
        }
    }

    /**
     * Recursive query to find collapsingToolbarLayout in view tree and judge if this layout need
     * to show view behind status bar(mIsNeedOverStatusBar).
     *
     * @param view root view
     */
    private void findCollapsingToolbarLayout(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.getChildCount() > 0) {
                View child = viewGroup.getChildAt(0);
                if (child instanceof CollapsingToolbarLayout && viewGroup instanceof AppBarLayout) {
                    if (!(((CollapsingToolbarLayout) child).getChildAt(0) instanceof Toolbar)) {
                        mCollapsingToolbarLayout = (CollapsingToolbarLayout) child;
                        mIsNeedOverStatusBar = true;
                    }
                }
                findCollapsingToolbarLayout(child);
            }
        }
    }

}
