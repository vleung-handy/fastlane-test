package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.ui.adapter.UpdatePaymentsFragmentPagerAdapter;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdatePaymentFragment extends ActionBarFragment
{
    @InjectView(R.id.payments_update_bank_info_sliding_tabs)
    TabLayout tabLayout;

    @InjectView(R.id.payments_update_bank_info_viewpager)
    ViewPager viewPager;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PAYMENTS;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setOptionsMenuEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_payments_add_method, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewPager.setAdapter(new UpdatePaymentsFragmentPagerAdapter(getChildFragmentManager(), this.getContext()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected List<String> requiredArguments()
    {
        return Lists.newArrayList(BundleKeys.BOOKING_ID, BundleKeys.BOOKING_TYPE, BundleKeys.BOOKING_DATE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.update_payment_method, false);
    }

}
