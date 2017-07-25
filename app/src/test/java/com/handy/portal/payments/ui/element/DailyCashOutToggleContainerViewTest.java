package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.view.View;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.ui.activity.TestActivity;
import com.handy.portal.payments.viewmodel.DailyCashOutToggleContainerViewModel;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DailyCashOutToggleContainerViewTest extends RobolectricGradleTestWrapper {

    private DailyCashOutToggleContainerView mDailyCashOutToggleContainerView;

    private DailyCashOutToggleContainerViewModel mDailyCashOutToggleContainerViewModel;

    @Before
    public void setUp() throws Exception {
        Context context = Robolectric.setupActivity(TestActivity.class);
        mDailyCashOutToggleContainerView = new DailyCashOutToggleContainerView(context);

        mDailyCashOutToggleContainerViewModel = mock(DailyCashOutToggleContainerViewModel.class);

        when(mDailyCashOutToggleContainerViewModel.isViewVisible()).thenReturn(true);
        when(mDailyCashOutToggleContainerViewModel.getInfoTextFormatted(context)).thenReturn("");
    }

    @Test
    public void shouldBeVisibleAccordingToViewModel() {
        when(mDailyCashOutToggleContainerViewModel.isViewVisible()).thenReturn(true);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertEquals("Should see daily cash out toggle when view model denotes it should be visible", View.VISIBLE, mDailyCashOutToggleContainerView.getVisibility());

        when(mDailyCashOutToggleContainerViewModel.isViewVisible()).thenReturn(false);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertEquals("Should not see daily cash out toggle when view model denotes it should not be visible", View.GONE, mDailyCashOutToggleContainerView.getVisibility());
    }

    @Test
    public void shouldBeApparentlyEnabledAccordingToViewModel() {
        when(mDailyCashOutToggleContainerViewModel.isViewApparentlyEnabled()).thenReturn(true);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertEquals("Should appear apparently enabled when view model denotes it", 1f, mDailyCashOutToggleContainerView.mContainer.getAlpha(), 0.0001f);

        when(mDailyCashOutToggleContainerViewModel.isViewApparentlyEnabled()).thenReturn(false);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertNotEquals("Should not appear apparently enabled when view model denotes it", 1f, mDailyCashOutToggleContainerView.mContainer.getAlpha(), 0.0001f);
    }

    @Test
    public void shouldSetToggleAccordingToViewModel() {
        when(mDailyCashOutToggleContainerViewModel.isToggleChecked()).thenReturn(true);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertEquals("Should set toggle checked when view model denotes it", true, mDailyCashOutToggleContainerView.mDailyCashOutToggle.isChecked());

        when(mDailyCashOutToggleContainerViewModel.isToggleChecked()).thenReturn(false);
        mDailyCashOutToggleContainerView.updateWithModel(mDailyCashOutToggleContainerViewModel);
        assertEquals("Should set toggle unchecked when view model denotes it", false, mDailyCashOutToggleContainerView.mDailyCashOutToggle.isChecked());
    }
}
