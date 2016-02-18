package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RatingsProPerformanceView extends FrameLayout
{
    @Bind(R.id.five_star_title)
    TextView mTitle;
    @Bind(R.id.five_star_ratings_view)
    JobRatingView mFiveStarRatingsView;
    @Bind(R.id.five_star_percentage_view)
    PercentageCircleView mPercentageCircleView;
    @Bind(R.id.rated_jobs_view)
    JobRatingView mRatedJobsView;
    @Bind(R.id.total_jobs_view)
    JobRatingView mTotalJobsView;
    @Bind(R.id.date_text)
    TextView mDateText;

    public RatingsProPerformanceView(final Context context)
    {
        super(context);
        init();
    }

    public RatingsProPerformanceView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public RatingsProPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatingsProPerformanceView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_ratings_pro_performance, this);
        ButterKnife.bind(this);

        mPercentageCircleView.setColor(ContextCompat.getColor(getContext(), R.color.white),
                ContextCompat.getColor(getContext(), R.color.bg_inactive_grey),
                ContextCompat.getColor(getContext(), R.color.requested_green),
                ContextCompat.getColor(getContext(), R.color.handy_yellow),
                ContextCompat.getColor(getContext(), R.color.error_red));
        mPercentageCircleView.setSign(getResources().getString(R.string.percentage));
        mPercentageCircleView.setSubText(getResources().getString(R.string.five_star));

        mFiveStarRatingsView.setDescription(getResources().getString(R.string.five_star_ratings));
        mRatedJobsView.setDescription(getResources().getString(R.string.rated_jobs));
        mTotalJobsView.setDescription(getResources().getString(R.string.total_jobs));
    }

    public void setJobRatings(int fiveStarRatings, int ratedJobs, int totalJobs)
    {
        mFiveStarRatingsView.setNumber(Integer.toString(fiveStarRatings));
        mRatedJobsView.setNumber(Integer.toString(ratedJobs));
        mTotalJobsView.setNumber(Integer.toString(totalJobs));
    }

    public void setDate(String date)
    {
        mDateText.setText(date);
    }

    public void setTitle(final CharSequence text)
    {
        mTitle.setText(text);
    }

    public void setPercentage(float percentage)
    {
        mPercentageCircleView.setPercentage(percentage);
    }
}
