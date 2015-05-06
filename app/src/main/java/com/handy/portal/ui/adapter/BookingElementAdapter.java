package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.element.BookingElementMediator;

import java.util.List;

/**
 * Created by cdavis on 5/6/15.
 */
public class BookingElementAdapter extends ArrayAdapter<Booking>
{
    public BookingElementAdapter(Context context, List<Booking> bookings) {
        super(context, 0, bookings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Booking booking = getItem(position);
        BookingElementMediator bem = new BookingElementMediator(getContext(), booking, convertView, parent);
        return bem.getAssociatedView();
    }
}
