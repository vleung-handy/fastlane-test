package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.handy.portal.ui.fragment.payments.UpdatePaymentFragment;

//TODO: work in progress. need to refactor
public class UpdatePaymentsFragmentPagerAdapter extends FragmentPagerAdapter
{
    private Context context;

    private int numItems = 0;
    public UpdatePaymentsFragmentPagerAdapter(FragmentManager fm, int numItems, Context context)
    {
        super(fm);
        this.numItems = numItems;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        return UpdatePaymentFragment.getItem(position);
    }

    @Override
    public int getCount()
    {
        return numItems;
    }
}
