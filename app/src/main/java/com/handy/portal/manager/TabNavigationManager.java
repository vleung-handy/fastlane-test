package com.handy.portal.manager;

import android.os.Bundle;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.SwapFragmentArguments;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class TabNavigationManager
{
    private final Bus mBus;
    private final ProviderManager mProviderManager;
    private final WebUrlManager mWebUrlManager;

    public TabNavigationManager(final Bus bus,
                                final ProviderManager providerManager,
                                final WebUrlManager webUrlManager)
    {
        mBus = bus;
        mBus.register(this);
        mProviderManager = providerManager;
        mWebUrlManager = webUrlManager;
    }

    private  boolean isCachedProviderBlockPro()
    {
        boolean userIsBlockPro = false;
        userIsBlockPro = (  mProviderManager.getCachedActiveProvider() != null &&
                            mProviderManager.getCachedActiveProvider().isBlockCleaner());
        return userIsBlockPro;
    }

    @Subscribe
    //catch this , add extra data/process needed data, pass along to fragment for final usage/swap
    public void onRequestProcessNavigateToTab(HandyEvent.RequestProcessNavigateToTab event)
    {
        //HACK : Magical hack to turn block pros available jobs into the webview block jobs
        if(isCachedProviderBlockPro() && event.targetTab == MainViewTab.AVAILABLE_JOBS)
        {
            event.targetTab = MainViewTab.BLOCK_PRO_AVAILABLE_JOBS_WEBVIEW;
        }

        SwapFragmentArguments swapFragmentArguments = generateSwapFragmentArguments(
                event.targetTab,
                event.currentTab,
                event.arguments,
                event.transitionStyle,
                event.userTriggered
        );

        mBus.post(new HandyEvent.SwapFragmentNavigation(swapFragmentArguments));
    }


    public SwapFragmentArguments generateSwapFragmentArguments(MainViewTab targetTab, MainViewTab currentTab, Bundle argumentsBundle, TransitionStyle overrideTransitionStyle, boolean userTriggered)
    {
        SwapFragmentArguments swapFragmentArguments = new SwapFragmentArguments();

        swapFragmentArguments.targetTab = targetTab;

        if (argumentsBundle == null)
        {
            argumentsBundle = new Bundle();
        }

        //don't use transition if don't have anything to transition from
        if (currentTab != null)
        {
            swapFragmentArguments.transitionStyle = (overrideTransitionStyle != null ? overrideTransitionStyle : currentTab.getDefaultTransitionStyle(targetTab));
        }

        swapFragmentArguments.targetClassType = targetTab.getClassType();

        if (targetTab == MainViewTab.DETAILS)
        {
            argumentsBundle.putSerializable(BundleKeys.TAB, currentTab);
        }

        //TEMPORARY BLOCK PRO WEB VIEW LOGIC
        if(targetTab == MainViewTab.BLOCK_PRO_AVAILABLE_JOBS_WEBVIEW)
        {
            String constructedUrl = mWebUrlManager.constructUrlForTargetTab(targetTab);
            argumentsBundle.putString(BundleKeys.TARGET_URL, constructedUrl);
        }
        //END TEMPORARY BLOCK PRO WEB VIEW LOGIC

        swapFragmentArguments.argumentsBundle = argumentsBundle;

        if (userTriggered)
        {
            swapFragmentArguments.addToBackStack = false;
        }
        else
        {
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.COMPLEMENTARY_JOBS;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.SELECT_PAYMENT_METHOD;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.UPDATE_BANK_ACCOUNT;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.UPDATE_DEBIT_CARD;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.DETAILS;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.PAYMENTS_DETAIL;
            swapFragmentArguments.addToBackStack |= targetTab == MainViewTab.HELP_CONTACT;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.DETAILS && targetTab == MainViewTab.HELP;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.HELP && targetTab == MainViewTab.HELP;
            swapFragmentArguments.addToBackStack |= currentTab == MainViewTab.PAYMENTS && targetTab == MainViewTab.HELP;
        }

        swapFragmentArguments.clearBackStack = !swapFragmentArguments.addToBackStack;

        return swapFragmentArguments;
    }





}
