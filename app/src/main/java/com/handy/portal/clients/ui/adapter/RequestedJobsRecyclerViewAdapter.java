package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.DismissableBookingElementView;
import com.handy.portal.clients.ui.element.ProRequestedJobsListGroupView;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class RequestedJobsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Inject
    ConfigManager mConfigManager;
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
            for (Booking booking : bookingsWrapper.getBookings()) {
                mItems.add(booking);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case VIEW_TYPE_DATE:
                final View itemView = new ProRequestedJobsListGroupView(parent.getContext());
                if (!isRequestDismissalEnabled()) {
                    addBottomBorder(itemView, R.drawable.border_gray_bottom_bg);
                }
                return new DateViewHolder(itemView);
            case VIEW_TYPE_JOB:
                final FrameLayout container = new FrameLayout(parent.getContext());
                container.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                if (!isRequestDismissalEnabled()) {
                    addBottomBorder(container, R.drawable.border_gray_bottom);
                }
                return new JobViewHolder(container);
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

    private boolean isRequestDismissalEnabled() {
        final ConfigurationResponse configuration = mConfigManager.getConfigurationResponse();
        return configuration != null
                && configuration.getRequestDismissal() != null
                && configuration.getRequestDismissal().isEnabled();
    }

    private void addBottomBorder(final View view, final int bottomBorderDrawableId) {
        final int oneDp = UIUtils.calculatePxToDp(view.getContext(), 1);
        view.setPadding(0, 0, 0, oneDp);
        view.setBackgroundResource(bottomBorderDrawableId);
    }

    private abstract class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(final View itemView) {
            super(itemView);
        }

        abstract void init(Object item);
    }


    private class DateViewHolder extends BaseViewHolder {
        DateViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            ((ProRequestedJobsListGroupView) itemView)
                    .updateDisplay((Date) item, itemView.getContext());
        }
    }


    private class JobViewHolder extends RequestedJobsRecyclerViewAdapter.BaseViewHolder {
        JobViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            View convertView = null;
            final ViewGroup parentView = (ViewGroup) itemView;
            if (parentView.getChildCount() == 1) {
                convertView = parentView.getChildAt(0);
            }

            final Booking booking = (Booking) item;

            BookingElementView bookingElementView;
            final boolean isRequestDismissalEnabled = isRequestDismissalEnabled();
            if (isRequestDismissalEnabled) {
                bookingElementView = new DismissableBookingElementView();
            }
            else {
                bookingElementView = new AvailableBookingElementView();
            }

            bookingElementView.initView(parentView.getContext(), booking, convertView, parentView);
            final View associatedView = bookingElementView.getAssociatedView();
            initActionListeners(associatedView, booking);
            // Hide requested pro indicator because this is a list view that displays only pro requests.
            final View requestedIndicator =
                    associatedView.findViewById(R.id.booking_list_entry_left_strip_indicator);
            if (requestedIndicator != null) {
                requestedIndicator.setVisibility(View.INVISIBLE);
            }

            if (parentView.getChildCount() == 0) {
                parentView.addView(associatedView);
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
