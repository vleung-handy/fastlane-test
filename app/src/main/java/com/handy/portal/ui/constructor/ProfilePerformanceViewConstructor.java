package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;

import java.text.DecimalFormat;

import butterknife.InjectView;

public class ProfilePerformanceViewConstructor extends ViewConstructor<PerformanceInfo>
{
    @InjectView(R.id.profile_section_header_title_text)
    TextView titleText;
    @InjectView(R.id.profile_section_header_subtitle_text)
    TextView subtitleText;
    @InjectView(R.id.tier_text)
    TextView tierText;
    @InjectView(R.id.tier_label)
    TextView tierLabel;
    @InjectView(R.id.trailing_rating_text)
    TextView trailingRatingText;
    @InjectView(R.id.trailing_jobs_text)
    TextView trailingJobsText;
    @InjectView(R.id.trailing_rate_text)
    TextView trailingRateText;

    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.00");

    public ProfilePerformanceViewConstructor(@NonNull Context context)
    {
        super(context);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_performance;
    }

    @Override
    protected boolean constructView(ViewGroup container, PerformanceInfo performanceInfo)
    {
        titleText.setText(R.string.performance);
        subtitleText.setText(R.string.based_on_last_28_days);

        if (performanceInfo.getTier() > 0)
        {
            tierText.setText(getContext().getString(R.string.tier_x, performanceInfo.getTier()));
        }
        else
        {
            tierText.setVisibility(View.GONE);
            tierLabel.setVisibility(View.GONE);
        }

        trailingRatingText.setText(RATING_FORMAT.format(performanceInfo.getTrailing28DayRating()));
        trailingJobsText.setText(Integer.toString(performanceInfo.getTrailing28DayJobsCount()));
        trailingRateText.setText(performanceInfo.getRate());

        return true;
    }
}
