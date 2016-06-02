package com.handy.portal.onboarding.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.onboarding.model.BookingViewModel;
import com.handy.portal.onboarding.model.BookingsWrapperViewModel;
import com.handy.portal.onboarding.ui.view.OnboardingJobsViewGroup;

import java.util.ArrayList;
import java.util.List;

public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.RecyclerViewHolder>
{
    private List<BookingsWrapperViewModel> mBookingsWrapperViewModels;
    private OnboardingJobsViewGroup.OnJobCheckedChangedListener mOnJobCheckedChangedListener;

    public JobsRecyclerAdapter(List<BookingsWrapper> bookings,
                               OnboardingJobsViewGroup.OnJobCheckedChangedListener mListener)
    {

        mBookingsWrapperViewModels = new ArrayList<>();

        //We assume there is at least one job. We filter out the wrappers without a job
        for (BookingsWrapper bookingsWrapper : bookings)
        {
            if (bookingsWrapper.getBookings() == null || bookingsWrapper.getBookings().isEmpty())
            {
                continue;
            }

            mBookingsWrapperViewModels.add(new BookingsWrapperViewModel(bookingsWrapper));
        }

        mOnJobCheckedChangedListener = mListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        OnboardingJobsViewGroup layoutView = new OnboardingJobsViewGroup(parent.getContext());
        layoutView.setOnJobCheckedChangedListener(mOnJobCheckedChangedListener);
        return new RecyclerViewHolder(layoutView);
    }

    public List<BookingsWrapperViewModel> getBookingsWrapperViewModels()
    {
        return mBookingsWrapperViewModels;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        holder.mJobView.bind(mBookingsWrapperViewModels.get(position));
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getItemCount()
    {
        return mBookingsWrapperViewModels.size();
    }

    public List<Booking> getSelectedBookings()
    {
        final ArrayList<Booking> bookings = new ArrayList<>();
        for (BookingsWrapperViewModel model : getBookingsWrapperViewModels())
        {
            if (model != null)
            {
                for (BookingViewModel bookingView : model.getBookingViewModels())
                {
                    if (bookingView.isSelected())
                    {
                        bookings.add(bookingView.getBooking());
                    }
                }
            }
        }
        return bookings;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public OnboardingJobsViewGroup mJobView;

        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            if (itemView instanceof OnboardingJobsViewGroup)
            {
                mJobView = (OnboardingJobsViewGroup) itemView;
            }
        }
    }
}
