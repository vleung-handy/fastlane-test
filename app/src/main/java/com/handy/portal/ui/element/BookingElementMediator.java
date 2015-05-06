package com.handy.portal.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/6/15.
 */
//mediators push information to the view telling them what to display
//they listen for signals from and intended for the associated view
//views do 0 logic, they just display information
public class BookingElementMediator
{
    private BookingElementView view;
    private Booking booking;
    private View convertView;
    private ViewGroup parent;

    public BookingElementMediator(Context parentContext, Booking booking, View convertView, ViewGroup parent)
    {
        this.booking = booking;
        this.convertView = convertView;
        this.parent = parent;

        this.view = new BookingElementView(this);
        this.view.initView(parentContext, booking, convertView, parent);

    }

    public View getAssociatedView()
    {
        return this.view.associatedView;
    }

    //

    public BookingElementView getView()
    {
        return view;
    }

}
