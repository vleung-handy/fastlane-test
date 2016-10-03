package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingProgress;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScheduledBookingElementAdapter extends ArrayAdapter<Booking> {
    private LayoutInflater inflater;

    private static final int FIND_JOB_TYPE = 1;

    public ScheduledBookingElementAdapter(Context context, List<Booking> bookings) {
        super(context, 0, bookings);
        this.inflater = LayoutInflater.from(getContext());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Booking booking = getItem(position);

        if (getItemViewType(position) == FIND_JOB_TYPE) {
            FindJobViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.element_scheduled_booking_find_job_row, parent, false);
                holder = new FindJobViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (FindJobViewHolder) convertView.getTag();
            }

            updateFindJobRow(holder, booking);
        } else {
            ScheduleRowViewHolder holder;
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.element_scheduled_booking_list_entry, parent, false);
                holder = new ScheduleRowViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ScheduleRowViewHolder) convertView.getTag();
            }

            updateScheduleRow(holder, booking);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        //If this is a schedule booking then return item 1
        if (getItem(position) instanceof ScheduledBookingFindJob) {
            return FIND_JOB_TYPE;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private void updateFindJobRow(FindJobViewHolder holder, Booking booking) {
        //Date and Time
        final String formattedStartDate = DateTimeUtils.formatDateTo12HourClock(booking.getStartDate()).toLowerCase();
        final String formattedEndDate = DateTimeUtils.formatDateTo12HourClock(booking.getEndDate()).toLowerCase();

        holder.findJobTime.setText(getContext().getString(R.string.find_jobs_between, formattedStartDate, formattedEndDate));
    }

    private void updateScheduleRow(ScheduleRowViewHolder holder, Booking booking) {
        //Location
        holder.addressTextView.setText(booking.getFormattedLocation(Booking.BookingStatus.CLAIMED));

        // Status
        holder.completedIndicator.setVisibility(View.GONE);
        holder.claimedIndicator.setVisibility(View.GONE);
        switch (booking.getBookingProgress()) {
            case BookingProgress.READY_FOR_ON_MY_WAY:
                holder.statusText.setText(R.string.claimed);
                holder.statusText.setTextColor(ContextCompat.getColor(getContext(), R.color.handyman_teal));
                holder.claimedIndicator.setVisibility(View.VISIBLE);
                break;
            case BookingProgress.READY_FOR_CHECK_IN:
            case BookingProgress.READY_FOR_CHECK_OUT:
                holder.statusText.setText(R.string.in_progress);
                holder.statusText.setTextColor(ContextCompat.getColor(getContext(), R.color.handyman_teal));
                holder.claimedIndicator.setVisibility(View.VISIBLE);
                break;
            case BookingProgress.FINISHED:
                holder.statusText.setText(R.string.completed);
                holder.statusText.setTextColor(ContextCompat.getColor(getContext(), R.color.black_pressed));
                holder.completedIndicator.setVisibility(View.VISIBLE);
                break;
            default:
                holder.statusText.setVisibility(View.GONE);
        }

        //Date and Time
        final String formattedStartDate = DateTimeUtils.formatDateTo12HourClock(booking.getStartDate());
        final String formattedEndDate = DateTimeUtils.formatDateTo12HourClock(booking.getEndDate());
        holder.timeText.setText(getContext().getString(R.string.booking_time,
                formattedStartDate.toLowerCase(), formattedEndDate.toLowerCase()));

        //Service or frequency for home cleaning jobs
        UIUtils.setService(holder.bookingServiceTextView, booking);
    }

    class FindJobViewHolder {
        @BindView(R.id.booking_entry_find_job_time)
        TextView findJobTime;

        public FindJobViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ScheduleRowViewHolder {
        @BindView(R.id.booking_entry_address_text)
        TextView addressTextView;
        @BindView(R.id.booking_entry_claimed_indicator)
        ImageView claimedIndicator;
        @BindView(R.id.booking_entry_status_text)
        TextView statusText;
        @BindView(R.id.booking_entry_completed_indicator)
        ImageView completedIndicator;
        @BindView(R.id.booking_entry_service_text)
        TextView bookingServiceTextView;
        @BindView(R.id.booking_entry_date_text)
        TextView timeText;

        public ScheduleRowViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
