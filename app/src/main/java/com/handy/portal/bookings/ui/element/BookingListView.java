package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.adapter.BookingElementAdapter;

import java.util.List;

public class BookingListView extends ListView {
    public BookingListView(Context context) {
        super(context);
    }

    public BookingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void populateList(List<Booking> bookings, Class<? extends BookingElementView> elementViewClass) {
        BookingElementAdapter itemsAdapter =
                new BookingElementAdapter(getContext(), bookings, elementViewClass);
        setAdapter(itemsAdapter);
    }

}
