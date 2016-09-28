package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.bookings.ui.element.BookingElementMediator;
import com.handy.portal.bookings.ui.element.BookingElementView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ScheduledBookingElementAdapter extends ArrayAdapter<Booking>
{
    private final Class<? extends BookingElementView> elementViewClass;

    private static final int FIND_JOB_TYPE = 1;

    public ScheduledBookingElementAdapter(Context context, List<Booking> bookings, Class<? extends BookingElementView> elementViewClass)
    {
        super(context, 0, bookings);
        this.elementViewClass = elementViewClass;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Booking booking = getItem(position);
        TextView tv = null;

        if (getItemViewType(position) == FIND_JOB_TYPE) {
            if(convertView == null) {
               tv = new TextView(getContext());
            }
            else {
                tv = (TextView) convertView;
            }

            //Date and Time
            final String formattedStartDate = new SimpleDateFormat(" dd h:mma", Locale.getDefault()).format(booking.getStartDate());
            final String formattedEndDate = new SimpleDateFormat(" dd h:mma", Locale.getDefault()).format(booking.getEndDate());

            tv.setText(formattedStartDate + "-" + formattedEndDate);
            return tv;
        } else {
            BookingElementMediator bem = new BookingElementMediator(getContext(), booking, convertView, parent, elementViewClass);
            return bem.getAssociatedView();
        }
    }

    @Override
    public int getItemViewType(int position) {
        //If this is a schedule booking then return item 1
        if(getItem(position) instanceof ScheduledBookingFindJob) {
            return FIND_JOB_TYPE;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


}
