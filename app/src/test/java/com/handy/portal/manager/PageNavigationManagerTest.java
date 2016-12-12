package com.handy.portal.manager;

import android.net.Uri;
import android.os.Bundle;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.deeplink.DeeplinkMapper;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.payments.PaymentsManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class PageNavigationManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private EventBus bus;
    @Mock
    private PrefsManager prefsManager;
    @Mock
    private ProviderManager mProviderManager;
    @Mock
    private WebUrlManager mWebUrlManager;
    @Mock
    private PaymentsManager mPaymentsManager;
    @Mock
    private ConfigManager mConfigManager;

    @Inject
    PageNavigationManager pageNavigationManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        pageNavigationManager = new PageNavigationManager(bus, mProviderManager, mWebUrlManager,
                mPaymentsManager, mConfigManager);
    }

    /**
     * not ideal but we currently have two deeplink handler methods due to logging complications
     * @throws Exception
     */
    @Test
    public void onHandleSupportedDeeplinkUrl_shouldPostNavigationEventForDeeplinkPage() throws Exception
    {
        ArgumentCaptor<NavigationEvent.NavigateToPage> captor = ArgumentCaptor
                .forClass(NavigationEvent.NavigateToPage.class);
        /*
        verify the deeplinks defined in DeeplinkMapper
         */
        ImmutableMap<String, MainViewPage> deeplinkMap = DeeplinkMapper.getDeeplinkMap();
        for(String deeplinkUrl : deeplinkMap.keySet())
        {
            MainViewPage targetDeeplinkPage = deeplinkMap.get(deeplinkUrl);

            pageNavigationManager.handleDeeplinkUrl(null, deeplinkUrl);

            //verify bus event emitted
            verify(bus, atLeastOnce()).post(captor.capture());

            //verify that the event's target page matches the deeplink's
            assertEquals(targetDeeplinkPage, captor.getValue().targetPage);
        }
    }

    @Test
    public void onHandleSupportedNonUriDerivedDeeplinkBundle_shouldPostNavigationEventForDeeplinkPage() throws Exception
    {
        ArgumentCaptor<NavigationEvent.NavigateToPage> captor = ArgumentCaptor
                .forClass(NavigationEvent.NavigateToPage.class);
        /*
        verify the deeplinks defined in DeeplinkMapper
         */
        ImmutableMap<String, MainViewPage> deeplinkMap = DeeplinkMapper.getDeeplinkMap();
        for(String deeplinkUrl : deeplinkMap.keySet())
        {
            Bundle deeplinkDataBundle = DeeplinkUtils.createDeeplinkBundleFromUri(Uri.parse(deeplinkUrl));
            pageNavigationManager.handleNonUriDerivedDeeplinkDataBundle(deeplinkDataBundle, null);

            //verify bus event emitted
            verify(bus, atLeastOnce()).post(captor.capture());

            //verify that the event's target page matches the deeplink's
            MainViewPage mainViewPage = deeplinkMap.get(deeplinkUrl);
            assertEquals(mainViewPage, captor.getValue().targetPage);
        }
    }
}
