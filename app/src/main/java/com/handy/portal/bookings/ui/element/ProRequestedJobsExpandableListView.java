package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.adapter.ProRequestedJobsExpandableListAdapter;

import java.util.List;

public class ProRequestedJobsExpandableListView extends ExpandableListView
    //TODO can we make this generic
{
    public ProRequestedJobsExpandableListView(final Context context)
    {
        super(context);
    }

    public ProRequestedJobsExpandableListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProRequestedJobsExpandableListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * use this to set data instead of directly calling setAdapter()
     *
     * because this view is meant to be used with a specific adapter
     * @param jobList
     */
    public void setData(@NonNull List<BookingsWrapper> jobList)
    {
        ProRequestedJobsExpandableListAdapter itemsAdapter = new ProRequestedJobsExpandableListAdapter(
                jobList);
        setAdapter(itemsAdapter);

        int numGroups = itemsAdapter.getGroupCount();
        for(int i = 0; i<numGroups; i++)
        {
            expandGroup(i);
        }
    }

}
