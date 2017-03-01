package com.handy.portal.core.manager;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.deeplink.DeeplinkMapper;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class PageNavigationManager {
    private final EventBus mBus;

    @Inject
    public PageNavigationManager(final EventBus bus) {
        mBus = bus;
        mBus.register(this);
    }

    /**
     * NOTE: as the name suggests, this method is only to be used when the given deeplink
     * data bundle is NOT derived from a Uri. reason is that we want to keep the logging logic
     * <p>
     * making this a direct call because we specifically only want THIS manager
     * to handle "deeplinks" which are specific to the logic in this manager
     * and are not traditional Android deeplinks
     * <p>
     * NOTE: cannot cleanly consolidate with the handling
     * of the deeplink data bundle in handleDeeplinkUrl
     * because of logging requirements and the way the log classes are currently structured
     */
    public void handleNonUriDerivedDeeplinkDataBundle(@Nullable final Bundle deeplinkDataBundle,
                                                      @DeeplinkLog.Source.DeeplinkSource final String deeplinkSource) {
        if (deeplinkDataBundle != null) {
            final String deeplink = deeplinkDataBundle.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink)) {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if (page != null) {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkDataBundle
                    )));
                    mBus.post(new NavigationEvent.NavigateToPage(page, deeplinkDataBundle));
                }
                else {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkDataBundle
                    )));
                }
            }
        }
    }

    /**
     * see notes on {@link #handleNonUriDerivedDeeplinkDataBundle(Bundle, String)}
     *
     * @param deeplinkSource
     * @param deeplinkUrl
     */
    public void handleDeeplinkUrl(@DeeplinkLog.Source.DeeplinkSource String deeplinkSource,
                                  @NonNull String deeplinkUrl) {
        final Uri deeplinkUri = Uri.parse(deeplinkUrl);
        final Bundle deeplinkDataBundle = DeeplinkUtils.createDeeplinkBundleFromUri(deeplinkUri);
        /*
        not consolidating this part with handleNonUriDerivedDeeplinkDataBundle because logging is different
         */
        if (deeplinkDataBundle != null) {
            /*
            TODO don't know why the Opened log event is being triggered here instead of on the actual click
             */
            mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Opened(deeplinkSource, deeplinkUri)));
            final String deeplink = deeplinkDataBundle.getString(BundleKeys.DEEPLINK);
            if (!TextUtils.isEmpty(deeplink)) {
                final MainViewPage page = DeeplinkMapper.getPageForDeeplink(deeplink);
                if (page != null) {

                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Processed(
                            deeplinkSource,
                            deeplinkUri
                    )));
                    mBus.post(new NavigationEvent.NavigateToPage(page, deeplinkDataBundle, !page.isTopLevel()));
                    //TODO PortalWebViewClient didn't use !page.isTopLevel() to determine whether to add to back stack. check if OK
                }
                else {
                    mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                            deeplinkSource,
                            DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                            deeplinkUri
                    )));
                }
            }
        }
        else if (deeplinkUri != null) {
            mBus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Ignored(
                    deeplinkSource,
                    DeeplinkLog.Ignored.Reason.UNRECOGNIZED,
                    deeplinkUri
            )));
        }
    }

    @Subscribe
    public void onNavigateToPageEvent(NavigationEvent.NavigateToPage event) {
        NavigationEvent.SwapFragmentEvent swapFragmentEvent = new NavigationEvent.SwapFragmentEvent(
                event.targetPage, event.arguments, event.transitionStyle, event.addToBackStack);

        swapFragmentEvent.setReturnFragment(event.getReturnFragment(), event.getActivityRequestCode());

        mBus.post(swapFragmentEvent);
    }
}
