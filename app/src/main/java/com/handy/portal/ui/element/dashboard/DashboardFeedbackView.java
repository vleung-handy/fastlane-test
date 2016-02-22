package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.ui.widget.BulletTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardFeedbackView extends FrameLayout
{
    @Bind(R.id.dashboard_feedback_title)
    TextView mTitle;
    @Bind(R.id.dashboard_feedback_description)
    TextView mDescription;
    @Bind(R.id.dashboard_feedback_highlights)
    LinearLayout mHighlights;
    @Bind(R.id.dashboard_feedback_tips)
    LinearLayout mTips;


    public DashboardFeedbackView(final Context context, @NonNull final ProviderFeedback providerFeedback)
    {
        super(context);
        init();
        setDisplay(providerFeedback);
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardFeedbackView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_dashboard_feedback, this);
        ButterKnife.bind(this);
    }

    public void setDisplay(@NonNull final ProviderFeedback feedback)
    {
        mTitle.setText(feedback.getTitle());
        mDescription.setText(feedback.getSubtitle());
        mHighlights.addView(new DashboardFeedbackHighlightView(getContext(), feedback.getSubtitle()));

        for (ProviderFeedback.FeedbackTip tip : feedback.getFeedbackTips())
        {
            mTips.addView(new BulletTextView(getContext(), tip.getData()));
        }
    }
}
