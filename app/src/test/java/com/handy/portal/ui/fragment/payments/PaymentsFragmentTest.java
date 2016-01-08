package com.handy.portal.ui.fragment.payments;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
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
    @Mock
    Bus mBus;

    @InjectMocks
    private PaymentsFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
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
    public void shouldShowHelpSlideUpWhenHelpIconIsClicked() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(mFragment.getActivity());
        mFragment.helpNodesListView = spy(mFragment.helpNodesListView);
        when(mFragment.helpNodesListView.getCount()).thenReturn(1);

        mFragment.mSlideUpPanelLayout = mock(SlideUpPanelLayout.class);
        shadowActivity.clickMenuItem(R.id.action_help);

        verify(mFragment.mSlideUpPanelLayout).showPanel(anyInt(), any(View.class));
    }

    @Test
    public void shouldRedirectToHelpCenterWhenHelpIconIsClickedButThereAreNoHelpNodes() throws Exception
    {
        ShadowActivity shadowActivity = Shadows.shadowOf(mFragment.getActivity());

        mFragment.mSlideUpPanelLayout = mock(SlideUpPanelLayout.class);
        shadowActivity.clickMenuItem(R.id.action_help);

        ArgumentCaptor<HandyEvent> captor = ArgumentCaptor.forClass(HandyEvent.class);
        verify(mBus, atLeastOnce()).post(captor.capture());
        HandyEvent.NavigateToTab event = getBusCaptorValue(captor, HandyEvent.NavigateToTab.class);
        assertNotNull("NavigateToTab event was not post to bus", event);
        assertEquals("Failed to navigate to help tab", MainViewTab.HELP, event.targetTab);
    }

}
