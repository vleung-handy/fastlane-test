package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.Booking;
import com.handy.portal.util.DateTimeUtils;

import java.util.Date;

import butterknife.InjectView;

public abstract class DetailMapViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.map_view)
    protected RelativeLayout mapViewStub;

    @InjectView(R.id.map_overlay_text)
    protected TextView mapOverlayText;

    public DetailMapViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }


    public void create(ViewGroup container, Booking booking)
    {
        super.create(container, booking);

        inflateMapView(mapViewStub);
        updateMapOverlayTextForBooking(booking);
    }

    public void updateMapOverlayTextForBooking(Booking booking)
    {
        Booking.CheckInSummary checkInSummary = booking.getCheckInSummary();
        Integer providerMinutesLate = booking.getProviderMinutesLate();

        boolean overlayTextVisible = true;
        if (checkInSummary != null && checkInSummary.isCheckedIn())
        {
            setMapOverlayText(getContext().getResources().getString(R.string.booking_details_check_in_time_msg, DateTimeUtils.formatDateTo12HourClock(checkInSummary.getCheckInTime())));
        }
        else if (providerMinutesLate != null && booking.getStartDate() != null)
        {
            Date estimatedTimeArrival = new Date(booking.getStartDate().getTime());
            estimatedTimeArrival.setTime(booking.getStartDate().getTime() + providerMinutesLate * DateTimeUtils.MILLISECONDS_IN_MINUTE);
            setMapOverlayText(getContext().getResources().getString(R.string.booking_details_eta_msg, DateTimeUtils.formatDateTo12HourClock(estimatedTimeArrival)));
        }
        else
        {
            overlayTextVisible = false;
        }
        setMapOverlayTextVisible(overlayTextVisible);

    }

    private void setMapOverlayText(String text)
    {
        mapOverlayText.setText(text);
    }

    private void setMapOverlayTextVisible(boolean visible)
    {
        mapOverlayText.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    protected abstract void inflateMapView(RelativeLayout mapViewStub);

    protected int getLayoutResourceId()
    {
        return R.layout.element_map_with_overlay_text;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking item)
    {
        return true;
    }

}
