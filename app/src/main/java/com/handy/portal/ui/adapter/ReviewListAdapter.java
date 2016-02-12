package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.util.DateTimeUtils;

import java.util.List;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder>
{
    private Context mContext;
    private List<ProviderRating> mRatings;

    public ReviewListAdapter(@NonNull final Context context, @NonNull final List<ProviderRating> ratings)
    {
        mContext = context;
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
        String date = DateTimeUtils.getMonthAndYear(rating.getDateRating());
        holder.mDateTextView.setText(mContext.getString(R.string.comma_formatted, rating.getSource(), date));
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
