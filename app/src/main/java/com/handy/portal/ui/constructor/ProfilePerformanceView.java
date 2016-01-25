package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.util.Utils;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfilePerformanceView extends FrameLayout
{
    @Bind(R.id.profile_section_header_title_text)
    TextView mTitleText;
    @Bind(R.id.profile_section_header_subtitle_text)
    TextView mSubtitleText;
    @Bind(R.id.tier_text)
    TextView mTierText;
    @Bind(R.id.tier_label)
    TextView mTierLabel;
    @Bind(R.id.trailing_rating_text)
    TextView mTrailingRatingText;
    @Bind(R.id.trailing_jobs_text)
    TextView mTrailingJobsText;
    @Bind(R.id.trailing_rate_text)
    TextView mTrailingRateText;

    private PerformanceInfo mPerformanceInfo;

    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.00");

    public ProfilePerformanceView(final Context context, @NonNull final PerformanceInfo performanceInfo)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_profile_performance, this);
        ButterKnife.bind(this);

        mPerformanceInfo = performanceInfo;

        mTitleText.setText(R.string.performance);
        mSubtitleText.setText(R.string.based_on_last_28_days);

        if (mPerformanceInfo.getTier() > 0)
        {
            mTierText.setText(getContext().getString(R.string.tier_x, mPerformanceInfo.getTier()));
        }
        else
        {
            mTierText.setVisibility(View.GONE);
            mTierLabel.setVisibility(View.GONE);
        }

        mTrailingRatingText.setText(RATING_FORMAT.format(mPerformanceInfo.getTrailing28DayRating()));
        mTrailingJobsText.setText(Integer.toString(mPerformanceInfo.getTrailing28DayJobsCount()));
        mTrailingRateText.setText(mPerformanceInfo.getRate());
    }
}
