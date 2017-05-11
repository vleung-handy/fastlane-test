package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.DismissableBookingElementView;
import com.handy.portal.clients.ui.element.ProRequestedJobsListGroupView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.SendAvailabilityLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class RequestedJobsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    EventBus mBus;

    private static final int VIEW_TYPE_DATE = 1;
    private static final int VIEW_TYPE_JOB = 2;
    private List<Object> mItems;

    public RequestedJobsRecyclerViewAdapter(final Context context,
                                            final List<BookingsWrapper> jobList) {
        Utils.inject(context, this);
        mItems = new ArrayList<>();
        for (BookingsWrapper bookingsWrapper : jobList) {
            mItems.add(bookingsWrapper.getDate());
            for (Booking booking : bookingsWrapper.getUndismissedBookings()) {
                mItems.add(booking);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DATE:
                final View itemView = new ProRequestedJobsListGroupView(parent.getContext());
                return new DateViewHolder(itemView);
            case VIEW_TYPE_JOB:
                final FrameLayout container = new FrameLayout(parent.getContext());
                container.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                return new JobViewHolder(container, mBus, EventContext.REQUESTED_JOBS);
        }
        return null;
    }

    @Override
    public int getItemViewType(final int position) {
        final Object item = mItems.get(position);
        if (item instanceof Date) {
            return VIEW_TYPE_DATE;
        }
        else if (item instanceof Booking) {
            return VIEW_TYPE_JOB;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((BaseViewHolder) holder).init(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public List<Object> getItems() {
        return mItems;
    }

    public void remove(final Booking booking) {
        for (int i = 0; i < mItems.size(); i++) {
            if (booking.equals(mItems.get(i))) {
                removeAt(i);
                break;
            }
        }
    }

    private void removeAt(final int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItems.size());

        final int itemBeforePosition = position - 1;
        final Object itemBefore = mItems.get(itemBeforePosition);
        final Object itemAfter = position < mItems.size() ? mItems.get(position) : null;
        if (itemBefore instanceof Date && (itemAfter instanceof Date || itemAfter == null)) {
            mItems.remove(itemBeforePosition);
            notifyItemRemoved(itemBeforePosition);
            notifyItemRangeChanged(itemBeforePosition, mItems.size());
        }
    }

    private static abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(final View itemView) {
            super(itemView);
        }

        abstract void init(Object item);
    }


    private static class DateViewHolder extends BaseViewHolder {
        DateViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            ((ProRequestedJobsListGroupView) itemView)
                    .updateDisplay((Date) item, itemView.getContext());
        }
    }


    public static class JobViewHolder extends BaseViewHolder {
        private final EventBus mBus;
        private String mOriginEventContext;

        public JobViewHolder(
                final View itemView,
                final EventBus bus,
                final String originEventContext
        ) {
            super(itemView);
            mBus = bus;
            mOriginEventContext = originEventContext;
        }

        @Override
        public void init(final Object item) {
            View convertView = null;
            final ViewGroup parentView = (ViewGroup) itemView;
            if (parentView.getChildCount() == 1) {
                convertView = parentView.getChildAt(0);
            }

            final Booking booking = (Booking) item;

            final BookingElementView bookingElementView = new DismissableBookingElementView();

            bookingElementView.initView(parentView.getContext(), booking, convertView, parentView);
            final View associatedView = bookingElementView.getAssociatedView();
            initActionListeners(associatedView, booking);

            customizeView(associatedView, booking);

            if (parentView.getChildCount() == 0) {
                parentView.addView(associatedView);
            }
        }

        private void customizeView(final View associatedView, final Booking booking) {
            final View requestedIndicator =
                    associatedView.findViewById(R.id.booking_list_entry_left_strip_indicator);
            if (requestedIndicator != null) {
                requestedIndicator.setVisibility(View.GONE);
            }

            final View requestedText =
                    associatedView.findViewById(R.id.booking_entry_listing_message_title_view);
            if (requestedIndicator != null) {
                requestedText.setVisibility(View.GONE);
            }

            final TextView title =
                    (TextView) associatedView.findViewById(R.id.booking_entry_area_text);
            if (title != null) {
                title.setText(booking.getRequestAttributes().getListingTitle());
            }

            final TextView subtitle =
                    (TextView) associatedView.findViewById(R.id.booking_entry_area_subtext);
            if (subtitle != null) {
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(booking.getRegionName());
            }

            final View swapIndicator = associatedView.findViewById(R.id.booking_swap_indicator);
            if (swapIndicator != null) {
                swapIndicator.setVisibility(booking.canSwap() ? View.VISIBLE : View.GONE);
            }

            final View sendAlternateTimesButton =
                    associatedView.findViewById(R.id.send_alternate_times_button);
            if (sendAlternateTimesButton != null) {
                final Booking.Action sendTimesAction =
                        booking.getAction(Booking.Action.ACTION_SEND_TIMES);
                if (sendTimesAction != null) {
                    sendAlternateTimesButton.setVisibility(View.VISIBLE);
                    sendAlternateTimesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final Bundle arguments = new Bundle();
                            arguments.putSerializable(BundleKeys.BOOKING, booking);
                            mBus.post(new NavigationEvent.NavigateToPage(
                                    MainViewPage.SEND_AVAILABLE_HOURS, arguments, true));
                            mBus.post(new LogEvent.AddLogEvent(
                                    new SendAvailabilityLog.SendAvailabilitySelected(
                                            mOriginEventContext, booking)));
                        }
                    });
                }
                else {
                    sendAlternateTimesButton.setVisibility(View.GONE);
                }
            }
        }

        private void initActionListeners(final View associatedView, final Booking booking) {
            associatedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mBus.post(new RequestedJobsRecyclerViewAdapter.Event.RequestedJobClicked(booking));
                }
            });

            final View claimButton = associatedView.findViewById(R.id.claim_button);
            if (claimButton != null) {
                claimButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        mBus.post(new RequestedJobsRecyclerViewAdapter.Event.RequestedJobClaimClicked(booking));
                    }
                });
            }

            final View dismissButton = associatedView.findViewById(R.id.dismiss_button);
            if (dismissButton != null) {
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        mBus.post(new RequestedJobsRecyclerViewAdapter.Event.RequestedJobDismissClicked(booking));
                    }
                });
            }
        }
    }


    public static abstract class Event {
        public static class RequestedJobClicked {
            private Booking mBooking;

            public RequestedJobClicked(final Booking booking) {
                mBooking = booking;
            }

            public Booking getBooking() {
                return mBooking;
            }
        }


        public static class RequestedJobClaimClicked {
            private Booking mBooking;

            public RequestedJobClaimClicked(final Booking booking) {
                mBooking = booking;
            }

            public Booking getBooking() {
                return mBooking;
            }
        }


        public static class RequestedJobDismissClicked {
            private Booking mBooking;

            public RequestedJobDismissClicked(final Booking booking) {
                mBooking = booking;
            }

            public Booking getBooking() {
                return mBooking;
            }
        }
    }
}
