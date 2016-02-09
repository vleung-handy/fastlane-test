package com.handy.portal.ui.element.dashboard;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RatingsProPerformanceView extends FrameLayout
{
    @Inject
    Bus mBus;

    @Bind(R.id.jobs_ratings_layout)
    ViewGroup mJobRatingsLayout;

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
    }

    public void addItem(String mainText, String subtitleText){
        JobRatingView jobRatingView = new JobRatingView(getContext());
        jobRatingView.setText(mainText, subtitleText);
        mJobRatingsLayout.addView(jobRatingView, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
    }

    @OnClick(R.id.reviews_option)
    public void switchToReviews(){
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.DASHBOARD_REVIEWS));
    }
}
