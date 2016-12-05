package com.handy.portal.manager;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.deeplink.DeeplinkMapper;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.payments.PaymentsManager;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

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

    private PageNavigationManager mPageNavigationManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        mPageNavigationManager = new PageNavigationManager(bus, mProviderManager, mWebUrlManager,
                mPaymentsManager, mConfigManager);
    }

    @Test
    public void onHandleSupportedDeeplinkUrl_shouldPostNavigationEventForDeeplinkPage() throws Exception
    {

        ArgumentCaptor<NavigationEvent.NavigateToPage> captor = ArgumentCaptor
                .forClass(NavigationEvent.NavigateToPage.class);
        /*
        verify the deeplinks defined in DeeplinkMapper
         */
        ImmutableMap<String, MainViewPage> deeplinkMap = DeeplinkMapper.getDeeplinkMap();
        for(String deeplinkString : deeplinkMap.keySet())
        {
            mPageNavigationManager.handleDeeplinkUrl(null, deeplinkString);
            //verify bus event emitted
            verify(bus, atLeastOnce()).post(captor.capture());
            MainViewPage mainViewPage = deeplinkMap.get(deeplinkString);
            assertEquals(mainViewPage, captor.getValue().targetPage);
        }
    }
}
