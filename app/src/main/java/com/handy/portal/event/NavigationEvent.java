package com.handy.portal.event;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;

public abstract class NavigationEvent extends HandyEvent
{
    public static class NavigateToPage extends NavigationEvent
    {
        public final MainViewPage targetPage;
        public final boolean addToBackStack;
        @NonNull
        public final Bundle arguments;
        public final TransitionStyle transitionStyle;

        public NavigateToPage(MainViewPage targetPage)
        {
            this(targetPage, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToPage(MainViewPage targetPage, boolean addToBackStack)
        {
            this(targetPage, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToPage(MainViewPage targetPage, Bundle arguments)
        {
            this(targetPage, arguments, TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToPage(MainViewPage targetPage, Bundle arguments, boolean addToBackStack)
        {
            this(targetPage, arguments, TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToPage(MainViewPage targetPage, Bundle arguments, TransitionStyle transitionStyle)
        {
            this(targetPage, arguments, transitionStyle, false);
        }

        public NavigateToPage(MainViewPage targetPage, @Nullable Bundle arguments, TransitionStyle transitionStyle, boolean addToBackStack)
        {
            this.targetPage = targetPage;
            this.addToBackStack = addToBackStack;
            this.arguments = (arguments != null) ? arguments : new Bundle();
            this.transitionStyle = transitionStyle;
        }
    }


    public static class SwapFragmentEvent extends NavigationEvent
    {
        public MainViewPage targetPage;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean addToBackStack;

        public SwapFragmentEvent(final MainViewPage targetPage, final Bundle arguments,
                                 final TransitionStyle transitionStyle, final boolean addToBackStack)
        {
            this.targetPage = targetPage;
            this.addToBackStack = addToBackStack;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
        }
    }


    //show hide the tabs restrict navigation, also need to block the drawer?
    public static class SetNavigationTabVisibility extends NavigationEvent
    {
        public final boolean isVisible;

        public SetNavigationTabVisibility(boolean isVisible)
        {
            this.isVisible = isVisible;
        }
    }


    //Disable the drawer to block navigation
    public static class SetNavigationDrawerActive extends NavigationEvent
    {
        public final boolean isActive;

        public SetNavigationDrawerActive(boolean isActive)
        {
            this.isActive = isActive;
        }
    }


    //Highlight the navigation tab
    public static class SelectTab extends NavigationEvent
    {
        public final MainViewPage tab;

        public SelectTab(@Nullable final MainViewPage tab) { this.tab = tab; }
    }

}
