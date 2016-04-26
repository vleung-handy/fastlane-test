package com.handy.portal.bookings;

import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.bookings.ui.fragment.BookingFragment;
import com.handy.portal.bookings.ui.fragment.CancellationRequestFragment;
import com.handy.portal.bookings.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.bookings.ui.fragment.InProgressBookingFragment;
import com.handy.portal.bookings.ui.fragment.NearbyBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingCancelDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.EarlyAccessTrialDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.JobAccessUnlockedDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.RateBookingDialogFragment;
import com.handy.portal.data.DataManager;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                BookingDetailsWrapperFragment.class,
                ScheduledBookingsFragment.class,
                AvailableBookingsFragment.class,
                ComplementaryBookingsFragment.class,
                RateBookingDialogFragment.class,
                NearbyBookingsFragment.class,
                CancellationRequestFragment.class,
                ConfirmBookingClaimDialogFragment.class,
                ConfirmBookingCancelDialogFragment.class,
                BookingFragment.class,
                InProgressBookingFragment.class,
                SendReceiptCheckoutFragment.class,
                EarlyAccessTrialDialogFragment.class,
                JobAccessUnlockedDialogFragment.class,
        })
public final class BookingsModule
{
    @Provides
    @Singleton
    final BookingManager provideBookingManager(final Bus bus,
                                               final DataManager dataManager)
    {
        return new BookingManager(bus, dataManager);
    }
}
