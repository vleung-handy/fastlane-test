package com.handy.portal.bookings.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.ui.element.NewDateButton;
import com.handy.portal.bookings.ui.element.NewDateButtonGroup;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatesPagerAdapter extends PagerAdapter
{
    public static final int POSITION_NOT_FOUND = -1;
    private static final int WEEKS_TOTAL = 4;
    private static final int DAYS_IN_A_WEEK = 7;
    private final List<NewDateButtonGroup> mViews;
    private final DateSelectedListener mDateSelectedListener;
    private final DateSelectedListener mDateSelectedListenerWrapper = new DateSelectedListener()
    {
        @Override
        public void onDateSelected(final Date date)
        {
            final NewDateButtonGroup selectedGroup = getDateButtonGroupForDate(date);
            for (final NewDateButtonGroup group : mViews)
            {
                if (!group.equals(selectedGroup))
                {
                    group.clearSelection();
                }
            }
            mDateSelectedListener.onDateSelected(date);
        }
    };

    public DatesPagerAdapter(final Context context, final DateSelectedListener dateSelectedListener)
    {
        mDateSelectedListener = dateSelectedListener;
        mViews = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance(Locale.US);
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
            mViews.add(new NewDateButtonGroup(context, dates, mDateSelectedListenerWrapper));
        }
    }

    @Override
    public int getItemPosition(final Object object)
    {
        int index = mViews.indexOf(object);
        if (index == POSITION_NOT_FOUND)
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
        final View view = getItemAt(position);
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
        container.removeView(getItemAt(position));
    }

    public void showClaimIndicatorForDate(final Date date)
    {
        final NewDateButton dateButton = getDateButtonForDate(date);
        if (dateButton != null)
        {
            dateButton.showClaimIndicator();
        }
    }

    public NewDateButton getDateButtonForDate(final Date date)
    {
        for (final NewDateButtonGroup view : mViews)
        {
            final NewDateButton dateButton = view.getDateButtonForDate(date);
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
            final NewDateButton dateButton = view.getDateButtonForDate(date);
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
        return POSITION_NOT_FOUND;
    }

    public NewDateButtonGroup getItemAt(final int position)
    {
        return mViews.get(position);
    }

    public interface DateSelectedListener
    {
        void onDateSelected(Date date);
    }
}
