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
import java.util.List;

import static com.handy.portal.ui.fragment.ComplementaryBookingsFragment.COMPLEMENTARY_JOBS_SOURCE_NAME;
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
    public void onClaimAvailableBookingSuccess_switchToJobsTab() throws Exception
    {
        fragment.onReceiveClaimJobSuccess(new HandyEvent.ReceiveClaimJobSuccess(booking, null));

        assertThat(getBusCaptorValue(HandyEvent.NavigateToTab.class).targetTab, equalTo(MainViewTab.AVAILABLE_JOBS));
    }

    @Test
    public void onClaimComplementaryBookingSuccess_switchToScheduleTab() throws Exception
    {
        fragment.onReceiveClaimJobSuccess(new HandyEvent.ReceiveClaimJobSuccess(booking, COMPLEMENTARY_JOBS_SOURCE_NAME));

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
        fragmentSpy.onReceiveNotifyJobCheckInSuccess(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking));

        verify(fragmentSpy).updateDisplayForBooking(booking);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.check_in_success)));
    }

    @Test
    public void onEtaSuccess_updateDisplayAndShowToast() throws Exception
    {
        BookingDetailsFragment fragmentSpy = spy(fragment);
        fragmentSpy.onNotifyUpdateArrivalRequestReceived(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess(booking));

        verify(fragmentSpy).updateDisplayForBooking(booking);
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.eta_success)));
    }

    @Test
    public void onCheckOutSuccess_switchToScheduleTabAndDisplayToast() throws Exception
    {
        fragment.onReceiveBookingDetailsSuccess(new HandyEvent.ReceiveBookingDetailsSuccess(booking));
        // the event below depends on the event above being called to set the associated booking
        fragment.onNotifyCheckOutJobRequestReceived(new HandyEvent.ReceiveNotifyJobCheckoutSuccess(null));

        assertThat(getBusCaptorValue(HandyEvent.NavigateToTab.class).targetTab, equalTo(MainViewTab.SCHEDULED_JOBS));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.check_out_success)));
    }

    private void assertBusPost(Matcher matcher)
    {
        assertThat(getBusCaptorValues(), hasItem(matcher));
    }

    private <T> T getBusCaptorValue(Class<T> klass)
    {
        for (Object o : getBusCaptorValues())
        {
            if (klass.isInstance(o))
            {
                return klass.cast(o);
            }
        }
        throw new RuntimeException("Class " + klass.getName() + " not found in captor");
    }

    private List<Object> getBusCaptorValues()
    {
        verify(fragment.bus, atLeastOnce()).post(captor.capture());
        return captor.getAllValues();
    }
}
