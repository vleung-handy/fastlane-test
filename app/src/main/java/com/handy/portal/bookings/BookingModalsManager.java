package com.handy.portal.bookings;

import android.support.annotation.NonNull;

import com.handy.portal.manager.PrefsManager;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;

import java.util.Date;

import javax.inject.Inject;

/**
 * remembers when booking modals are shown
 *
 * i.e. bookings for day unlocked modal
 */
public class BookingModalsManager
{
    private final Bus mBus;
    private final PrefsManager mPrefsManager;

    @Inject
    public BookingModalsManager(final Bus bus, final PrefsManager prefsManager)
    {
        mBus = bus;
        mBus.register(this);
        mPrefsManager = prefsManager;
    }

    public BookingsForDayModalsManager getBookingsForDayModalsManager(
            @NonNull BookingsForDayModalsManager.BookingsForDayModalType modalType,
            @NonNull Date bookingDay)
    {
        return new BookingsForDayModalsManager(modalType, bookingDay, mPrefsManager);
    }

    public static class BookingsForDayModalsManager
    {
        public enum BookingsForDayModalType
        {
            BOOKINGS_FOR_DAY_UNLOCKED_MODAL,
            BOOKINGS_FOR_DAY_UNLOCKED_TRIAL_MODAL
        }

        private final BookingsForDayModalType mBookingsForDayModalType;
        private final Date mBookingDay;
        private final PrefsManager mPrefsManager;
        private static final String MODAL_SHOWN_PREFS_KEY_SUFFIX = "_SHOWN";

        public BookingsForDayModalsManager(
                @NonNull BookingsForDayModalType bookingsForDayModalType,
                @NonNull Date bookingDay,
                @NonNull PrefsManager prefsManager)
        {
            mBookingsForDayModalType = bookingsForDayModalType;
            mBookingDay = bookingDay;
            mPrefsManager = prefsManager;
        }

        @NonNull
        public PrefsManager getPrefsManager()
        {
            return mPrefsManager;
        }

        private String getModalShownPrefsKey()
        {
            String datePrefsKey = DateTimeUtils.NUMERIC_YEAR_MONTH_DATE_FORMATTER.format(mBookingDay);
            return datePrefsKey + "_" + mBookingsForDayModalType + MODAL_SHOWN_PREFS_KEY_SUFFIX;
        }

        public boolean bookingsForDayModalPreviouslyShown()
        {
            String prefsKey = getModalShownPrefsKey();
            return mPrefsManager.getBoolean(prefsKey, false);
        }

        public void onBookingsForDayModalShown()
        {
            String prefsKey = getModalShownPrefsKey();
            mPrefsManager.setBoolean(prefsKey, true);
        }

        public void resetModalShownStatus()
        {
            String prefsKey = getModalShownPrefsKey();
            mPrefsManager.setBoolean(prefsKey, false);
        }
    }
}
