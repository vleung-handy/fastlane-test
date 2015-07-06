package com.handy.portal.ui.element;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

import java.util.List;

public class MapPlaceholderViewConstructor extends BookingDetailsViewConstructor
{

    protected int getLayoutResourceId()
    {
        return R.layout.element_map_placeholder;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
    }



}
