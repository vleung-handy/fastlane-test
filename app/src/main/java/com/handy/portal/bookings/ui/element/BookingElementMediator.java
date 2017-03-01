package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;

//mediators push information to the view telling them what to display
//they listen for signals from and intended for the associated view
//views do 0 logic, they just display information
public class BookingElementMediator {
    private BookingElementView mView;

    public BookingElementMediator(Context parentContext, Booking booking, View convertView, ViewGroup parent, Class<? extends BookingElementView> elementViewClass) {
        // TODO: This is a quick hack, soon the scheduled view is going to be entirely different and not use these cells so I am not putting in the time to make it super clean
        try {
            mView = elementViewClass.newInstance();
        }
        catch (Exception e) {
            // not gonna happen
            e.printStackTrace();
        }

        mView.initView(parentContext, booking, convertView, parent);
    }

    public View getAssociatedView() {
        return mView.getAssociatedView();
    }

    public BookingElementView getView() {
        return mView;
    }

}
