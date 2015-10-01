package com.handy.portal.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.handy.portal.ui.fragment.payments.PaymentsUpdateBankInfoFragment;
import com.handy.portal.ui.fragment.payments.PaymentsUpdateDebitCardFragment;

//TODO: work in progress. need to refactor
public class UpdatePaymentsFragmentPagerAdapter extends FragmentPagerAdapter //TODO: rename this
{
    private String tabTitles[] = new String[] {"Bank Account", "Debit Card"}; //TODO: reorganize this/put in better place/use strings.xml
    private Context context;

    public UpdatePaymentsFragmentPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch(position) //TODO: reorganize/put in better place?
        {
            case 0:
                return new PaymentsUpdateBankInfoFragment();
            case 1:
                return new PaymentsUpdateDebitCardFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return tabTitles[position];
    }

    @Override
    public int getCount()
    {
        return tabTitles.length;
    }
}
