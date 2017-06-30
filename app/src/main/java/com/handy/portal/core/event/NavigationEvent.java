package com.handy.portal.core.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;

public abstract class NavigationEvent extends HandyEvent {
    public static class SwapFragmentEvent extends NavigationEvent {
        public MainViewPage targetPage;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean addToBackStack;
        private Fragment mReturnFragment;
        private int mActivityRequestCode;

        public SwapFragmentEvent(final MainViewPage targetPage, final Bundle arguments,
                                 final TransitionStyle transitionStyle, final boolean addToBackStack) {
            this.targetPage = targetPage;
            this.addToBackStack = addToBackStack;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
        }

        public void setReturnFragment(@Nullable final Fragment returnFragment,
                                      final int requestCode) {
            mReturnFragment = returnFragment;
            mActivityRequestCode = requestCode;
        }

        @Nullable
        public Fragment getReturnFragment() {
            return mReturnFragment;
        }

        public int getActivityRequestCode() {
            return mActivityRequestCode;
        }
    }


    //show hide the tabs restrict navigation, also need to block the drawer?
    public static class SetNavigationTabVisibility extends NavigationEvent {
        public final boolean isVisible;

        public SetNavigationTabVisibility(boolean isVisible) {
            this.isVisible = isVisible;
        }
    }


    //Highlight the navigation tab
    public static class SelectPage extends NavigationEvent {
        public final MainViewPage page;

        public SelectPage(@Nullable final MainViewPage page) { this.page = page; }
    }

}
