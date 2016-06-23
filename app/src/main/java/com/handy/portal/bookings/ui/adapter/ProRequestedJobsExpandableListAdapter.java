package com.handy.portal.bookings.ui.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementMediator;
import com.handy.portal.bookings.ui.element.ProRequestedJobsListGroupView;

import java.util.List;

/**
 * expandable list view adapter for pro requested jobs view
 *
 * group headers are not clickable
 *
 */
public class ProRequestedJobsExpandableListAdapter extends BaseExpandableListAdapter
{
    private List<BookingsWrapper> mJobsList;

    public ProRequestedJobsExpandableListAdapter(@NonNull List<BookingsWrapper> jobsList)
    {
        setData(jobsList);
    }

    public void setData(@NonNull List<BookingsWrapper> jobsList)
    {
        mJobsList = jobsList;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public int getGroupCount()
    {
        return mJobsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return getGroup(groupPosition).getBookings().size();
    }

    @Override
    public BookingsWrapper getGroup(int groupPosition)
    {
        return mJobsList.get(groupPosition);
    }

    @Override
    public Booking getChild(int groupPosition, int childPosition)
    {
        return getGroup(groupPosition).getBookings().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        BookingsWrapper bookingsWrapper = getGroup(groupPosition);
        if (convertView == null)
        {
            convertView = new ProRequestedJobsListGroupView(parent.getContext());
        }
        ((ProRequestedJobsListGroupView) convertView)
                .updateDisplay(bookingsWrapper, parent.getContext());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Booking booking = getChild(groupPosition, childPosition);

        BookingElementMediator bem = new BookingElementMediator(
                parent.getContext(),
                booking,
                convertView,
                parent,
                AvailableBookingElementView.class);
        final View associatedView = bem.getAssociatedView();
        // Hide requested pro indicator because this is a list view that displays only pro requests.
        final View requestedIndicator =
                associatedView.findViewById(R.id.booking_list_entry_left_strip_indicator);
        if (requestedIndicator != null)
        {
            requestedIndicator.setVisibility(View.INVISIBLE);
        }

        return associatedView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
