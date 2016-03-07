package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ConfigurationResponse;
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
        System.out.println("CSD - Onboarding fragment resume");

        super.onResume();
        setActionBar(R.string.onboarding_tab_title, false);

        //Make sure we should still be showing onboarding, and this version of it, otherwise nav away
        //Store current values of config response to compare to the next one
        if (configManager != null &&
            configManager.getConfigurationResponse() != null &&
            configManager.getConfigurationResponse().getOnboardingParams() != null)
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
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
        }
        else
        {
            //Lock nav drawers and hide tabs if this is a blocking onboarding fragment
            if (mLastOnboardingParams.isOnboardingBlocking())
            {
                bus.post(new HandyEvent.SetNavigationDrawerActive(false));
                bus.post(new HandyEvent.SetNavigationTabVisibility(false));
            }
        }
    }

    @Subscribe
    public void onConfigurationResponseRetrieved(HandyEvent.ConfigurationResponseRetrieved event)
    {
        System.out.println("CSD - Check to leave onboarding");
        checkToLeaveOnboarding();
    }

    //if we get a new config and the onboarding params differ from the old one get out of onboarding
    private void checkToLeaveOnboarding()
    {
        if (configManager != null &&
            configManager.getConfigurationResponse() != null)
        {
            ConfigurationResponse response = configManager.getConfigurationResponse();
            if (mLastOnboardingParams != null &&
                !mLastOnboardingParams.equals(response.getOnboardingParams()))
            {
                //just nav back to main, can lazily reload and tab navigation will handle the rest
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
            }
        }
    }
}
