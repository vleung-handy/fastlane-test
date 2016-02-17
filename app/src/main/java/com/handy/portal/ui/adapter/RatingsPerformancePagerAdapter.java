package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.ui.element.dashboard.RatingsProPerformanceView;

public class RatingsPerformancePagerAdapter extends PagerAdapter
{
    private static final int PAGE_COUNT = 2;
    private Context mContext;

    public RatingsPerformancePagerAdapter(final Context context)
    {
        mContext = context;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        RatingsProPerformanceView view = new RatingsProPerformanceView(mContext);
        // TODO: replace the mock data with api call before merge into develop
        if (position == 0)
        {
            view.setTitle(mContext.getString(R.string.past_28_days));
            view.setDate("January 5 - February 5, 2016");
            view.setJobRatings("8", "10", "15");
            container.addView(view);
        }
        else if (position == 1)
        {
            view.setTitle(mContext.getString(R.string.lifetime_rating));
            view.setDate("[Start date] - February 5, 2016");
            view.setDate("[Start date] - February 5, 2016");
            view.setJobRatings("8", "10", "15");
            container.addView(view);
        }
        return view;
    }

    @Override
    public int getCount()
    {
        return PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view == object;
    }
}
