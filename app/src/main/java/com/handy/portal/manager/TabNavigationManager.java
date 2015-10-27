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

    private  boolean isCachedProviderBlockPro()
    {
        boolean userIsBlockPro = false;
        userIsBlockPro = (  mProviderManager.getCachedActiveProvider() != null &&
                            mProviderManager.getCachedActiveProvider().isBlockCleaner());
        return userIsBlockPro;
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

        if (targetTab == MainViewTab.DETAILS)
        {
            inputArgumentsBundle.putSerializable(BundleKeys.TAB, currentTab);
        }

        //The new web view page URL style requires some processing since it is structured in a RESTful way
        if(targetTab.getWebViewTarget() != null)
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
            //TODO: Some really ugly logic about adding to the backstack, clean this up somehow
            addToBackStack |= targetTab == MainViewTab.COMPLEMENTARY_JOBS;
            addToBackStack |= targetTab == MainViewTab.SELECT_PAYMENT_METHOD;
            addToBackStack |= targetTab == MainViewTab.UPDATE_BANK_ACCOUNT;
            addToBackStack |= targetTab == MainViewTab.UPDATE_DEBIT_CARD;
            addToBackStack |= targetTab == MainViewTab.DETAILS;
            addToBackStack |= targetTab == MainViewTab.PAYMENTS_DETAIL;
            addToBackStack |= targetTab == MainViewTab.HELP_CONTACT;
            addToBackStack |= currentTab == MainViewTab.DETAILS && targetTab == MainViewTab.HELP;
            addToBackStack |= currentTab == MainViewTab.HELP && targetTab == MainViewTab.HELP;
            addToBackStack |= currentTab == MainViewTab.PAYMENTS && targetTab == MainViewTab.HELP;
        }

        return addToBackStack;
    }



}
