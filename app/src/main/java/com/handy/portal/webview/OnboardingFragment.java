package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ConfigurationResponse;
import com.squareup.otto.Subscribe;

public class OnboardingFragment extends PortalWebViewFragment
{
    //retain these to compare with latest version from config response, to determine if we need to change
    private boolean mIsBlocking = false;
    private String mTargetUrl = "";
    private boolean mShouldShowOnboarding = false;

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
        if (configManager != null && configManager.getConfigurationResponse() != null)
        {
            ConfigurationResponse response = configManager.getConfigurationResponse();
            mIsBlocking = response.isOnboardingBlocking();
            mTargetUrl = response.getOnboardingFullWebUrl();
            mShouldShowOnboarding = response.shouldShowOnboarding();
        }
        else
        {
            mShouldShowOnboarding = false;
        }

        if (!mShouldShowOnboarding)
        {
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
        }
        else
        {
            //Lock nav drawers and hide tabs if this is a blocking onboarding fragment
            if (mIsBlocking)
            {
                bus.post(new HandyEvent.SetNavigationDrawerActive(false));
                bus.post(new HandyEvent.SetNavigationTabVisibility(false));
            }
            //Request new values to see if they have changed
            configManager.prefetch();
        }
    }

    @Subscribe
    public void onConfigurationResponseRetrieved(HandyEvent.ConfigurationResponseRetrieved event)
    {
        checkToLeaveOnboarding();
    }

    private void checkToLeaveOnboarding()
    {
        if (configManager != null && configManager.getConfigurationResponse() != null)
        {
            ConfigurationResponse response = configManager.getConfigurationResponse();
            if (!onboardingValuesMatch(response))
            {
                //just nav back to main, can lazily reload and tab navigation will handle the rest
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
            }
        }
    }

    private boolean onboardingValuesMatch(ConfigurationResponse response)
    {
        return (mIsBlocking == response.isOnboardingBlocking() &&
                mTargetUrl.equals(response.getOnboardingFullWebUrl()) &&
                mShouldShowOnboarding == response.shouldShowOnboarding());
    }
}
