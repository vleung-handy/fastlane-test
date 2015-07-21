package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.BookingElementMediator;
import com.handy.portal.ui.element.BookingElementView;

import java.util.List;

public class BookingElementAdapter extends ArrayAdapter<Booking>
{
    private final Context context;
    private final List<Booking> bookings;
    private final Class<? extends BookingElementView> elementViewClass;

    public BookingElementAdapter(Context context, List<Booking> bookings, Class<? extends BookingElementView> elementViewClass)
    {
        super(context, 0, bookings);
        this.context = context;
        this.bookings = bookings;
        this.elementViewClass = elementViewClass;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Booking booking = getItem(position);
        BookingElementMediator bem = new BookingElementMediator(getContext(), booking, convertView, parent, elementViewClass);
        return bem.getAssociatedView();
    }
}
