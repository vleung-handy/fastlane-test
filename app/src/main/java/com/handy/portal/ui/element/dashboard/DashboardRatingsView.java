package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardRatingsView extends FrameLayout
{
    @BindView(R.id.five_star_title)
    TextView mTitle;
    @BindView(R.id.five_star_ratings_view)
    JobRatingView mFiveStarRatingsView;
    @BindView(R.id.rated_jobs_view)
    JobRatingView mRatedJobsView;
    @BindView(R.id.total_jobs_view)
    JobRatingView mTotalJobsView;
    @BindView(R.id.date_text)
    TextView mDateText;
    @BindView(R.id.five_star_progress_percentage_view)
    FiveStarRatingPercentageView mFiveStarProgressPercentageRatingView;

    private boolean mAnimated = false;

    public DashboardRatingsView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardRatingsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardRatingsView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardRatingsView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_ratings_pro_performance, this);
        ButterKnife.bind(this);

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

    /**
     *
     * @param percentage percentage of five star reviews as a float between 0 and 1
     */
    public void setFiveStarRatingPercentage(float percentage)
    {
        mFiveStarProgressPercentageRatingView.setupDecoView(Math.round(percentage * 100));
    }

    public void setContentColor(int colorResourceId)
    {
        mFiveStarProgressPercentageRatingView.setContentColor(colorResourceId);
    }

    public void startAnimation()
    {
        mFiveStarProgressPercentageRatingView.startAnimation();
    }

    public void animateProgressBar()
    {
        mAnimated = true;
        mFiveStarProgressPercentageRatingView.animateProgressBar();
    }

    public void setOnResumeState()
    {
        mAnimated = true;
        mFiveStarProgressPercentageRatingView.setOnResumeState();
    }

    public boolean hasBeenAnimated()
    {
        return mAnimated;
    }
}
