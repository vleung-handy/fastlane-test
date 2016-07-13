package com.handy.portal.data;

import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.logevents.EventLogService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestDataManager extends DataManager
{
    public static final String BOOKING_ERROR_ID = "111";
    public static final String BOOKING_UNCLAIMED_ID = "222";
    public static final String BOOKING_IN_PROGRESS_ID = "333";

    public TestDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final StripeRetrofitService stripeService, final EventLogService eventLogService)
    {
        super(service, endpoint, stripeService, eventLogService);
    }

    @Override
    public void updateProviderProfile(final String providerId, final TypeSafeMap<ProviderKey> params, final Callback<ProviderPersonalInfo> cb) { }

    @Override
    public void getBookingDetails(final String bookingId, final Booking.BookingType type, final Callback<Booking> cb)
    {
        switch (bookingId)
        {
            case BOOKING_ERROR_ID:
            {
                cb.onError(new DataManagerError(DataManagerError.Type.OTHER, "error"));
                break;
            }
            case BOOKING_UNCLAIMED_ID:
            {
                cb.onSuccess(createMockBooking(BOOKING_UNCLAIMED_ID, BookingProgress.READY_FOR_CLAIM));
                break;
            }
            case BOOKING_IN_PROGRESS_ID:
            {
                cb.onSuccess(createMockBooking(BOOKING_IN_PROGRESS_ID, BookingProgress.READY_FOR_CHECK_OUT));
                break;
            }
        }
    }

    public static Booking createMockBooking(String bookingId, @BookingProgress.Progress int bookingProgress)
    {
        Booking booking = mock(Booking.class);
        when(booking.getId()).thenReturn(bookingId);
        when(booking.getStartDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(booking.getEndDate()).thenReturn(new Date(System.currentTimeMillis()));
        when(booking.getServiceInfo()).thenReturn(new Booking.ServiceInfo("home_cleaning", "Home Cleaning"));
        when(booking.getBookingProgress()).thenReturn(bookingProgress);
        when(booking.getCustomerPreferences()).thenReturn(createCustomerPreferences());

        return booking;
    }

    public static List<Booking.BookingInstructionUpdateRequest> createCustomerPreferences()
    {
        List<Booking.BookingInstructionUpdateRequest> customerPreferences = new ArrayList<>();

        customerPreferences.add(new Booking.BookingInstructionUpdateRequest());
        customerPreferences.add(new Booking.BookingInstructionUpdateRequest());

        return customerPreferences;
    }
}

