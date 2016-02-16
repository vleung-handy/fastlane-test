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

public class DashboardFeedbackHighlightView extends FrameLayout
{
    @Bind(R.id.feedback_highlight_text)
    TextView mFeedbackHighlightText;

    public DashboardFeedbackHighlightView(final Context context, CharSequence feedbackHighlight)
    {
        super(context);
        init();
        setText(feedbackHighlight);
    }

    public DashboardFeedbackHighlightView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DashboardFeedbackHighlightView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DashboardFeedbackHighlightView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_dashboard_feedback_highlight, this);
        ButterKnife.bind(this);
    }

    public void setText(CharSequence highlight)
    {
        mFeedbackHighlightText.setText(highlight);
    }
}
