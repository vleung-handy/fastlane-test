package com.handy.portal.ui.element.dashboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class JobRatingView extends FrameLayout
{
    @Bind(R.id.main_layout)
    LinearLayout mMainLayout;
    @Bind(R.id.main_text)
    TextView mMainText;
    @Bind(R.id.subtitle_text)
    TextView mSubtitleText;

    public JobRatingView(final Context context)
    {
        super(context);
        init();
    }

    public JobRatingView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public JobRatingView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JobRatingView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_job_rating, this);
        ButterKnife.bind(this);
    }

    public void setText(String mainText, String subtitleText)
    {
        mMainText.setText(mainText);
        mSubtitleText.setText(subtitleText);
    }
}
