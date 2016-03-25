package com.handy.portal.manager;

import android.os.Bundle;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.model.SwapFragmentArguments;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class TabNavigationManager
{
    private final Bus mBus;
    private final ProviderManager mProviderManager;
    private final PaymentsManager mPaymentsManager;
    private final WebUrlManager mWebUrlManager;
    private final ConfigManager mConfigManager;

    private boolean mHaveShownNonBlockingOnboarding = false; //Now storing this for session client side so non-blocking will not be shown multiple times

    @Inject
    public TabNavigationManager(final Bus bus,
                                final ProviderManager providerManager,
                                final WebUrlManager webUrlManager,
                                final PaymentsManager paymentsManager,
                                final ConfigManager configManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mProviderManager = providerManager;
        mWebUrlManager = webUrlManager;
        mPaymentsManager = paymentsManager;
        mConfigManager = configManager;
    }

    @Subscribe
    //catch this , add extra data/process needed data, pass along to fragment for final usage/swap
    public void onRequestProcessNavigateToTab(NavigationEvent.RequestProcessNavigateToTab event)
    {
        //Ordering is important for these checks, they have different priorities

        //HACK : Magical hack to direct new pros to a webview for their onboarding
        if (doesCachedProviderNeedOnboarding() &&
                (isOnboardingBlocking() || !mHaveShownNonBlockingOnboarding))
        {
            if (!isOnboardingBlocking())
            {
                mHaveShownNonBlockingOnboarding = true;
            }
            event.targetTab = MainViewTab.ONBOARDING;
        }
        //HACK : Magical hack to show a blocking fragment if the pro's payment info is out of date
        else if (doesCachedProviderNeedPaymentInformation() &&
                configBlockingForPayment() &&
                (
                        event.targetTab == MainViewTab.AVAILABLE_JOBS ||
                                event.targetTab == MainViewTab.SCHEDULED_JOBS ||
                                event.targetTab == MainViewTab.BLOCK_PRO_WEBVIEW
                )
                )
        {
            event.targetTab = MainViewTab.PAYMENT_BLOCKING;
        }
        //HACK : Magical hack to turn block pros available jobs into the webview block jobs
        else if (isCachedProviderBlockPro() && event.targetTab == MainViewTab.AVAILABLE_JOBS)
        {
            event.targetTab = MainViewTab.BLOCK_PRO_WEBVIEW;
        }

        SwapFragmentArguments swapFragmentArguments = generateSwapFragmentArguments(
                event.targetTab,
                event.currentTab,
                event.arguments,
                event.transitionStyle,
                event.userTriggered
        );

        mBus.post(new NavigationEvent.SwapFragmentNavigation(swapFragmentArguments));
    }

    private boolean isCachedProviderBlockPro()
    {
        return mProviderManager.getCachedActiveProvider() != null && mProviderManager.getCachedActiveProvider().isBlockCleaner();
    }

    private boolean doesCachedProviderNeedOnboarding()
    {
        return (mConfigManager.getConfigurationResponse() != null &&
                mConfigManager.getConfigurationResponse().shouldShowOnboarding());
    }

    private boolean isOnboardingBlocking()
    {
        return (mConfigManager.getConfigurationResponse() != null &&
                mConfigManager.getConfigurationResponse().getOnboardingParams() != null &&
                mConfigManager.getConfigurationResponse().getOnboardingParams().isOnboardingBlocking());
    }

    private boolean doesCachedProviderNeedPaymentInformation()
    {
        return mPaymentsManager.HACK_directAccessCacheNeedsPayment();
    }

    private boolean configBlockingForPayment()
    {
        return (mConfigManager.getConfigurationResponse() != null && mConfigManager.getConfigurationResponse().shouldBlockClaimsIfMissingAccountInformation());
    }

    public SwapFragmentArguments generateSwapFragmentArguments(MainViewTab targetTab,
                                                               MainViewTab currentTab,
                                                               Bundle argumentsBundle,
                                                               TransitionStyle overrideTransitionStyle,
                                                               boolean userTriggered
    )
    {
        SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

        swapFragmentArguments.targetTab = targetTab;
        swapFragmentArguments.targetClassType = targetTab.getClassType();

        swapFragmentArguments.transitionStyle = getTransitionStyle(overrideTransitionStyle, targetTab, currentTab);
        swapFragmentArguments.argumentsBundle = getSwapFragmentArgumentsBundle(argumentsBundle, targetTab, currentTab);
        swapFragmentArguments.addToBackStack = getAddToBackStack(userTriggered, targetTab, currentTab);

        swapFragmentArguments.clearBackStack = !swapFragmentArguments.addToBackStack;

        return swapFragmentArguments;
    }

    private TransitionStyle getTransitionStyle(TransitionStyle overrideTransitionStyle, MainViewTab targetTab, MainViewTab currentTab)
    {
        //No transition if nothing to transition from
        if (currentTab != null)
        {
            return (overrideTransitionStyle != null ? overrideTransitionStyle : currentTab.getDefaultTransitionStyle(targetTab));
        }
        return null;
    }

    private Bundle getSwapFragmentArgumentsBundle(Bundle inputArgumentsBundle, MainViewTab targetTab, MainViewTab currentTab)
    {
        if (inputArgumentsBundle == null)
        {
            inputArgumentsBundle = new Bundle();
        }

        if (targetTab == MainViewTab.JOB_DETAILS)
        {
            inputArgumentsBundle.putSerializable(BundleKeys.TAB, currentTab);
        }

        //The new web view page URL style requires some processing since it is structured in a RESTful way
        if (targetTab.getWebViewTarget() != null)
        {
            String constructedUrl = mWebUrlManager.constructUrlForTargetTab(targetTab);
            inputArgumentsBundle.putString(BundleKeys.TARGET_URL, constructedUrl);
        }

        return inputArgumentsBundle;
    }

    private boolean getAddToBackStack(boolean userTriggered, MainViewTab targetTab, MainViewTab currentTab)
    {
        boolean addToBackStack = false;

        if (!userTriggered)
        {
            //TODO: Some really ugly logic about adding to the backstack, clean this up `
            addToBackStack = targetTab == MainViewTab.COMPLEMENTARY_JOBS;
            addToBackStack |= targetTab == MainViewTab.UPDATE_BANK_ACCOUNT;
            addToBackStack |= targetTab == MainViewTab.UPDATE_DEBIT_CARD;
            addToBackStack |= targetTab == MainViewTab.PROFILE_UPDATE;
            addToBackStack |= targetTab == MainViewTab.JOB_DETAILS;
            addToBackStack |= targetTab == MainViewTab.PAYMENTS_DETAIL;
            addToBackStack |= targetTab == MainViewTab.HELP_CONTACT;
            addToBackStack |= targetTab == MainViewTab.REQUEST_SUPPLIES;
            addToBackStack |= targetTab == MainViewTab.NEARBY_JOBS;
            addToBackStack |= targetTab == MainViewTab.CANCELLATION_REQUEST;
            addToBackStack |= targetTab == MainViewTab.DASHBOARD_TIERS;
            addToBackStack |= targetTab == MainViewTab.DASHBOARD_FEEDBACK;
            addToBackStack |= targetTab == MainViewTab.DASHBOARD_REVIEWS;
            addToBackStack |= targetTab == MainViewTab.DASHBOARD_VIDEO_LIBRARY;
            addToBackStack |= currentTab == MainViewTab.JOB_DETAILS && targetTab == MainViewTab.HELP;
            addToBackStack |= currentTab == MainViewTab.HELP && targetTab == MainViewTab.HELP;
            addToBackStack |= currentTab == MainViewTab.PAYMENTS && targetTab == MainViewTab.HELP;

            // Account Settings
            addToBackStack |= targetTab == MainViewTab.SELECT_PAYMENT_METHOD;
            addToBackStack |= targetTab == MainViewTab.PROFILE_UPDATE;
            addToBackStack |= targetTab == MainViewTab.YOUTUBE_PLAYER;

            // New Checkout
            addToBackStack |= targetTab == MainViewTab.SEND_RECEIPT_CHECKOUT;
        }

        return addToBackStack;
    }


}
