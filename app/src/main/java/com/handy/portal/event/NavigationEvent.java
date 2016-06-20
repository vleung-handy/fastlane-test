package com.handy.portal.event;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.constant.AppPage;
import com.handy.portal.constant.TransitionStyle;

public abstract class NavigationEvent extends HandyEvent
{
    public static class NavigateToTab extends NavigationEvent
    {
        public final AppPage targetTab;
        public final boolean addToBackStack;
        @NonNull
        public final Bundle arguments;
        public final TransitionStyle transitionStyle;

        public NavigateToTab(AppPage targetTab)
        {
            this(targetTab, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToTab(AppPage targetTab, boolean addToBackStack)
        {
            this(targetTab, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToTab(AppPage targetTab, Bundle arguments)
        {
            this(targetTab, arguments, TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToTab(AppPage targetTab, Bundle arguments, boolean addToBackStack)
        {
            this(targetTab, arguments, TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToTab(AppPage targetTab, Bundle arguments, TransitionStyle transitionStyle)
        {
            this(targetTab, arguments, transitionStyle, false);
        }

        public NavigateToTab(AppPage targetTab, @Nullable Bundle arguments, TransitionStyle transitionStyle, boolean addToBackStack)
        {
            this.targetTab = targetTab;
            this.addToBackStack = addToBackStack;
            this.arguments = (arguments != null) ? arguments : new Bundle();
            this.transitionStyle = transitionStyle;
        }
    }


    public static class SwapFragmentEvent extends NavigationEvent
    {
        public AppPage targetTab;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean addToBackStack;

        public SwapFragmentEvent(final AppPage targetTab, final Bundle arguments,
                                 final TransitionStyle transitionStyle, final boolean addToBackStack)
        {
            this.targetTab = targetTab;
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
        public final AppPage tab;

        public SelectTab(@Nullable final AppPage tab) { this.tab = tab; }
    }

}
