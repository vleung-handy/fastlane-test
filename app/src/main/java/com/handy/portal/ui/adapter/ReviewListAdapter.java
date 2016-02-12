package com.handy.portal.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderRating;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder>
{
    private List<ProviderRating> mRatings;

    public ReviewListAdapter(List<ProviderRating> ratings)
    {
        mRatings = ratings;
    }

    @Override
    public ReviewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType)
    {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.five_star_review, parent, false);
        return new ViewHolder(v, (TextView) v.findViewById(R.id.review_text),
                (TextView) v.findViewById(R.id.review_date));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ProviderRating rating = mRatings.get(position);
        holder.mReviewTextView.setText(rating.getComment());
        holder.mDateTextView.setText(rating.getSource() + ", " + rating.getDateRating());
    }

    @Override
    public int getItemCount()
    {
        return mRatings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mReviewTextView;
        public TextView mDateTextView;

        public ViewHolder(View parent, TextView review, TextView date)
        {
            super(parent);
            mReviewTextView = review;
            mDateTextView = date;
        }
    }
}
