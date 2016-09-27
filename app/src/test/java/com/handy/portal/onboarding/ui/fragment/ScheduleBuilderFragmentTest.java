package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScheduleBuilderFragmentTest extends RobolectricGradleTestWrapper
{
    private ScheduleBuilderFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mSubflowData;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking mBooking;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.CLAIM)).thenReturn(mSubflowData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE)).thenReturn(SubflowType.CLAIM);
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        mActivityController.create().resume().visible();
    }

    private void startFragment()
    {
        final HandyEvent.ReceiveOnboardingJobsSuccess event =
                mock(HandyEvent.ReceiveOnboardingJobsSuccess.class,
                        Answers.RETURNS_DEEP_STUBS.get());
        when(event.getBookingsListWrapper().hasBookings()).thenReturn(true);
        final BookingsWrapper bookingsWrapper = mock(BookingsWrapper.class,
                Answers.RETURNS_DEEP_STUBS.get());
        when(bookingsWrapper.getBookings()).thenReturn(Lists.newArrayList(mBooking));
        when(event.getBookingsListWrapper().getBookingsWrappers()).thenReturn(
                Lists.newArrayList(bookingsWrapper));

        ((SchedulePreferencesFragment) mActivityController.get().getSupportFragmentManager()
                .getFragments().get(0)).onReceiveOnboardingJobsSuccess(event);
        mFragment = spy((ScheduleBuilderFragment) mActivityController.get()
                .getSupportFragmentManager().getFragments().get(1));
    }

    @Test
    public void testJobSelection() throws Exception
    {
        startFragment();

        assertThat(mFragment.mJobsContainer.getChildCount(), equalTo(1));
        assertFalse(mFragment.mSingleActionButton.isEnabled());
        final ViewGroup jobsGroup = (ViewGroup) mFragment.mJobsContainer.getChildAt(0);
        assertThat(jobsGroup.getChildCount(), equalTo(2)); // first is the header, second is the booking
        jobsGroup.getChildAt(1).performClick();
        assertTrue(mFragment.mSingleActionButton.isEnabled());
    }

    @Test
    public void testSubmission() throws Exception
    {
        when(mBooking.getId()).thenReturn("15");

        testJobSelection();
        mFragment.onPrimaryButtonClicked();

        verify(mFragment).terminate(mIntentCaptor.capture());
        final ArrayList<Booking> bookings = (ArrayList<Booking>) mIntentCaptor.getValue()
                .getSerializableExtra(BundleKeys.BOOKINGS);
        assertNotNull(bookings);
        assertThat(bookings.size(), equalTo(1));
        assertThat(bookings.get(0).getId(), equalTo("15"));
    }
}
