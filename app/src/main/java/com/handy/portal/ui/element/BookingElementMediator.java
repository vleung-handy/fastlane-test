package com.handy.portal.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;

//mediators push information to the view telling them what to display
//they listen for signals from and intended for the associated view
//views do 0 logic, they just display information
public class BookingElementMediator
{
    private BookingElementView view;

    public BookingElementMediator(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        // TODO: This is a quick hack, soon the scheduled view is going to be entirely different and not use these cells so I am not putting in the time to make it super clean
        if (booking == null || booking.inferBookingStatus() == BookingStatus.AVAILABLE)
        {
            this.view = new AvailableBookingElementView();
        }
        else
        {
            this.view = new ScheduledBookingElementView();
        }

        this.view.initView(parentContext, booking, convertView, parent);
    }

    public View getAssociatedView()
    {
        return this.view.associatedView;
    }

    public BookingElementView getView()
    {
        return view;
    }

}
