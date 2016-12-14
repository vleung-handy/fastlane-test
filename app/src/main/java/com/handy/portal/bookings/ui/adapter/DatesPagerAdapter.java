package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.ui.element.NewDateButtonGroup;
import com.handy.portal.bookings.ui.element.NewDateButtonView;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatesPagerAdapter extends PagerAdapter
{
    private static final int WEEKS_TOTAL = 4;
    private static final int DAYS_IN_A_WEEK = 7;
    private final List<NewDateButtonGroup> mViews;

    public DatesPagerAdapter(final Context context, final DateSelectedListener dateSelectedListener)
    {
        mViews = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance();
        DateTimeUtils.convertToMidnight(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < WEEKS_TOTAL; i++)
        {
            final List<Date> dates = new ArrayList<>(DAYS_IN_A_WEEK);
            for (int j = 0; j < DAYS_IN_A_WEEK; j++)
            {
                dates.add(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
            mViews.add(new NewDateButtonGroup(context, dates, dateSelectedListener));
        }
    }

    @Override
    public int getItemPosition(final Object object)
    {
        int index = mViews.indexOf(object);
        if (index == -1)
        {
            return POSITION_NONE;
        }
        else
        {
            return index;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        final View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount()
    {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView(mViews.get(position));
    }

    public void showClaimIndicatorForDate(final Date date)
    {
        final NewDateButtonView dateButton = getDateButtonForDate(date);
        if (dateButton != null)
        {
            dateButton.showClaimIndicator();
        }
    }

    public NewDateButtonView getDateButtonForDate(final Date date)
    {
        for (final NewDateButtonGroup view : mViews)
        {
            final NewDateButtonView dateButton = view.getDateButtonForDate(date);
            if (dateButton != null)
            {
                return dateButton;
            }
        }
        return null;
    }

    public NewDateButtonGroup getDateButtonGroupForDate(final Date date)
    {
        for (final NewDateButtonGroup view : mViews)
        {
            final NewDateButtonView dateButton = view.getDateButtonForDate(date);
            if (dateButton != null)
            {
                return view;
            }
        }
        return null;
    }

    public int getItemPositionWithDate(final Date date)
    {
        final NewDateButtonGroup group = getDateButtonGroupForDate(date);
        if (group != null)
        {
            return getItemPosition(group);
        }
        return -1;
    }

    public interface DateSelectedListener
    {
        void onDateSelected(Date date);
    }
}
