package com.handy.portal.ui.form;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.adapter.BookingElementAdapter;

import java.util.List;

/**
 * Created by cdavis on 5/6/15.
 */
public class BookingListView extends ListView
{
    public BookingListView(Context context) {
        super(context);
    }

    public BookingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void populateList(List<Booking> bookings)
    {
        BookingElementAdapter itemsAdapter =
                new BookingElementAdapter(getContext().getApplicationContext(), bookings);
        setAdapter(itemsAdapter);
    }

}
