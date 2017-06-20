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

public class DatesPagerAdapter extends PagerAdapter {
    public static final int POSITION_NOT_FOUND = -1;
    private static final int DAYS_IN_A_WEEK = 7;
    private final List<NewDateButtonGroup> mViews;
    private final DateSelectedListener mDateSelectedListener;
    private final DateSelectedListener mDateSelectedListenerWrapper = new DateSelectedListener() {
        @Override
        public void onDateSelected(final Date date) {
            final NewDateButtonGroup selectedGroup = getDateButtonGroupForDate(date);
            for (final NewDateButtonGroup group : mViews) {
                if (!group.equals(selectedGroup)) {
                    group.clearSelection();
                }
            }
            mDateSelectedListener.onDateSelected(date);
        }
    };

    public DatesPagerAdapter(
            final Context context,
            final int numberOfDaysToEnable,
            final DateSelectedListener dateSelectedListener
    ) {
        mDateSelectedListener = dateSelectedListener;
        mViews = new ArrayList<>();
        final Calendar calendar = Calendar.getInstance(Locale.US);
        DateTimeUtils.convertToMidnight(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        for (int i = 0; i < getNumberOfWeeksToDisplay(numberOfDaysToEnable); i++) {
            final List<Date> dates = new ArrayList<>(DAYS_IN_A_WEEK);
            for (int j = 0; j < DAYS_IN_A_WEEK; j++) {
                dates.add(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
            mViews.add(new NewDateButtonGroup(
                    context,
                    dates,
                    numberOfDaysToEnable,
                    mDateSelectedListenerWrapper
            ));
        }
    }

    private int getNumberOfWeeksToDisplay(final int numberOfDaysToEnable) {
        // The following code might be confusing to you, but let me explain what it does.
        // The purpose of this method is to determine how many weeks we need to display given the
        // number of enabled days, N. Assuming all enabled days are shown in the dates pager and
        // that N is inclusive and contiguous (e.g. N=2 means today and tomorrow are enabled), we
        // take the sum of the number of past days in the current week and N, and divide the result
        // by 7 (which is the number of days in a week).
        //
        // For example, on a Wednesday and N=5, the result will be 1.1428571429. If we take the
        // ceil of that, the result will be 2 weeks which makes sense because we want to display 5
        // enabled days starting from the current day. In order to do this, we would have display
        // 2 weeks, show 4 out of 5 enabled days (Wednesday to Saturday) on the first week, and 1
        // out of 5 enabled days (Sunday) on the second week, with the rest of the days disabled.

        final Calendar today = Calendar.getInstance(Locale.US);
        today.setTime(new Date());
        final int numberOfPastDaysInCurrentWeek = today.get(Calendar.DAY_OF_WEEK) - 1;
        return ((Double) Math.ceil((double)
                (numberOfPastDaysInCurrentWeek + numberOfDaysToEnable) / DAYS_IN_A_WEEK
        )).intValue();
    }

    @Override
    public int getItemPosition(final Object object) {
        int index = mViews.indexOf(object);
        if (index == POSITION_NOT_FOUND) {
            return POSITION_NONE;
        }
        else {
            return index;
        }
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = getItemAt(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(getItemAt(position));
    }

    public void showClaimIndicatorForDate(final Date date) {
        final NewDateButton dateButton = getDateButtonForDate(date);
        if (dateButton != null) {
            dateButton.showClaimIndicator();
        }
    }

    public NewDateButton getDateButtonForDate(final Date date) {
        for (final NewDateButtonGroup view : mViews) {
            final NewDateButton dateButton = view.getDateButtonForDate(date);
            if (dateButton != null) {
                return dateButton;
            }
        }
        return null;
    }

    public NewDateButtonGroup getDateButtonGroupForDate(final Date date) {
        for (final NewDateButtonGroup view : mViews) {
            final NewDateButton dateButton = view.getDateButtonForDate(date);
            if (dateButton != null) {
                return view;
            }
        }
        return null;
    }

    public int getItemPositionWithDate(final Date date) {
        final NewDateButtonGroup group = getDateButtonGroupForDate(date);
        if (group != null) {
            return getItemPosition(group);
        }
        return POSITION_NOT_FOUND;
    }

    public NewDateButtonGroup getItemAt(final int position) {
        return mViews.get(position);
    }

    public interface DateSelectedListener {
        void onDateSelected(Date date);
    }
}
