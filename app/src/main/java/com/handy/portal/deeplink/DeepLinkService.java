package com.handy.portal.deeplink;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handy.portal.bookings.model.Booking.BookingType;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.EventContext;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class DeepLinkService extends IntentService {
    @Inject
    EventBus bus;

    public static final String URI_HOST_DEEPLINK = "deeplink";
    public static final String URI_PATH_AVAILABLE_JOBS = "/available_jobs";
    public static final String URI_PATH_BOOKING_DETAILS = "/booking_details";

    public DeepLinkService() {
        super("DeepLinkService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.inject(getApplicationContext(), this);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        checkForDeepLinkIntent(intent);
    }

    private void checkForDeepLinkIntent(Intent intent) {
        if (intent != null && intent.getData() != null) {
            Uri intentUri = intent.getData();
            if (intentUri.getHost().equals(URI_HOST_DEEPLINK)) {
                openDeepLink(intent.getData());
            }
        }
    }

    private void openDeepLink(Uri deepLink) {
        if (deepLink == null || !deepLink.getHost().equals(URI_HOST_DEEPLINK)) {
            return;
        }

        String path = deepLink.getPath();

        switch (path) {
            case URI_PATH_AVAILABLE_JOBS: {
                NavigationEvent.NavigateToPage navigateToPage = new NavigationEvent.NavigateToPage(MainViewPage.AVAILABLE_JOBS);
                bus.post(navigateToPage);
            }
            break;

            case URI_PATH_BOOKING_DETAILS: {
                Bundle bundle = new Bundle();
                String bookingId = deepLink.getQuery();
                bundle.putString(BundleKeys.BOOKING_ID, bookingId);
                bundle.putString(BundleKeys.BOOKING_TYPE, BookingType.BOOKING.toString());
                bundle.putString(BundleKeys.EVENT_CONTEXT, EventContext.DEEPLINK);
                NavigationEvent.NavigateToPage navigateToPage = new NavigationEvent.NavigateToPage(MainViewPage.JOB_DETAILS, bundle, true);
                bus.post(navigateToPage);
            }
            break;
        }
    }
}
