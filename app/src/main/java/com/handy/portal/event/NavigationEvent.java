package com.handy.portal.event;

import android.os.Bundle;

import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.model.SwapFragmentArguments;

public abstract class NavigationEvent extends HandyEvent
{
    public static class NavigateToTab extends NavigationEvent
    {
        public MainViewTab targetTab;
        public Bundle arguments;
        public TransitionStyle transitionStyleOverride;

        public NavigateToTab(MainViewTab targetTab)
        {
            this.targetTab = targetTab;
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
        }

        public NavigateToTab(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyleOverride)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
            this.transitionStyleOverride = transitionStyleOverride;
        }
    }


    //TODO: Come up with better name
    public static class RequestProcessNavigateToTab extends NavigationEvent
    {
        public MainViewTab targetTab;
        public MainViewTab currentTab;
        public Bundle arguments;
        public TransitionStyle transitionStyle;
        public boolean userTriggered;

        public RequestProcessNavigateToTab(MainViewTab targetTab, MainViewTab currentTab, Bundle arguments, TransitionStyle transitionStyle, boolean userTriggered)
        {
            this.targetTab = targetTab;
            this.currentTab = currentTab;
            this.arguments = arguments;
            this.transitionStyle = transitionStyle;
            this.userTriggered = userTriggered;
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
