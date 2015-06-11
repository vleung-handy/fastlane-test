package com.handy.portal.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

//mediators push information to the view telling them what to display
//they listen for signals from and intended for the associated view
//views do 0 logic, they just display information
public class BookingElementMediator
{
    private BookingElementView view;

    public BookingElementMediator(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        this.view = new BookingElementView();
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
