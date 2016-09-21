package com.handy.portal.manager;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.payments.PaymentsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class PageNavigationManager
{
    private final EventBus mBus;
    private final ProviderManager mProviderManager;
    private final PaymentsManager mPaymentsManager;
    private final WebUrlManager mWebUrlManager;
    private final ConfigManager mConfigManager;

    @Inject
    public PageNavigationManager(final EventBus bus,
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
    public void onNavigateToPageEvent(NavigationEvent.NavigateToPage event)
    {
        mBus.post(new LogEvent.AddLogEvent(new AppLog.Navigation(event.targetPage.name().toLowerCase())));
        //Ordering is important for these checks, they have different priorities

        NavigationEvent.SwapFragmentEvent swapFragmentEvent = new NavigationEvent.SwapFragmentEvent(
                event.targetPage, event.arguments, event.transitionStyle, event.addToBackStack);

        //HACK : Magical hack to show a blocking fragment if the pro's payment info is out of date
        if (doesCachedProviderNeedPaymentInformation() &&
                configBlockingForPayment() &&
                (event.targetPage == MainViewPage.AVAILABLE_JOBS ||
                        event.targetPage == MainViewPage.SCHEDULED_JOBS ||
                        event.targetPage == MainViewPage.BLOCK_PRO_WEBVIEW))
        {
            swapFragmentEvent.targetPage = MainViewPage.PAYMENT_BLOCKING;
        }

        //HACK : Magical hack to turn block pros available jobs into the webview block jobs
        else if (isCachedProviderBlockPro() && event.targetPage == MainViewPage.AVAILABLE_JOBS)
        {
            swapFragmentEvent.targetPage = MainViewPage.BLOCK_PRO_WEBVIEW;
        }

        if (swapFragmentEvent.targetPage.getWebViewTarget() != null)
        {
            String constructedUrl = mWebUrlManager.constructUrlForTargetPage(swapFragmentEvent.targetPage);
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
