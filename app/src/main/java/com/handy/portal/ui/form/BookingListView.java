package com.handy.portal.ui.form;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.handy.portal.model.Booking;
import com.handy.portal.ui.adapter.BookingElementAdapter;

import java.util.List;

public class BookingListView extends ListView
{
    public BookingListView(Context context)
    {
        super(context);
    }

    public BookingListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public BookingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void populateList(List<Booking> bookings)
    {
        BookingElementAdapter itemsAdapter =
                new BookingElementAdapter(getContext(), bookings);
        setAdapter(itemsAdapter);
    }

}
