package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class DashboardTiersHeaderView extends FrameLayout
{
    @Bind(R.id.current_week_completed_jobs_text)
    TextView mCurrentWeekCompletedJobsText;
    @Bind(R.id.current_week_text)
    TextView mCurrentWeekText;

    public DashboardTiersHeaderView(final Context context)
    {
        super(context);
        init();
    }

    public DashboardTiersHeaderView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardTiersHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardTiersHeaderView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_dashboard_tiers_header, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(int currentWeekCompletedJobs, String currentWeek)
    {
        mCurrentWeekCompletedJobsText.setText(Integer.toString(currentWeekCompletedJobs));
        mCurrentWeekText.setText(getResources().getString(R.string.parentheses_formatted,
                currentWeek));
    }
}
