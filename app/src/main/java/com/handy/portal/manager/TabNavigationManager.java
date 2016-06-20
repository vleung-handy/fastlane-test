package com.handy.portal.manager;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.AppPage;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.payments.PaymentsManager;
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
    public void onNavigateToTabEvent(NavigationEvent.NavigateToTab event)
    {
        mBus.post(new LogEvent.AddLogEvent(new AppLog.Navigation(event.targetTab.name().toLowerCase())));
        //Ordering is important for these checks, they have different priorities

        NavigationEvent.SwapFragmentEvent swapFragmentEvent = new NavigationEvent.SwapFragmentEvent(
                event.targetTab, event.arguments, event.transitionStyle, event.addToBackStack);

        //HACK : Magical hack to show a blocking fragment if the pro's payment info is out of date
        if (doesCachedProviderNeedPaymentInformation() &&
                configBlockingForPayment() &&
                (event.targetTab == AppPage.AVAILABLE_JOBS ||
                        event.targetTab == AppPage.SCHEDULED_JOBS ||
                        event.targetTab == AppPage.BLOCK_PRO_WEBVIEW))
        {
            swapFragmentEvent.targetTab = AppPage.PAYMENT_BLOCKING;
        }

        //HACK : Magical hack to turn block pros available jobs into the webview block jobs
        else if (isCachedProviderBlockPro() && event.targetTab == AppPage.AVAILABLE_JOBS)
        {
            swapFragmentEvent.targetTab = AppPage.BLOCK_PRO_WEBVIEW;
        }

        if (swapFragmentEvent.targetTab.getWebViewTarget() != null)
        {
            String constructedUrl = mWebUrlManager.constructUrlForTargetTab(swapFragmentEvent.targetTab);
            swapFragmentEvent.arguments.putString(BundleKeys.TARGET_URL, constructedUrl);
        }

        mBus.post(swapFragmentEvent);
    }

    private boolean isCachedProviderBlockPro()
    {
        return mProviderManager.getCachedActiveProvider() != null && mProviderManager.getCachedActiveProvider().isBlockCleaner();
    }

    private boolean doesCachedProviderNeedPaymentInformation()
    {
        return mPaymentsManager.HACK_directAccessCacheNeedsPayment();
    }

    private boolean configBlockingForPayment()
    {
        return (mConfigManager.getConfigurationResponse() != null && mConfigManager.getConfigurationResponse().shouldBlockClaimsIfMissingAccountInformation());
    }
}
