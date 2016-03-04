package com.handy.portal.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.util.DateTimeUtils;

import java.util.Date;
import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder>
{
    private List<ProviderRating> mRatings;
    private boolean mDoneLoading = false;

    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_ACTIVITY = 1;

    public ReviewListAdapter(@NonNull final List<ProviderRating> ratings)
    {
        mRatings = ratings;
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == VIEW_TYPE_ACTIVITY)
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.five_star_review, parent, false);
            return new ViewHolder(v, (TextView) v.findViewById(R.id.five_star_review_text),
                    (TextView) v.findViewById(R.id.review_date));
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.element_progress_bar, parent, false);
            return new ViewHolder(v, null, null);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if (position < mRatings.size())
        {
            ProviderRating rating = mRatings.get(position);
            holder.bind(rating);
        }
    }

    @Override
    public int getItemCount()
    {
        int addedProgressBar = shouldAddProgressBar() ? 1 : 0;
        return mRatings.size() + addedProgressBar;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView mReviewTextView;
        private TextView mDateTextView;

        public ViewHolder(View parent, TextView review, TextView date)
        {
            super(parent);
            mReviewTextView = review;
            mDateTextView = date;
        }

        public void bind(ProviderRating rating)
        {
            mReviewTextView.setText(rating.getComment());
            mDateTextView.setText(DateTimeUtils.getMonthAndYear(rating.getDateRating()));
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position < mRatings.size())
        {
            return VIEW_TYPE_ACTIVITY;
        }
        else
        {
            return VIEW_TYPE_LOADING;
        }
    }

    @Nullable
    public Date getToBookingDate()
    {
        if (mRatings.isEmpty()) {
            return null;
        }
        else
        {
            return mRatings.get(mRatings.size() - 1).getBookingDate();
        }
    }

    public void setDoneLoading(boolean doneLoading)
    {
        mDoneLoading = doneLoading;
    }

    private boolean shouldAddProgressBar()
    {
        return !mRatings.isEmpty() && !mDoneLoading;
    }
}
