package com.handy.portal.ui.fragment;

import android.app.ActionBar;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.activity.TestActivity;
import com.handy.portal.ui.layout.SlideUpPanelContainer;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PaymentsFragmentTest extends RobolectricGradleTestWrapper
{
    private PaymentsFragment fragment;

    @Before
    public void setUp() throws Exception
    {
        fragment = new PaymentsFragment();
        SupportFragmentTestUtil.startFragment(fragment, TestActivity.class);
        initMocks(this);
    }

    @Test
    public void shouldHaveCorrectTitleOnActionBar() throws Exception
    {
        ActionBar actionBar = fragment.getActivity().getActionBar();
        assertNotNull(actionBar);
        assertEquals(fragment.getString(R.string.payments), actionBar.getTitle());
    }

    @Test
    public void shouldNavigateToProfileTabWhenUpdateBankingClicked() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
        shadowActivity.clickMenuItem(R.id.action_update_banking);

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        HandyEvent.NavigateToTab event = getBusCaptorValue(captor, HandyEvent.NavigateToTab.class);
        assertNotNull("NavigateToTab event was not post to bus", event);
        assertEquals("Failed to navigate to profile tab", MainViewTab.PROFILE, event.targetTab);
    }

    @Test
    public void shouldShowIncomeVerificationConfirm() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
        shadowActivity.clickMenuItem(R.id.action_email_verification);

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        HandyEvent.RequestSendIncomeVerification event = getBusCaptorValue(captor, HandyEvent.RequestSendIncomeVerification.class);
        assertNotNull("RequestSendIncomeVerification event was not post to bus", event);
    }

    @Test
    public void shouldShowHelpSlideUpWhenHelpIconIsClicked() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
        fragment.helpNodesListView = spy(fragment.helpNodesListView);
        when(fragment.helpNodesListView.getCount()).thenReturn(1);

        fragment.slideUpPanelContainer = mock(SlideUpPanelContainer.class);
        shadowActivity.clickMenuItem(R.id.action_help);

        verify(fragment.slideUpPanelContainer).showPanel(anyInt(), any(SlideUpPanelContainer.ContentInitializer.class));
    }

    @Test
    public void shouldRedirectToHelpCenterWhenHelpIconIsClickedButThereAreNoHelpNodes() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());

        fragment.slideUpPanelContainer = mock(SlideUpPanelContainer.class);
        shadowActivity.clickMenuItem(R.id.action_help);

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        HandyEvent.NavigateToTab event = getBusCaptorValue(captor, HandyEvent.NavigateToTab.class);
        assertNotNull("NavigateToTab event was not post to bus", event);
        assertEquals("Failed to navigate to help tab", MainViewTab.HELP, event.targetTab);
    }

}
