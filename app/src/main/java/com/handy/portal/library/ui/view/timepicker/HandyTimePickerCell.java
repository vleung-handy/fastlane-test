package com.handy.portal.library.ui.view.timepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class HandyTimePickerCell extends FrameLayout
{
    private static final SimpleDateFormat HOUR_FORMAT =
            new SimpleDateFormat("HH", Locale.getDefault());

    @BindView(R.id.time_text)
    TextView mTimeText;

    private final int mHour;
    private final TimeClickListener mTimeClickListener;

    public HandyTimePickerCell(final Context context,
                               final int hour,
                               @NonNull final TimeClickListener timeClickListener)
    {
        super(context);
        mHour = hour;
        mTimeClickListener = timeClickListener;
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_time_picker_cell, this);
        ButterKnife.bind(this);
        final Date date = DateTimeUtils.parseDateString(String.valueOf(mHour), HOUR_FORMAT);
        mTimeText.setText(DateTimeUtils.formatDateToHour(date));
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                mTimeClickListener.onHourClicked(getHour());
            }
        });
    }

    public void reset()
    {
        mTimeText.setBackground(null);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    public void highlight()
    {
        mTimeText.setBackgroundResource(R.drawable.circle_inactive_gray);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    public void select()
    {
        mTimeText.setBackgroundResource(R.drawable.circle_tertiary_gray);
        mTimeText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    public int getHour()
    {
        return mHour;
    }

    interface TimeClickListener
    {
        void onHourClicked(int time);
    }
}
