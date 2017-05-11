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

import java.util.List;

public class RequestedJobsPagerAdapter extends PagerAdapter {
    public static final int POSITION_NOT_FOUND = -1;
    private final Context mContext;
    private final EventBus mBus;
    private final List<Booking> mRequestedJobs;

    public RequestedJobsPagerAdapter(
            final Context context,
            final EventBus bus,
            final List<Booking> requestedJobs
    ) {
        mContext = context;
        mBus = bus;
        mRequestedJobs = requestedJobs;
    }

    @Override
    public int getCount() {
        return mRequestedJobs.size();
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.getTag() == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final int xPadding =
                mContext.getResources().getDimensionPixelSize(R.dimen.default_padding_half);
        final Booking requestedJob = mRequestedJobs.get(position);
        final FrameLayout frame = new FrameLayout(mContext);
        frame.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        frame.setPadding(xPadding, 0, xPadding, 0);
        frame.setTag(requestedJob);
        new RequestedJobsRecyclerViewAdapter.JobViewHolder(frame, mBus).init(requestedJob);
        container.addView(frame);
        return requestedJob;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        for (int i = 0; i < container.getChildCount(); i++) {
            final View view = container.getChildAt(i);
            if (isViewFromObject(view, object)) {
                container.removeView(view);
                return;
            }
        }
    }

    @Override
    public int getItemPosition(final Object object) {
        int index = mRequestedJobs.indexOf(object);
        if (index == POSITION_NOT_FOUND) {
            return POSITION_NONE;
        }
        else {
            return index;
        }
    }
}
