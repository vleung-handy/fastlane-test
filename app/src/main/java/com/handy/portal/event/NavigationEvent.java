package com.handy.portal.event;

import android.os.Bundle;

import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;

public abstract class NavigationEvent extends HandyEvent
{
    public static class NavigateToTab extends NavigationEvent
    {
        public final MainViewTab targetTab;
        public final boolean addToBackStack;
        public final Bundle arguments;
        public final TransitionStyle transitionStyle;

        public NavigateToTab(MainViewTab targetTab)
        {
            this(targetTab, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToTab(MainViewTab targetTab, boolean addToBackStack)
        {
            this(targetTab, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments)
        {
            this(targetTab, arguments, TransitionStyle.NATIVE_TO_NATIVE, false);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, boolean addToBackStack)
        {
            this(targetTab, arguments, TransitionStyle.NATIVE_TO_NATIVE, addToBackStack);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyle)
        {
            this(targetTab, arguments, transitionStyle, false);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyle, boolean addToBackStack)
        {
            this.targetTab = targetTab;
            this.addToBackStack = addToBackStack;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
        }
    }


    public static class SwapFragmentEvent extends NavigationEvent
    {
        public MainViewTab targetTab;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean addToBackStack;

        public SwapFragmentEvent(final MainViewTab targetTab, final Bundle arguments,
                                 final TransitionStyle transitionStyle, final boolean addToBackStack)
        {
            this.targetTab = targetTab;
            this.addToBackStack = addToBackStack;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
        }
    }


    //show hide the tabs restrict navigation, also need to block the drawer?
    public static class SetNavigationTabVisibility extends HandyEvent
    {
        public final boolean isVisible;

        public SetNavigationTabVisibility(boolean isVisible)
        {
            this.isVisible = isVisible;
        }
    }


    //Disable the drawer to block navigation
    public static class SetNavigationDrawerActive extends HandyEvent
    {
        public final boolean isActive;

        public SetNavigationDrawerActive(boolean isActive)
        {
            this.isActive = isActive;
        }
    }

}
