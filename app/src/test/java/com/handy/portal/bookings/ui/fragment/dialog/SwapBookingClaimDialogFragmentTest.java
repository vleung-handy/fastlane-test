package com.handy.portal.bookings.ui.fragment.dialog;

import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SwapBookingClaimDialogFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking booking;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking swappableBooking;
    private SwapBookingClaimDialogFragment dialog;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        initMocks(this);
        when(booking.getSwappableBooking()).thenReturn(swappableBooking);
        when(booking.getLocationName()).thenReturn("Manhattan");
        when(booking.getType()).thenReturn(Booking.BookingType.BOOKING_PROXY);
        when(booking.isProxy()).thenReturn(true);
        when(swappableBooking.getLocationName()).thenReturn("Queens");
        when(swappableBooking.isProxy()).thenReturn(true);
        when(swappableBooking.getType()).thenReturn(Booking.BookingType.BOOKING_PROXY);
        when(swappableBooking.isProxy()).thenReturn(true);
        dialog = SwapBookingClaimDialogFragment.newInstance(booking);
        SupportFragmentTestUtil.startFragment(dialog);
    }

    @Test
    public void shouldDisplayLocationsOfConflictingBookings() throws Exception
    {
        final TextView swappableBookingLocationText = (TextView) dialog.mSwappableJobContainer
                .findViewById(R.id.booking_entry_area_text);
        assertNotNull(swappableBookingLocationText);
        assertThat(swappableBookingLocationText.getText().toString(), equalTo("Queens"));

        final TextView claimableBookingLocationText = (TextView) dialog.mClaimableJobContainer
                .findViewById(R.id.booking_entry_area_text);
        assertNotNull(claimableBookingLocationText);
        assertThat(claimableBookingLocationText.getText().toString(), equalTo("Manhattan"));
    }
}
