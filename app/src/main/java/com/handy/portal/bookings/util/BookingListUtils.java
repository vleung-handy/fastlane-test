package com.handy.portal.bookings.util;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.bookings.model.AuxiliaryInfo;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;

import java.util.List;

public class BookingListUtils {

    public static int getOverallCountPerAuxType(
            @Nullable final List<BookingsWrapper> jobList,
            @NonNull final AuxiliaryInfo.Type type
    ) {
        if (jobList == null || jobList.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (BookingsWrapper bookingsWrapper : jobList) {
            if (bookingsWrapper.getBookings() != null) {
                count += getCountPerAuxType(bookingsWrapper.getBookings(), type);
            }
        }
        return count;
    }

    public static int getCountPerAuxType(
            @NonNull final List<Booking> bookings,
            @NonNull final AuxiliaryInfo.Type type
    ) {
        int count = 0;
        for (Booking booking : bookings) {
            if (booking.getAuxiliaryInfo() != null && type == booking.getAuxiliaryInfo().getType()) {
                count++;
            }
        }
        return count;
    }
}
