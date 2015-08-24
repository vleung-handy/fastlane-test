package com.handy.portal.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by cdavis on 8/19/15.
 */
public class DeepLinkService extends IntentService
{
    @Inject
    Bus bus;

    public static final String URI_HOST_DEEPLINK = "deeplink";
    public static final String URI_PATH_AVAILABLE_JOBS = "/available_jobs";
    public static final String URI_PATH_BOOKING_DETAILS = "/booking_details";

    public DeepLinkService()
    {
        super("DeepLinkService");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Utils.inject(getApplicationContext(), this);
    }

    @Override
    public void onHandleIntent(Intent intent)
    {
        checkForDeepLinkIntent(intent);
    }

    private void checkForDeepLinkIntent(Intent intent)
    {
        if (intent != null && intent.getData() != null)
        {
            Uri intentUri = intent.getData();
            if (intentUri.getHost().equals(URI_HOST_DEEPLINK))
            {
                openDeepLink(intent.getData());
            }
        }
    }

    private void openDeepLink(Uri deepLink)
    {
        if (deepLink == null || !deepLink.getHost().equals(URI_HOST_DEEPLINK))
        {
            return;
        }

        String path = deepLink.getPath();

        switch (path)
        {
            case URI_PATH_AVAILABLE_JOBS:
            {
                HandyEvent.NavigateToTab navigateToTab = new HandyEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS);
                bus.post(navigateToTab);
            }
            break;

            case URI_PATH_BOOKING_DETAILS:
            {
                Bundle bundle = new Bundle();
                String bookingId = deepLink.getQuery();
                bundle.putString(BundleKeys.BOOKING_ID, bookingId);
                HandyEvent.NavigateToTab navigateToTab = new HandyEvent.NavigateToTab(MainViewTab.DETAILS, bundle);
                bus.post(navigateToTab);
            }
            break;
        }
    }
}
