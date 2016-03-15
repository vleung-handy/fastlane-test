package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.model.OnboardingParams;
import com.squareup.otto.Subscribe;

public class OnboardingFragment extends PortalWebViewFragment
{
    //retain these to compare with latest version from config response, to determine if we need to change
    private OnboardingParams mLastOnboardingParams;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.ONBOARDING;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.onboarding_tab_title, false);

        //Make sure we should still be showing onboarding, and this version of it, otherwise nav away
        //Store current values of config response to compare to the next one
        if (configManager != null &&
                configManager.getConfigurationResponse() != null)
        {
            mLastOnboardingParams = configManager.getConfigurationResponse().getOnboardingParams();
        }
        else
        {
            mLastOnboardingParams = null;
        }

        //We got into a bad state, leave
        if (mLastOnboardingParams == null)
        {
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(
                    mEventLogFactory.createWebOnboardingShownLog(mLastOnboardingParams)));

            //Lock nav drawers and hide tabs if this is a blocking onboarding fragment
            if (mLastOnboardingParams.isOnboardingBlocking())
            {
                bus.post(new NavigationEvent.SetNavigationDrawerActive(false));
                bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
            }
        }
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(HandyEvent.ReceiveConfigurationSuccess event)
    {
        if (event.getConfigurationResponse() != null)
        {
            checkToLeaveOnboarding(event.getConfigurationResponse().getOnboardingParams());
        }
    }

    //if we get a new config and the onboarding params differ from the old one get out of onboarding
    private void checkToLeaveOnboarding(OnboardingParams onboardingParams)
    {
        boolean leaveOnboarding = false;

        if (onboardingParams == null ||
                (mLastOnboardingParams != null &&
                        !mLastOnboardingParams.equals(onboardingParams))
                )
        {
            leaveOnboarding = true;
        }

        if (leaveOnboarding)
        {
            bus.post(new LogEvent.AddLogEvent(
                    mEventLogFactory.createWebOnboardingClosedLog()));

            //just nav back to main, can lazily reload and tab navigation will handle the rest if we need to go to a different onboarding
            bus.post(new NavigationEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
        }

    }
}
