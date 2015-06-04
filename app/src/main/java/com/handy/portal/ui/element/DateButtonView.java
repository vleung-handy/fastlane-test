package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DateButtonView extends LinearLayout
{
    @InjectView(R.id.date_month_text)
    protected TextView monthText;

    @InjectView(R.id.date_day_of_week_text)
    protected TextView dayOfWeekText;

    @InjectView(R.id.date_day_of_month_text)
    protected TextView dayOfMonthText;

    @InjectView(R.id.provider_requested_indicator_image)
    protected ImageView requestedIndicator;

    @InjectView(R.id.selected_day_indicator_image)
    protected ImageView selectedDayIndicator;

    private static final String DATE_FORMAT = "MMM E d";

    public DateButtonView(final Context context)
    {
        super(context);
    }

    public DateButtonView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DateButtonView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void init(Date date, boolean requestedJobsThisDay)
    {
        ButterKnife.inject(this);

        requestedIndicator.setVisibility(requestedJobsThisDay ? View.VISIBLE : View.INVISIBLE);
        selectedDayIndicator.setVisibility(View.INVISIBLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String[] formattedDate = dateFormat.format(date.getTime()).split(" ");

        //only display month for first day in a month
        if (Integer.parseInt(formattedDate[2]) == 1)
        {
            monthText.setText(formattedDate[0]);
        }
        else
        {
            monthText.setVisibility(View.INVISIBLE);
        }

        dayOfWeekText.setText(formattedDate[1]);
        dayOfMonthText.setText(formattedDate[2]);
    }

    public void setChecked(boolean checked)
    {
        selectedDayIndicator.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
        setBackgroundColor(getResources().getColor(checked ? R.color.bg_active_grey : R.color.bg_inactive_grey));
    }
}
