package com.handy.portal.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.onboarding.BookingsWrapperViewModel;
import com.handy.portal.ui.view.OnboardJobGroupView;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.RecyclerViewHolder>
{
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    //this is the view model
    List<BookingsWrapperViewModel> mBookingsWrapperViewModels;
    String mTitle;

    OnboardJobGroupView.OnJobChangeListener mOnJobChangeListener;

    public JobsRecyclerAdapter(List<BookingsWrapper> bookings, String title,
                               OnboardJobGroupView.OnJobChangeListener mListener)
    {

        mBookingsWrapperViewModels = new ArrayList<>();

        //adding place holders for the header position
        mBookingsWrapperViewModels.add(null);

        //We assume there is at least one job. We filter out the wrappers without a job
        for (BookingsWrapper booking : bookings)
        {
            if (booking.getBookings() == null || booking.getBookings().isEmpty())
            {
                continue;
            }

            mBookingsWrapperViewModels.add(new BookingsWrapperViewModel(booking));
        }

        mOnJobChangeListener = mListener;
        mTitle = title;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View layoutView = null;
        switch (viewType)
        {
            case TYPE_HEADER:
                layoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.getting_started_header, parent, false);

                ((TextView) layoutView).setText(mTitle);

                break;
            case TYPE_ITEM:
                layoutView = new OnboardJobGroupView(parent.getContext());
                ((OnboardJobGroupView) layoutView).setOnJobChangeListener(mOnJobChangeListener);
                break;
        }

        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return rcv;
    }

    public List<BookingsWrapperViewModel> getBookingsWrapperViewModels()
    {
        return mBookingsWrapperViewModels;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        if (holder.getItemViewType() == TYPE_ITEM)
        {
            holder.mJobView.bind(mBookingsWrapperViewModels.get(position));
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0)
        {
            return TYPE_HEADER;
        }
        else
        {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount()
    {
        return mBookingsWrapperViewModels.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        public OnboardJobGroupView mJobView;

        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            if (itemView instanceof OnboardJobGroupView)
            {
                mJobView = (OnboardJobGroupView) itemView;
            }
        }
    }
}
