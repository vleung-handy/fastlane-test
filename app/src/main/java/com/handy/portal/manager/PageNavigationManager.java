package com.handy.portal.manager;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.deeplink.DeeplinkMapper;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
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

    /**
     * making this a direct call because we specifically only want THIS manager
     * to handle "deeplinks" which are specific to the logic in this manager
     * and are not traditional Android deeplinks
     *
     * NOTE: cannot cleanly consolidate with the handling
     * of the deeplink data bundle in handleDeeplinkUrl
     * because of logging requirements and the way the log classes are currently structured
     */
    public void handleDeeplinkDataBundle(@Nullable final Bundle deeplinkData,
                                          final String deeplinkSource)
    {
        if (deeplinkData != null)
        {
            final String deeplink = deeplinkData.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink))
            {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if(page != null)
                {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkData
                    )));
                    mBus.post(new NavigationEvent.NavigateToPage(page, deeplinkData));
                }
                else
                {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkData
                    )));
                }
            }
        }
    }

    /**
     * making this a direct call because we specifically only want THIS manager
     * to handle "deeplinks" which are specific to the logic in this manager
     * and are not traditional Android deeplinks
     *
     * NOTE: cannot cleanly consolidate with the handling
     * of the deeplink data bundle in handleDeeplinkDataBundle
     * because of logging requirements and the way the log classes are currently structured
     *
     * @param deeplinkSource
     * @param deeplinkString
     */
    public void handleDeeplinkUrl(@DeeplinkLog.Source.DeeplinkSource String deeplinkSource,
                                  @NonNull String deeplinkString)
    {
        final Uri deeplinkUri = Uri.parse(deeplinkString);
        final Bundle deeplinkData = DeeplinkUtils.createDeeplinkBundleFromUri(deeplinkUri);
        if (deeplinkData != null)
        {
            //TODO find out why the the Opened event is being triggered when deeplinkdData != null
            //rather than on the actual click
            mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Opened(
                    deeplinkSource,
                    deeplinkUri
            )));
            final String deeplink = deeplinkData.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink))
            {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if(page != null)
                {

                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkUri
                    )));
                    mBus.post(new NavigationEvent.NavigateToPage(page, deeplinkData));
                }
                else
                {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkUri
                    )));
                }
            }
        }
        else if(deeplinkUri != null)
        {
            mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                    deeplinkSource,
                    DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                    deeplinkUri
            )));
        }
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
