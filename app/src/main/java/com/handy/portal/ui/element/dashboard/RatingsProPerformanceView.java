package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RatingsProPerformanceView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.five_star_ratings_view)
    JobRatingView mFiveStarRatingsView;
    @Bind(R.id.rated_jobs_view)
    JobRatingView mRatedJobsView;
    @Bind(R.id.total_jobs_view)
    JobRatingView mTotalJobsView;
    @Bind(R.id.jobs_ratings_layout)
    ViewGroup mJobRatingsLayout;
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
        Utils.inject(getContext(), this);
        inflate(getContext(), R.layout.element_ratings_pro_performance, this);
        ButterKnife.bind(this);

        mFiveStarRatingsView.setDescription(getResources().getString(R.string.five_star_ratings));
        mRatedJobsView.setDescription(getResources().getString(R.string.rated_jobs));
        mTotalJobsView.setDescription(getResources().getString(R.string.total_jobs));
    }

    public void setJobRatings(String fiveStarRatings, String ratedJobs, String totalJobs)
    {
        mFiveStarRatingsView.setNumber(fiveStarRatings);
        mRatedJobsView.setNumber(ratedJobs);
        mTotalJobsView.setNumber(totalJobs);
    }

    public void setDate(String date)
    {
        mDateText.setText(date);
    }
}
