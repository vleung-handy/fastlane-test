package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewDateButtonView extends LinearLayout
{
    @BindView(R.id.day_text)
    TextView mDayText;
    @BindView(R.id.month_text)
    TextView mMonthText;
    @BindView(R.id.date_text)
    TextView mDateText;
    @BindView(R.id.schedule_indicator)
    View mScheduleIndicator;

    public NewDateButtonView(final Context context, final Date date)
    {
        super(context);
        init(date);
    }

    private void init(final Date date)
    {
        inflate(getContext(), R.layout.element_date_button_new, this);
        final LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        setLayoutParams(layoutParams);
        setBackgroundResource(R.color.white);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);

        ButterKnife.bind(this);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mDayText.setText(String.valueOf(DateTimeUtils.getDayOfWeek(date).charAt(0)));
        mDateText.setText(String.valueOf(dayOfMonth));
        if (dayOfMonth == 1)
        {
            mMonthText.setText(DateTimeUtils.getMonthShortName(date));
            mMonthText.setVisibility(VISIBLE);
        }
        else
        {
            mMonthText.setVisibility(INVISIBLE);
        }
    }

    public void setScheduleIndicator(final boolean isShown)
    {
        mScheduleIndicator.setVisibility(isShown ? VISIBLE : INVISIBLE);
    }
}
