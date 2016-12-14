package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewDateButtonView extends LinearLayout
{
    @BindView(R.id.day_text)
    TextView mDayOfWeekText;
    @BindView(R.id.month_text)
    TextView mMonthText;
    @BindView(R.id.date_text)
    TextView mDayOfMonthText;
    @BindView(R.id.schedule_indicator)
    ImageView mScheduleIndicator;
    @BindColor(R.color.handy_darkened_blue)
    int mDarkBlue;
    @BindColor(R.color.handy_blue)
    int mBlue;
    @BindColor(R.color.black)
    int mBlack;
    @BindColor(R.color.white)
    int mWhite;

    private final Date mDate;
    private final int mDayOfMonth;
    private boolean mIsSelected = false;

    public NewDateButtonView(final Context context, final Date date)
    {
        super(context);
        mDate = date;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        mDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        init();
    }

    private void init()
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

        mDayOfWeekText.setText(String.valueOf(DateTimeUtils.getDayOfWeek(mDate).charAt(0)));
        mDayOfMonthText.setText(String.valueOf(mDayOfMonth));
        final boolean isFirstOfTheMonth = mDayOfMonth == 1;
        mMonthText.setVisibility(isFirstOfTheMonth ? VISIBLE : INVISIBLE);
        if (isFirstOfTheMonth)
        {
            mMonthText.setText(DateTimeUtils.getMonthShortName(mDate));
        }
        refreshState();
    }

    private void refreshState()
    {
        if (mIsSelected)
        {
            mMonthText.setVisibility(INVISIBLE);
            mDayOfMonthText.setTextColor(mWhite);
            mScheduleIndicator.setImageResource(R.drawable.circle_white);
        }
        else
        {
            mMonthText.setVisibility(mDayOfMonth == 1 ? VISIBLE : INVISIBLE);
            final boolean isToday = DateTimeUtils.daysBetween(mDate, new Date()) == 0;
            mDayOfMonthText.setTextColor(isToday ? mDarkBlue : mBlack);
            mScheduleIndicator.setImageResource(R.drawable.circle_handy_blue);
        }
    }

    public void setSelected(final boolean isSelected)
    {
        mIsSelected = isSelected;
        refreshState();
    }

    public void setScheduleIndicator(final boolean isShown)
    {
        mScheduleIndicator.setVisibility(isShown ? VISIBLE : INVISIBLE);
    }
}
