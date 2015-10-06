package com.handy.portal.ui.fragment.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.adapter.UpdatePaymentsFragmentPagerAdapter;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdatePaymentFragment extends ActionBarFragment
{
    @InjectView(R.id.payments_update_info_radio_group_container)
    CardView radioGroupContainer;

    @InjectView(R.id.payments_update_method_single_option_header)
    CardView singleOptionHeader;

    @InjectView(R.id.payments_update_method_radio_group)
    RadioGroup radioGroup;

    @InjectView(R.id.payments_update_info_view_pager_wrapper)
    CardView viewPagerWrapper;

    @InjectView(R.id.payments_update_info_viewpager)
    ViewPager viewPager;

    @Inject
    ProviderManager providerManager;

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

        View view = inflater.inflate(R.layout.fragment_payments_update_method, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        createViewFromRecommendedPaymentFlow();

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

    private void createViewFromRecommendedPaymentFlow() //TODO: clean this up
    {
        boolean stripeAndDebit = Provider.RecommendedPaymentFlow.STRIPE_DEBIT.getValue().equalsIgnoreCase(providerManager.getCachedActiveProvider().getRecommendedPaymentFlow());
        //TODO: disable swipe on viewpager

        int numItems;
        if(!stripeAndDebit)
        {
            singleOptionHeader.setVisibility(View.VISIBLE);
            viewPagerWrapper.setVisibility(View.VISIBLE);
            numItems = 1;
        }
        else
        {
            radioGroupContainer.setVisibility(View.VISIBLE);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    viewPagerWrapper.setVisibility(View.VISIBLE);
                    switch (checkedId)
                    {
                        case R.id.payments_update_method_bank_account_radio_button:
                            viewPager.setCurrentItem(0, true);
                            break;
                        case R.id.payments_update_method_debit_card_radio_button:
                            viewPager.setCurrentItem(1, true);
                            break;
                    }
                }
            });
            numItems = 2;
        }
        viewPager.setAdapter(new UpdatePaymentsFragmentPagerAdapter(getChildFragmentManager(), numItems, this.getContext()));

    }

    public static Fragment getItem(int position)
    {
        switch(position)
        {
            case 0:
                return new PaymentsUpdateBankInfoFragment();
            case 1:
                return new PaymentsUpdateDebitCardFragment();
        }
        return null;
    }
}
