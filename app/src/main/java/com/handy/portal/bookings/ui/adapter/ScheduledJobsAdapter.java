package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.ScheduledBookingElementView;

import java.util.List;

public class ScheduledJobsAdapter extends RecyclerView.Adapter<ViewHolder> {

    private Context mContext;
    private List<Booking> mBookings;
    private JobClickListener mJobClickListener;


    public interface JobClickListener {
        void onJobClick(Booking booking);
    }

    public ScheduledJobsAdapter(
            final Context context,
            final List<Booking> bookings,
            final JobClickListener jobClickListener) {
        mContext = context;
        mBookings = bookings;
        mJobClickListener = jobClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.element_scheduled_booking_list_entry, parent, false);
        return new JobViewHolder(itemView, mJobClickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Booking booking = mBookings.get(position);
        ((JobViewHolder) holder).init(booking);
    }

    @Override
    public int getItemCount() {
        return mBookings.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    private static abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {
        BaseViewHolder(final View itemView) {
            super(itemView);
        }

        abstract void init(T item);
    }


    private static class JobViewHolder extends BaseViewHolder<Booking> {
        private final JobClickListener mJobClickListener;

        JobViewHolder(final View itemView, final JobClickListener jobClickListener) {
            super(itemView);
            mJobClickListener = jobClickListener;
        }

        @Override
        void init(final Booking item) {
            new ScheduledBookingElementView().initView(
                    itemView.getContext(),
                    item,
                    itemView,
                    (ViewGroup) itemView.getParent()
            );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mJobClickListener.onJobClick(item);
                }
            });
        }
    }
}
