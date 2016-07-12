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


public class JobRatingView extends FrameLayout
{
    @BindView(R.id.number_text)
    TextView mNumberText;
    @BindView(R.id.description_text)
    TextView mDescriptionText;

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

    public void setNumber(String number)
    {
        mNumberText.setText(number);
    }

    public void setDescription(String description)
    {
        mDescriptionText.setText(description);
    }
}
