package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.activity.MainActivity;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BookingDetailsFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking booking;
    @Mock
    private BookingClaimDetails bookingClaimDetails;
    @Mock
    private BookingClaimDetails.ClaimTargetInfo claimTargetInfo;
    @Mock
    private PaymentInfo claimTargetPaymentInfo;
    @Captor
    private ArgumentCaptor<Object> captor;

    private BookingDetailsFragment fragment;

    @Before
    public void setUp() throws Exception
    {
        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        activityController.start().resume().visible();

        fragment = new BookingDetailsFragment();

        Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, "123456");
        args.putString(BundleKeys.BOOKING_TYPE, Booking.BookingType.BOOKING.toString());
        fragment.setArguments(args);

        FragmentManager fragmentManager = activityController.get().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, null)
                .commit();
        fragmentManager.executePendingTransactions();

        initMocks(this);

        when(fragment.prefsManager.getString(PrefsKey.LAST_PROVIDER_ID)).thenReturn("444");
        when(booking.getProviderId()).thenReturn("444");
        when(booking.inferBookingStatus(anyString())).thenReturn(Booking.BookingStatus.CLAIMED);
        when(booking.getStartDate()).thenReturn(new Date());
        when(booking.getEndDate()).thenReturn(new Date());

        when(bookingClaimDetails.getBooking()).thenReturn(booking);
        when(claimTargetInfo.getNumBookingsThreshold()).thenReturn(5);
        when(claimTargetInfo.getNumDaysExpectedPayment()).thenReturn(7);
        when(claimTargetInfo.getNumJobsClaimed()).thenReturn(3);
        when(claimTargetPaymentInfo.getAmount()).thenReturn(180);
        when(claimTargetPaymentInfo.getCurrencySymbol()).thenReturn("$");
        when(claimTargetInfo.getPaymentInfo()).thenReturn(claimTargetPaymentInfo);
        when(bookingClaimDetails.getClaimTargetInfo()).thenReturn(claimTargetInfo);
    }

    @Test
    public void onCreateView_shouldRequestBookingDetails() throws Exception
    {
        assertBusPost(instanceOf(HandyEvent.RequestBookingDetails.class));
    }

    @Test
    public void onReceiveBookingDetailsSuccess_updateDisplay() throws Exception
    {
        BookingDetailsFragment fragmentSpy = spy(fragment);
        fragmentSpy.onReceiveBookingDetailsSuccess(new HandyEvent.ReceiveBookingDetailsSuccess(booking));

        verify(fragmentSpy).updateDisplayForBooking(booking);
    }

    @Test
    public void onClaimBookingSuccess_switchToScheduleTab() throws Exception
    {
        when(bookingClaimDetails.shouldShowClaimTarget()).thenReturn(false); //case when claim target is not shown
        fragment.onReceiveClaimJobSuccess(new HandyEvent.ReceiveClaimJobSuccess(bookingClaimDetails, null));

        assertThat(getBusCaptorValue(HandyEvent.NavigateToTab.class).targetTab, equalTo(MainViewTab.SCHEDULED_JOBS));

        when(bookingClaimDetails.shouldShowClaimTarget()).thenReturn(true); //case when claim target is shown
        fragment.onReceiveClaimJobSuccess(new HandyEvent.ReceiveClaimJobSuccess(bookingClaimDetails, null));

        assertThat(getBusCaptorValue(HandyEvent.NavigateToTab.class).targetTab, equalTo(MainViewTab.SCHEDULED_JOBS));
    }

    @Test
    public void onOnMyWaySuccess_updateDisplayAndShowToast() throws Exception
    {
        BookingDetailsFragment fragmentSpy = spy(fragment);
        fragmentSpy.onReceiveNotifyJobOnMyWaySuccess(new HandyEvent.ReceiveNotifyJobOnMyWaySuccess(booking));

        verify(fragmentSpy).updateDisplayForBooking(booking);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.omw_success)));
    }

    @Test
    public void onCheckInSuccess_updateDisplayAndShowToast() throws Exception
    {
        BookingDetailsFragment fragmentSpy = spy(fragment);
        fragmentSpy.onReceiveNotifyJobCheckInSuccess(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking, false));

        verify(fragmentSpy).updateDisplayForBooking(booking);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.check_in_success)));
    }

    @Test
    public void onEtaSuccess_updateDisplayAndShowToast() throws Exception
    {
        BookingDetailsFragment fragmentSpy = spy(fragment);
        fragmentSpy.onReceiveNotifyJobUpdateArrivalTimeSuccess(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess(booking));

        verify(fragmentSpy).updateDisplayForBooking(booking);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.eta_success)));
    }

    @Test
    public void onCheckOutSuccess_switchToScheduleTabAndDisplayToast() throws Exception
    {
        fragment.onReceiveBookingDetailsSuccess(new HandyEvent.ReceiveBookingDetailsSuccess(booking));
        // the event below depends on the event above being called to set the associated booking
        fragment.onReceiveNotifyJobCheckOutSuccess(new HandyEvent.ReceiveNotifyJobCheckOutSuccess(null, false));

        assertThat(getBusCaptorValue(HandyEvent.NavigateToTab.class).targetTab, equalTo(MainViewTab.SCHEDULED_JOBS));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.check_out_success)));
    }

    private void assertBusPost(Matcher matcher)
    {
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        assertThat(captor.getAllValues(), hasItem(matcher));
    }

    private <T> T getBusCaptorValue(Class<T> classType)
    {
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        return getBusCaptorValue(captor, classType);
    }

}
