package com.handy.portal.clients.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.view.HandyTabLayout;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientsFragment extends ActionBarFragment
{
    @BindView(R.id.clients_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.clients_pager)
    ViewPager mViewPager;
    @BindView(R.id.clients_tab_layout)
    HandyTabLayout mTabLayout;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.CLIENTS;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_clients, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        final ConfigurationResponse configuration = configManager.getConfigurationResponse();
        final boolean shouldShowMessagesTab = configuration != null
                && configuration.isClientsChatEnabled();

        if (shouldShowMessagesTab)
        {
            setActionBarVisible(false);
            mToolbar.setTitle(R.string.your_clients);
        }
        else
        {
            setActionBarTitle(R.string.your_clients);
            mTabLayout.setVisibility(View.GONE);
            mToolbar.setVisibility(View.GONE);
        }

        final TabAdapter tabAdapter = new TabAdapter(getActivity(), getChildFragmentManager(),
                shouldShowMessagesTab);
        mViewPager.setAdapter(tabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(tabAdapter);
    }

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private List<InjectedFragment> mFragments = new ArrayList<>();
        private List<String> mTitles = new ArrayList<>();

        public TabAdapter(final Context context,
                          final FragmentManager fragmentManager,
                          final boolean shouldShowMessagesTab)
        {
            super(fragmentManager);
            mTitles.add(context.getResources().getString(R.string.job_requests));
            mFragments.add(ProRequestedJobsFragment.newInstance());
            if (shouldShowMessagesTab)
            {
                mTitles.add(context.getResources().getString(R.string.messages));
                mFragments.add(ClientConversationsFragment.newInstance());
            }
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mTitles.get(position);
        }

        @Override
        public InjectedFragment getItem(int position)
        {
            return mFragments.get(position);
        }
    }
}
