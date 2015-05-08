package com.handy.portal.ui.element;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public class GoogleMapView extends BookingDetailsView
{
    protected int getLayoutResourceId()
    {
        return R.layout.element_map;
    }

    protected void initFromBooking(Booking booking)
    {
        //use lat/long of booking to target the google map
    }

}
