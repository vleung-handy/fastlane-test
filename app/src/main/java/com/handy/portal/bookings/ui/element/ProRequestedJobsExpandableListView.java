package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.adapter.ProRequestedJobsExpandableListAdapter;

import java.util.List;

public class ProRequestedJobsExpandableListView extends ExpandableListView
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
     * and has to expand its group headers after data is set
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

    /**
     * convenience method for determining whether this list view has valid data attached to it
     * @return true if has valid data, false otherwise
     */
    public boolean hasValidData()
    {
        return getExpandableListAdapter() != null;
    }

}
