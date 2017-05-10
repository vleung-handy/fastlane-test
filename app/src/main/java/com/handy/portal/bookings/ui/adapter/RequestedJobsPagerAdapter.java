package com.handy.portal.bookings.ui.adapter;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.clients.ui.adapter.RequestedJobsRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class RequestedJobsPagerAdapter extends PagerAdapter {
    public static final int POSITION_NOT_FOUND = -1;

    private List<View> mViews;

    public RequestedJobsPagerAdapter(
            final Context context,
            final EventBus bus,
            final List<Booking> requestedJobs
    ) {
        final int xPadding =
                context.getResources().getDimensionPixelSize(R.dimen.default_padding_half);
        mViews = new ArrayList<>();
        for (final Booking requestedJob : requestedJobs) {
            final FrameLayout container = new FrameLayout(context);
            container.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            container.setPadding(xPadding, 0, xPadding, 0);
            new RequestedJobsRecyclerViewAdapter.JobViewHolder(container, bus).init(requestedJob);
            mViews.add(container);
        }
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
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = mViews.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView(mViews.get(position));
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
}
