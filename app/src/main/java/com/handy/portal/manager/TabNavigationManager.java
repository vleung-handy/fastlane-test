package com.handy.portal.manager;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.BasicLog;
import com.handy.portal.payments.PaymentsManager;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class TabNavigationManager
{
    private final EventBus mBus;
    private final ProviderManager mProviderManager;
    private final PaymentsManager mPaymentsManager;
    private final WebUrlManager mWebUrlManager;
    private final ConfigManager mConfigManager;

    @Inject
    public TabNavigationManager(final EventBus bus,
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
        mBus.post(new LogEvent.AddLogEvent(new BasicLog.Navigation(event.targetTab.name().toLowerCase())));
        //Ordering is important for these checks, they have different priorities

        NavigationEvent.SwapFragmentEvent swapFragmentEvent = new NavigationEvent.SwapFragmentEvent(
                event.targetTab, event.arguments, event.transitionStyle, event.addToBackStack);

        //HACK : Magical hack to show a blocking fragment if the pro's payment info is out of date
        if (doesCachedProviderNeedPaymentInformation() &&
                configBlockingForPayment() &&
                (event.targetTab == MainViewTab.AVAILABLE_JOBS ||
                        event.targetTab == MainViewTab.SCHEDULED_JOBS ||
                        event.targetTab == MainViewTab.BLOCK_PRO_WEBVIEW))
        {
            swapFragmentEvent.targetTab = MainViewTab.PAYMENT_BLOCKING;
        }

        //HACK : Magical hack to turn block pros available jobs into the webview block jobs
        else if (isCachedProviderBlockPro() && event.targetTab == MainViewTab.AVAILABLE_JOBS)
        {
            swapFragmentEvent.targetTab = MainViewTab.BLOCK_PRO_WEBVIEW;
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
