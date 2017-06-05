package com.handy.portal.availability.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.availability.model.Availability;
import com.handy.portal.availability.view.AvailableHoursWithDateView;
import com.handy.portal.availability.view.WeeklyAvailableHoursView;
import com.handy.portal.library.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeeklyPagerAdapter extends PagerAdapter {
    public static final int POSITION_NOT_FOUND = -1;
    private List<WeeklyAvailableHoursView> mViews;

    public WeeklyPagerAdapter(final Context context,
                              final Availability.Range currentWeekRange,
                              final Availability.Range nextWeekRange,
                              final WeeklyAvailableHoursView.CellClickListener cellClickListener) {
        mViews = new ArrayList<>();
        mViews.add(new WeeklyAvailableHoursView(
                context, getViewModelsFromWeekRange(currentWeekRange), cellClickListener
        ));
        mViews.add(new WeeklyAvailableHoursView(
                context, getViewModelsFromWeekRange(nextWeekRange), cellClickListener
        ));
    }

    private List<AvailableHoursViewModel> getViewModelsFromWeekRange(
            final Availability.Range weekRange
    ) {
        final List<AvailableHoursViewModel> viewModels = new ArrayList<>();
        for (final Date date : weekRange.dates()) {
            viewModels.add(getAvailableHoursViewModel(date, null));
        }
        return viewModels;
    }

    @NonNull
    private AvailableHoursViewModel getAvailableHoursViewModel(
            final Date date,
            final @Nullable Availability.AdhocTimeline timeline
    ) {
        return new AvailableHoursViewModel(
                DateTimeUtils.formatDateShortDayOfWeekShortMonthDay(date),
                timeline != null ? timeline.getIntervals() : null,
                !DateTimeUtils.isDaysPast(date),
                date
        );
    }

    public void updateViewWithTimeline(
            @NonNull final Date date,
            @Nullable final Availability.AdhocTimeline timeline
    ) {
        for (WeeklyAvailableHoursView weekView : mViews) {
            final AvailableHoursWithDateView view = weekView.getViewWithIdentifier(date);
            if (view != null) {
                view.update(getAvailableHoursViewModel(date, timeline));
            }
        }
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

    private View getItemAt(final int position) {
        return mViews.get(position);
    }
}
