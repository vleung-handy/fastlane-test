package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.ui.element.dashboard.DashboardRegionTierView;

public class DashboardTiersPagerAdapter extends PagerAdapter
{

    private int mPageCount;
    private Context mContext;
    private ProviderPersonalInfo mProviderPersonalInfo;

    public DashboardTiersPagerAdapter(final Context context, int count,
                                      ProviderPersonalInfo providerPersonalInfo)
    {
        mContext = context;
//        mPageCount = count;
        mPageCount = 2;
        mProviderPersonalInfo = providerPersonalInfo;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position)
    {
        //TODO: remove all hardcoding

        DashboardRegionTierView view = new DashboardRegionTierView(mContext);
//        view.setRegion(mProviderPersonalInfo.getOperatingRegion());
        if (position == 0)
        {
            view.setRegion("New York");
            view.setTiersInfo("1 \u2013 3", "$16", "4 \u2013 6", "$17", "7+", "$18");
            view.setTier(0);
        }
        else
        {
            view.setRegion("Atlanta");
            view.setTiersInfo("1 \u2013 3", "$15", "4 \u2013 6", "$16", "7+", "$17");
            view.setTier(0);
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount()
    {
        return mPageCount;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object)
    {
        container.removeView((View) object);
    }
}
