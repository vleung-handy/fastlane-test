package com.handy.portal.payments.ui.fragment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.layout.SlideUpPanelLayout;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PaymentsFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock
    Bus mBus;

    @InjectMocks
    private PaymentsFragment mFragment;
    @Mock
    private ConfigurationResponse mConfigurationResponse;
    @Inject
    ConfigManager mConfigManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);
        when(mConfigManager.getConfigurationResponse()).thenReturn(mConfigurationResponse);
        when(mConfigurationResponse.shouldShowNotificationMenuButton()).thenReturn(false);
        mFragment = new PaymentsFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
        initMocks(this);
    }

    @Test
    public void shouldHaveCorrectTitleOnActionBar() throws Exception
    {
        ActionBar actionBar = ((AppCompatActivity) mFragment.getActivity()).getSupportActionBar();
        assertNotNull(actionBar);
        assertEquals(mFragment.getString(R.string.payments), actionBar.getTitle());
    }

    @Test
    public void shouldRedirectToHelpCenterWhenHelpIconIsClicked() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(mFragment.getActivity());

        mFragment.mSlideUpPanelLayout = mock(SlideUpPanelLayout.class);
        shadowActivity.clickMenuItem(R.id.action_help);

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mBus, atLeastOnce()).post(captor.capture());
        NavigationEvent.NavigateToTab event = TestUtils.getBusCaptorValue(captor, NavigationEvent.NavigateToTab.class);

        assertNotNull("NavigateToTab event was not post to bus", event);
        assertEquals("Failed to navigate to help tab", MainViewTab.HELP_WEBVIEW, event.targetTab);
    }

}
