package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.util.DateTimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DateButtonView extends RelativeLayout implements Checkable
{
    @Bind(R.id.date_month_text)
    protected TextView mMonthText;

    @Bind(R.id.date_day_of_week_text)
    protected TextView mDayOfWeekText;

    @Bind(R.id.date_day_of_month_text)
    protected TextView mDayOfMonthText;

    @Bind(R.id.provider_requested_indicator_image)
    protected ImageView mRequestedIndicator;

    @Bind(R.id.claimed_job_exists_indicator_image)
    protected ImageView mClaimedJobExistsIndicator;

    @Bind(R.id.selected_day_indicator_image)
    protected ImageView mSelectedDayIndicator;

    @Bind(R.id.today_text)
    protected TextView mTodayText;

    private static final String DATE_FORMAT = "MMM E d";

    private boolean isChecked;

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

    public void init(Date date)
    {
        ButterKnife.bind(this);

        mSelectedDayIndicator.setVisibility(View.INVISIBLE);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String[] formattedDate = dateFormat.format(date.getTime()).split(" ");

        //only display month for first day in a month
        if (Integer.parseInt(formattedDate[2]) == 1)
        {
            mMonthText.setText(formattedDate[0]);
        }
        else
        {
            mMonthText.setVisibility(View.INVISIBLE);
        }

        if (DateTimeUtils.isToday(date))
        {
            mTodayText.setVisibility(View.VISIBLE);
            mDayOfMonthText.setVisibility(View.INVISIBLE);
            mDayOfWeekText.setText(formattedDate[1] + " " + formattedDate[2]);
        }
        else
        {
            mDayOfWeekText.setText(formattedDate[1]);
            mDayOfMonthText.setText(formattedDate[2]);
        }
    }

    public void showRequestedIndicator(boolean show)
    {
        mRequestedIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showClaimedIndicator(boolean show)
    {
        mClaimedJobExistsIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private static int[] DRAWABLE_STATE_CHECKED = {android.R.attr.state_checked};

    @Override
    protected int[] onCreateDrawableState(int extraSpace)
    {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
        {
            mergeDrawableStates(drawableState, DRAWABLE_STATE_CHECKED);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked)
    {
        isChecked = checked;
        refreshDrawableState();
        mSelectedDayIndicator.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean isChecked()
    {
        return isChecked;
    }

    @Override
    public void toggle()
    {
    }
}
