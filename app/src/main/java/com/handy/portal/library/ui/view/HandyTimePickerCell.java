package com.handy.portal.library.ui.view;

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

public class HandyTimePickerCell extends FrameLayout
{
    private static final SimpleDateFormat HOUR_FORMAT =
            new SimpleDateFormat("HH", Locale.getDefault());

    @BindView(R.id.time_text)
    TextView mTimeText;

    private final int mTime;
    private final TimeClickListener mTimeClickListener;

    public HandyTimePickerCell(final Context context,
                               final int time,
                               @NonNull final TimeClickListener timeClickListener)
    {
        super(context);
        mTime = time;
        mTimeClickListener = timeClickListener;
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_time_picker_cell, this);
        ButterKnife.bind(this);
        final Date date = DateTimeUtils.parseDateString(String.valueOf(mTime), HOUR_FORMAT);
        mTimeText.setText(DateTimeUtils.formatDateToHour(date));
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                mTimeClickListener.onTimeClick(getTime());
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

    public int getTime()
    {
        return mTime;
    }

    interface TimeClickListener
    {
        void onTimeClick(int time);
    }
}
