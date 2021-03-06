package com.handy.portal.clients.ui.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.ui.element.BookingElementView;
import com.handy.portal.bookings.ui.element.DismissableBookingElementView;
import com.handy.portal.clients.ui.element.RequestedJobsDateView;
import com.handy.portal.clients.ui.element.RequestedJobsHeaderView;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FontUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.EventContext;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class RequestedJobsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;

    @Inject
    EventBus mBus;

    private static final int VIEW_TYPE_NONE = 0;
    private static final int VIEW_TYPE_DATE = 1;
    private static final int VIEW_TYPE_JOB = 2;
    private static final int VIEW_TYPE_HEADER = 3;
    private static final int VIEW_TYPE_DIVIDER = 4;
    private static final int VIEW_TYPE_EMPTY = 5;
    private List<Object> mItems;
    private JobViewHolder.Listener mJobViewHolderListener;

    public RequestedJobsRecyclerViewAdapter(
            final Context context,
            final List<BookingsWrapper> jobList,
            final JobViewHolder.Listener jobViewHolderListener
    ) {
        mContext = context;
        mJobViewHolderListener = jobViewHolderListener;
        Utils.inject(context, this);

        final List<Booking> exclusiveBookings = new ArrayList<>();
        final List<Booking> regularBookings = new ArrayList<>();
        for (BookingsWrapper bookingsWrapper : jobList) {
            final List<Booking> undismissedBookings = bookingsWrapper.getUndismissedBookings();
            for (final Booking booking : undismissedBookings) {
                if (booking.isExclusiveRequest()) {
                    exclusiveBookings.add(booking);
                }
                else {
                    regularBookings.add(booking);
                }
            }
        }

        mItems = new ArrayList<>();
        populateItemsWithSection(
                mContext.getString(R.string.exclusive_requests),
                mContext.getString(R.string.exclusive_requests_help_content),
                exclusiveBookings,
                true
        );
        mItems.add(VIEW_TYPE_DIVIDER);
        if (!regularBookings.isEmpty()) {
            populateItemsWithSection(
                    mContext.getString(R.string.other_requests),
                    mContext.getString(R.string.other_requests_help_content),
                    regularBookings,
                    false
            );
        }
    }

    private void populateItemsWithSection(
            final String title,
            @Nullable final String helpContent,
            final List<Booking> bookings,
            final boolean showEmptyState
    ) {
        mItems.add(new RequestedJobsHeaderView.ViewModel(title, helpContent));
        if (bookings.isEmpty() && showEmptyState) {
            mItems.add(title);
        }
        for (final Booking booking : bookings) {
            final Object previousItem = mItems.get(mItems.size() - 1);
            if (!(previousItem instanceof Booking)
                    || !DateTimeUtils.isOnSameDay(((Booking) previousItem).getStartDate(), booking.getStartDate())) {
                mItems.add(DateTimeUtils.getDateWithoutTime(booking.getStartDate()));
            }
            mItems.add(booking);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                return new HeaderViewHolder(new RequestedJobsHeaderView(mContext));
            case VIEW_TYPE_DATE:
                return new DateViewHolder(new RequestedJobsDateView(mContext));
            case VIEW_TYPE_JOB:
                final FrameLayout container = new FrameLayout(mContext);
                container.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                return new JobViewHolder(container, EventContext.REQUESTED_JOBS, mJobViewHolderListener);
            case VIEW_TYPE_EMPTY:
                return new EmptyStateViewHolder(createEmptyStateTextView());
            case VIEW_TYPE_DIVIDER:
                return new BaseViewHolder(createSectionDivider());
        }
        return null;
    }

    private TextView createEmptyStateTextView() {
        final TextView textView = new TextView(mContext);
        final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        final int doublePadding = (int) mContext.getResources().getDimension(R.dimen.default_padding_double);
        final int padding = (int) mContext.getResources().getDimension(R.dimen.default_padding);
        textView.setPadding(0, doublePadding, 0, padding);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.handy_text_gray));
        textView.setTypeface(FontUtils.getFont(mContext, FontUtils.CIRCULAR_BOOK));
        textView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.medium_text_size)
        );
        textView.setGravity(Gravity.CENTER);
        return textView;
    }

    private View createSectionDivider() {
        final FrameLayout divider = new FrameLayout(mContext);
        LayoutInflater.from(mContext).inflate(R.layout.divider, divider);
        final int padding = (int) mContext.getResources().getDimension(R.dimen.default_padding);
        divider.setPadding(padding, padding, padding, 0);
        return divider;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position >= 0 && position < mItems.size()) {
            final Object item = mItems.get(position);
            if (item instanceof RequestedJobsHeaderView.ViewModel) {
                return VIEW_TYPE_HEADER;
            }
            else if (item instanceof Date) {
                return VIEW_TYPE_DATE;
            }
            else if (item instanceof Booking) {
                return VIEW_TYPE_JOB;
            }
            else if (item instanceof String) {
                return VIEW_TYPE_EMPTY;
            }
            else if (item instanceof Integer) {
                return (Integer) item;
            }
        }
        return VIEW_TYPE_NONE;
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

        int positionToRemove = position - 1;
        int positionAfter = position;
        // Remove date section if necessary
        if (getItemViewType(positionToRemove) == VIEW_TYPE_DATE
                && getItemViewType(positionAfter) != VIEW_TYPE_JOB) {
            mItems.remove(positionToRemove);
            notifyItemRemoved(positionToRemove);
            notifyItemRangeChanged(positionToRemove, mItems.size());

            // Remove section header if necessary
            positionToRemove = position - 2;
            positionAfter = position - 1;
            if (getItemViewType(positionToRemove) == VIEW_TYPE_HEADER
                    && getItemViewType(positionAfter) != VIEW_TYPE_DATE) {
                mItems.remove(positionToRemove);
                notifyItemRemoved(positionToRemove);
                notifyItemRangeChanged(positionToRemove, mItems.size());

                // Remove divider if necessary
                if (getItemViewType(positionToRemove) == VIEW_TYPE_DIVIDER) {
                    mItems.remove(positionToRemove);
                    notifyItemRemoved(positionToRemove);
                    notifyItemRangeChanged(positionToRemove, mItems.size());
                }
            }
        }
    }

    private static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(final View itemView) {
            super(itemView);
        }

        void init(Object item) {
            // do nothing
        }
    }


    private static class HeaderViewHolder extends BaseViewHolder {
        HeaderViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            ((RequestedJobsHeaderView) itemView).bind((RequestedJobsHeaderView.ViewModel) item);
        }
    }


    private static class DateViewHolder extends BaseViewHolder {
        DateViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            ((RequestedJobsDateView) itemView)
                    .updateDisplay((Date) item, itemView.getContext());
        }
    }


    private static class EmptyStateViewHolder extends BaseViewHolder {
        EmptyStateViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        void init(final Object item) {
            ((TextView) itemView).setText(itemView.getContext().getString(
                    R.string.empty_requests_formatted,
                    ((String) item).toLowerCase()
            ));
        }
    }


    public static class JobViewHolder extends BaseViewHolder {
        private final Listener mListener;
        private String mOriginEventContext;
        private CountDownTimer mCountDownTimer;

        public JobViewHolder(
                final View itemView,
                final String originEventContext,
                final Listener listener
        ) {
            super(itemView);
            mOriginEventContext = originEventContext;
            mListener = listener;
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
            associatedView.setAlpha(1.0f);

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

            final TextView expirationTimer =
                    (TextView) associatedView.findViewById(R.id.booking_entry_expiration_timer);
            if (expirationTimer != null) {
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
                final Booking.RequestAttributes requestAttributes = booking.getRequestAttributes();
                if (requestAttributes != null && requestAttributes.getExpirationDate() != null) {
                    expirationTimer.setVisibility(View.VISIBLE);
                    final Date expirationDate = requestAttributes.getExpirationDate();
                    long millisInFuture = expirationDate.getTime() - new Date().getTime();
                    if (millisInFuture > 0) {
                        initExpirationTimer(associatedView, expirationTimer, millisInFuture);
                    }
                    else {
                        expireJob(associatedView);
                    }
                }
                else {
                    expirationTimer.setVisibility(View.GONE);
                }
            }
        }

        private void initExpirationTimer(
                final View associatedView,
                final TextView expirationTimer,
                final long millisInFuture
        ) {
            final Context context = expirationTimer.getContext();
            mCountDownTimer = new CountDownTimer(
                    millisInFuture,
                    DateUtils.SECOND_IN_MILLIS
            ) {
                @Override
                public void onTick(final long millisUntilFinished) {
                    expirationTimer.setText(getExpirationText(context, millisUntilFinished));
                }

                @Override
                public void onFinish() {
                    expireJob(associatedView);
                }
            };
            mCountDownTimer.start();
        }

        private String getExpirationText(final Context context, final long millisUntilFinished) {
            int daysUntilFinished = DateTimeUtils.millisToDays(millisUntilFinished);
            if (daysUntilFinished == 0) {
                return context.getString(
                        R.string.expiration_timer_formatted,
                        DateTimeUtils.millisecondsToFormattedString(millisUntilFinished)
                );
            }
            else if (daysUntilFinished == 1) {
                return context.getString(R.string.expiration_tomorrow);
            }
            else {
                return context.getString(R.string.expiration_days_formatted, daysUntilFinished);
            }
        }

        private void initActionListeners(final View associatedView, final Booking booking) {
            associatedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mListener.onSelect(booking);
                }
            });

            final View bottomButtomGroup =
                    associatedView.findViewById(R.id.booking_entry_bottom_button_group);
            if (bottomButtomGroup != null) {
                bottomButtomGroup.setVisibility(View.VISIBLE);
            }

            final View claimButton = associatedView.findViewById(R.id.claim_button);
            if (claimButton != null) {
                claimButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        mListener.onClaim(booking);
                    }
                });
            }

            final View dismissButton = associatedView.findViewById(R.id.dismiss_button);
            if (dismissButton != null) {
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        mListener.onDismiss(booking);
                    }
                });
            }

            final View sendAlternateTimesButton =
                    associatedView.findViewById(R.id.reschedule_button);
            if (sendAlternateTimesButton != null) {
                final Booking.Action sendTimesAction =
                        booking.getAction(Booking.Action.ACTION_SEND_TIMES);
                if (sendTimesAction != null) {
                    sendAlternateTimesButton.setVisibility(View.VISIBLE);
                    sendAlternateTimesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            mListener.onReschedule(booking);
                        }
                    });
                }
                else {
                    sendAlternateTimesButton.setVisibility(View.INVISIBLE);
                }
            }
        }

        private void expireJob(final View associatedView) {
            final TextView expirationTimer =
                    (TextView) associatedView.findViewById(R.id.booking_entry_expiration_timer);
            if (expirationTimer != null) {
                expirationTimer.setText(associatedView.getContext().getString(
                        R.string.expiration_timer_formatted,
                        DateTimeUtils.millisecondsToFormattedString(0)
                ));
            }
            associatedView.setAlpha(0.5f);
            UIUtils.disableClicks(associatedView);
        }

        public interface Listener {
            void onSelect(Booking booking);

            void onClaim(Booking booking);

            void onDismiss(Booking booking);

            void onReschedule(Booking booking);
        }
    }
}
