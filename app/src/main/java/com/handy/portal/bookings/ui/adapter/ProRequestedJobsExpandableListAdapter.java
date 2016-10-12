package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementMediator;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.DismissableBookingElementView;
import com.handy.portal.bookings.ui.element.ProRequestedJobsListGroupView;
import com.handy.portal.library.util.Utils;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

/**
 * expandable list view adapter for pro requested jobs view
 * <p>
 * group headers are not clickable
 */
public class ProRequestedJobsExpandableListAdapter extends BaseExpandableListAdapter
{
    @Inject
    ConfigManager mConfigManager;
    @Inject
    EventBus mBus;

    private List<BookingsWrapper> mJobsList;

    public ProRequestedJobsExpandableListAdapter(final Context context,
                                                 @NonNull List<BookingsWrapper> jobsList)
    {
        Utils.inject(context, this);
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
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        Class<? extends BookingElementView> bookingElementViewClass;
        final boolean isRequestDismissalEnabled = configuration != null
                && configuration.getRequestDismissal() != null
                && configuration.getRequestDismissal().isEnabled();
        if (isRequestDismissalEnabled)
        {
            bookingElementViewClass = DismissableBookingElementView.class;
        }
        else
        {
            bookingElementViewClass = AvailableBookingElementView.class;
        }

        Booking booking = getChild(groupPosition, childPosition);
        BookingElementMediator bem = new BookingElementMediator(
                parent.getContext(),
                booking,
                convertView,
                parent,
                bookingElementViewClass);
        final View associatedView = bem.getAssociatedView();
        if (isRequestDismissalEnabled)
        {
            initActionListeners(associatedView, booking);
        }
        // Hide requested pro indicator because this is a list view that displays only pro requests.
        final View requestedIndicator =
                associatedView.findViewById(R.id.booking_list_entry_left_strip_indicator);
        if (requestedIndicator != null)
        {
            requestedIndicator.setVisibility(View.INVISIBLE);
        }

        return associatedView;
    }

    private void initActionListeners(final View associatedView, final Booking booking)
    {
        final View claimButton = associatedView.findViewById(R.id.claim_button);
        if (claimButton != null)
        {
            claimButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View view)
                {
                    mBus.post(new Event.RequestedJobClaimClicked(booking));
                }
            });
        }

        final View dismissButton = associatedView.findViewById(R.id.dismiss_button);
        if (dismissButton != null)
        {
            dismissButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View view)
                {
                    mBus.post(new Event.RequestedJobDismissClicked(booking));
                }
            });
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    public static abstract class Event
    {
        public static class RequestedJobClaimClicked
        {
            private Booking mBooking;

            public RequestedJobClaimClicked(final Booking booking) {mBooking = booking;}

            public Booking getBooking()
            {
                return mBooking;
            }
        }


        public static class RequestedJobDismissClicked
        {
            private Booking mBooking;

            public RequestedJobDismissClicked(final Booking booking) {mBooking = booking;}

            public Booking getBooking()
            {
                return mBooking;
            }
        }
    }
}
