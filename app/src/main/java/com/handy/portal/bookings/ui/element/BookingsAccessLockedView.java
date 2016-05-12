package com.handy.portal.bookings.ui.element;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * The view that is shown when bookings access is locked for a certain day
 */
public class BookingsAccessLockedView extends FrameLayout
{
    @Bind(R.id.layout_job_access_locked_title)
    TextView mTitleText;

    @Bind(R.id.layout_job_access_locked_description)
    TextView mDescriptionText;

    @Bind(R.id.layout_job_access_locked_keep_rate_info_button)
    TextView mKeepRateInfoButton;

    public BookingsAccessLockedView(final Context context)
    {
        super(context);
        init();
    }

    public BookingsAccessLockedView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingsAccessLockedView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookingsAccessLockedView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.layout_job_access_locked, this);
        ButterKnife.bind(this);
    }

    public BookingsAccessLockedView setTitleText(String titleText)
    {
        mTitleText.setText(titleText);
        return this;
    }

    public BookingsAccessLockedView setDescriptionText(String descriptionText)
    {
        mDescriptionText.setText(descriptionText);
        return this;
    }

    public BookingsAccessLockedView setKeepRateInfoButtonClickListener(OnClickListener onClickListener)
    {
        mKeepRateInfoButton.setOnClickListener(onClickListener);
        return this;
    }
}
