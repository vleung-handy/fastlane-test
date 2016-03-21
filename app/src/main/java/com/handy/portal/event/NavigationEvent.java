package com.handy.portal.event;

import android.os.Bundle;

import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.model.SwapFragmentArguments;

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
            this(targetTab, null, null, false);
        }

        public NavigateToTab(MainViewTab targetTab, boolean addToBackStack)
        {
            this(targetTab, null, null, addToBackStack);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments)
        {
            this(targetTab, arguments, null, false);
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, boolean addToBackStack)
        {
            this(targetTab, arguments, null, addToBackStack);
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


    //TODO: Come up with better name
    public static class RequestProcessNavigateToTab extends NavigationEvent
    {
        public MainViewTab targetTab;
        public MainViewTab currentTab;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean addToBackStack;

        public RequestProcessNavigateToTab(MainViewTab targetTab, MainViewTab currentTab, Bundle arguments, TransitionStyle transitionStyle, boolean addToBackStack)
        {
            this.targetTab = targetTab;
            this.currentTab = currentTab;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
            this.addToBackStack = addToBackStack;
        }
    }


    //TODO: Come up with better name
    public static class SwapFragmentNavigation extends NavigationEvent
    {
        public SwapFragmentArguments swapFragmentArguments;

        public SwapFragmentNavigation(SwapFragmentArguments swapFragmentArguments)
        {
            this.swapFragmentArguments = swapFragmentArguments;
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
