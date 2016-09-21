package com.handy.portal.bookings.manager;

import android.support.annotation.NonNull;

import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.manager.PrefsManager;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import javax.inject.Inject;

/**
 * remembers when booking modals are shown
 * <p>
 * i.e. bookings for day unlocked modal
 */
public class BookingModalsManager
{
    private final EventBus mBus;
    private final PrefsManager mPrefsManager;

    @Inject
    public BookingModalsManager(final EventBus bus, final PrefsManager prefsManager)
    {
        mBus = bus;
        mPrefsManager = prefsManager;
    }

    public BookingsForDaysAheadModalsManager getBookingsForDayModalsManager(
            @NonNull BookingsForDaysAheadModalsManager.BookingsForDaysAheadModalType modalType,
            @NonNull Date bookingsDay)
    {
        Date currentDate = new Date();
        int numDaysAhead = DateTimeUtils.daysBetween(currentDate, bookingsDay) + 1;
        return new BookingsForDaysAheadModalsManager(modalType, numDaysAhead, mPrefsManager);
    }

    public static class BookingsForDaysAheadModalsManager
    {
        public enum BookingsForDaysAheadModalType
        {
            UNLOCKED_MODAL,
            UNLOCKED_TRIAL_MODAL
        }


        private final BookingsForDaysAheadModalType mBookingsForDaysAheadModalType;
        private final int mNumDaysAhead;
        private final PrefsManager mPrefsManager;

        public BookingsForDaysAheadModalsManager(
                @NonNull BookingsForDaysAheadModalType bookingsForDaysAheadModalType,
                @NonNull int numDaysAhead,
                @NonNull PrefsManager prefsManager)
        {
            mBookingsForDaysAheadModalType = bookingsForDaysAheadModalType;
            mNumDaysAhead = numDaysAhead;
            mPrefsManager = prefsManager;
        }

        @NonNull
        public PrefsManager getPrefsManager()
        {
            return mPrefsManager;
        }

        private String getModalShownPrefsKey()
        {
            return mNumDaysAhead + "_DAYS_AHEAD_" + mBookingsForDaysAheadModalType + "_SHOWN";
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
