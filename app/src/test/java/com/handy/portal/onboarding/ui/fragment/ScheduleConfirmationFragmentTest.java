package com.handy.portal.onboarding.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Designation;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class ScheduleConfirmationFragmentTest extends RobolectricGradleTestWrapper
{
    private ScheduleConfirmationFragment mFragment;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OnboardingDetails mOnboardingDetails;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Intent mIntent;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SubflowData mSubflowData;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SuppliesOrderInfo mSuppliesOrderInfo;
    private ArrayList<Booking> mBookings;
    @Captor
    private ArgumentCaptor<Intent> mIntentCaptor;
    private ActivityController<OnboardingSubflowActivity> mActivityController;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        when(mOnboardingDetails.getSubflowDataByType(SubflowType.CONFIRMATION))
                .thenReturn(mSubflowData);
        when(mIntent.getSerializableExtra(BundleKeys.ONBOARDING_DETAILS))
                .thenReturn(mOnboardingDetails);
        when(mIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE))
                .thenReturn(SubflowType.CONFIRMATION);
        when(mIntent.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO))
                .thenReturn(mSuppliesOrderInfo);
        mBookings = new ArrayList<>();
        when(mIntent.getSerializableExtra(BundleKeys.BOOKINGS))
                .thenReturn(mBookings);
    }

    private void startFragment()
    {
        mActivityController = Robolectric.buildActivity(OnboardingSubflowActivity.class, mIntent);
        mActivityController.create().resume().visible();
        mFragment = (ScheduleConfirmationFragment) mActivityController.get()
                .getSupportFragmentManager().getFragments().get(0);
    }

    @Test
    public void shouldDisplayPendingBookings() throws Exception
    {
        final Booking booking = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        when(booking.isProxy()).thenReturn(true);
        when(booking.getLocationName()).thenReturn("Manhattan");
        mBookings.add(booking);

        startFragment();

        assertThat(mFragment.mJobsContainer.getChildCount(), equalTo(1));
        assertThat(((TextView) mFragment.mJobsContainer.getChildAt(0)
                        .findViewById(R.id.booking_entry_area_text)).getText().toString(),
                equalTo("Manhattan"));
    }

    @Test
    public void shouldNotDisplaySuppliesOrderInfoIfNotAvailable() throws Exception
    {
        when(mIntent.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO)).thenReturn(null);

        startFragment();

        assertThat(mFragment.mSuppliesContainer.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mSuppliesHeader.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldNotDisplaySuppliesOrderInfoIfDesignationIsUndecided() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.UNDECIDED);

        startFragment();

        assertThat(mFragment.mSuppliesContainer.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mSuppliesHeader.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void shouldDisplaySuppliesOrderInfoWithOptionToOptInIfDesignationIsNo() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.NO);

        startFragment();

        assertThat(mFragment.mSuppliesContainer.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mSuppliesHeader.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mEditSuppliesButton.getText().toString(), equalTo("Add"));
    }

    @Test
    public void shouldDisplaySuppliesOrderInfoWithOptionToEditIfDesignationIsYes() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.YES);

        startFragment();

        assertThat(mFragment.mSuppliesContainer.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mSuppliesHeader.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mEditSuppliesButton.getText().toString(), equalTo("Edit"));
    }

    @Test
    public void shouldDisplayPaymentInfo() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.YES);
        when(mSuppliesOrderInfo.getPaymentText()).thenReturn("Card ending in 1234");
        when(mSuppliesOrderInfo.getOrderTotalText()).thenReturn("$40");

        startFragment();

        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.label))
                .getText().toString(), equalTo("Order Total"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.value))
                .getText().toString(), equalTo("$40"));
    }

    @Test
    public void shouldDisplayFeeInfo() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.YES);
        when(mSuppliesOrderInfo.getPaymentText()).thenReturn(null);
        when(mSuppliesOrderInfo.getOrderTotalText()).thenReturn("$40");

        startFragment();

        assertThat(mFragment.mPaymentView.getVisibility(), equalTo(View.GONE));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.label))
                .getText().toString(), equalTo("Supplies Fee"));
        assertThat(((TextView) mFragment.mOrderTotalView.findViewById(R.id.value))
                .getText().toString(), equalTo("$40"));
    }

    @Test
    public void shouldLaunchSuppliesSubflowOnEditSuppliesOrder() throws Exception
    {
        when(mSuppliesOrderInfo.getDesignation()).thenReturn(Designation.YES);

        startFragment();
        mFragment.onEditSuppliesButtonClicked();

        final Intent nextIntent = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertThat((SubflowType) nextIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE),
                equalTo(SubflowType.SUPPLIES));
    }

    @Test
    public void shouldLaunchClaimSubflowOnEditJobs() throws Exception
    {
        startFragment();
        mFragment.onEditJobsButtonClicked();

        final Intent nextIntent = shadowOf(mActivityController.get()).getNextStartedActivity();
        assertThat((SubflowType) nextIntent.getSerializableExtra(BundleKeys.SUBFLOW_TYPE),
                equalTo(SubflowType.CLAIM));
    }

    @Test
    public void shouldDisplayNewPendingBookingsAfterEditingJobs() throws Exception
    {
        shouldDisplayPendingBookings();

        final Intent data = new Intent();
        final Booking booking = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        when(booking.isProxy()).thenReturn(true);
        when(booking.getLocationName()).thenReturn("Brooklyn");
        data.putExtra(BundleKeys.BOOKINGS, Lists.newArrayList(booking));
        mFragment.onActivityResult(RequestCode.ONBOARDING_SUBFLOW, Activity.RESULT_OK, data);

        assertThat(mFragment.mJobsContainer.getChildCount(), equalTo(1));
        assertThat(((TextView) mFragment.mJobsContainer.getChildAt(0)
                        .findViewById(R.id.booking_entry_area_text)).getText().toString(),
                equalTo("Brooklyn"));
    }

    @Test
    public void shouldDisplayNewSuppliesOrderInfoBookingsAfterEditingSuppliesOrder() throws Exception
    {
        shouldDisplaySuppliesOrderInfoWithOptionToEditIfDesignationIsYes();

        final Intent data = new Intent();
        final SuppliesOrderInfo suppliesOrderInfo = mock(SuppliesOrderInfo.class,
                Answers.RETURNS_DEEP_STUBS.get());
        when(suppliesOrderInfo.getDesignation()).thenReturn(Designation.NO);
        data.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, suppliesOrderInfo);
        mFragment.onActivityResult(RequestCode.ONBOARDING_SUBFLOW, Activity.RESULT_OK, data);

        assertThat(mFragment.mSuppliesContainer.getVisibility(), equalTo(View.GONE));
        assertThat(mFragment.mSuppliesHeader.getVisibility(), equalTo(View.VISIBLE));
        assertThat(mFragment.mEditSuppliesButton.getText().toString(), equalTo("Add"));
    }

    @Test
    public void shouldClaimBookings() throws Exception
    {
        final Booking booking = mock(Booking.class, Answers.RETURNS_DEEP_STUBS.get());
        when(booking.getId()).thenReturn("555");
        when(booking.getType()).thenReturn(Booking.BookingType.BOOKING_PROXY);
        mBookings.add(booking);

        startFragment();

        mFragment.onPrimaryButtonClicked();

        final HandyEvent.RequestClaimJobs event = TestUtils.getFirstMatchingBusEvent(
                mFragment.getBus(), HandyEvent.RequestClaimJobs.class);
        assertNotNull(event);
        assertThat(event.mJobClaimRequest.getJobs().size(), equalTo(1));
        assertThat(event.mJobClaimRequest.getJobs().get(0).getBookingId(), equalTo("555"));
        assertThat(event.mJobClaimRequest.getJobs().get(0).getJobType(), equalTo("booking_proxy"));
    }

    @Test
    public void shouldTerminateAfterClaiming() throws Exception
    {
        shouldClaimBookings();
        final JobClaimResponse jobClaimResponse = mock(JobClaimResponse.class);
        final BookingClaimDetails bookingClaimDetails =
                mock(BookingClaimDetails.class, Answers.RETURNS_DEEP_STUBS.get());
        when(bookingClaimDetails.getBooking().getType())
                .thenReturn(Booking.BookingType.BOOKING_PROXY);
        when(bookingClaimDetails.getBooking().inferBookingStatus(anyString()))
                .thenReturn(Booking.BookingStatus.CLAIMED);
        when(jobClaimResponse.getJobs()).thenReturn(Lists.newArrayList(bookingClaimDetails));

        mFragment.onReceiveClaimJobsSuccess(
                new HandyEvent.ReceiveClaimJobsSuccess(jobClaimResponse));

        assertTrue(mActivityController.get().isFinishing());
    }
}
