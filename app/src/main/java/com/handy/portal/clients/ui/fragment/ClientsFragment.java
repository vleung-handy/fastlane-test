package com.handy.portal.clients.ui.fragment;

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
import com.handy.portal.library.ui.view.TabWithCountView;
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
    TabLayout mTabLayout;

    private TabWithCountView mRequestsTab;
    private TabWithCountView mMessagesTab;

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

        final TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager(),
                shouldShowMessagesTab);
        mViewPager.setAdapter(tabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        initTabViews();
    }

    private void initTabViews()
    {
        mRequestsTab = new TabWithCountView(getActivity());
        mRequestsTab.setTitle(R.string.job_requests);
        mTabLayout.getTabAt(0).setCustomView(mRequestsTab);

        mMessagesTab = new TabWithCountView(getActivity());
        mMessagesTab.setTitle(R.string.messages);
        mTabLayout.getTabAt(1).setCustomView(mMessagesTab);
    }

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private List<InjectedFragment> mFragments = new ArrayList<>();

        public TabAdapter(final FragmentManager fragmentManager,
                          final boolean shouldShowMessagesTab)
        {
            super(fragmentManager);
            mFragments.add(ProRequestedJobsFragment.newInstance());
            if (shouldShowMessagesTab)
            {
                mFragments.add(ClientConversationsFragment.newInstance());
            }
        }

        @Override
        public int getCount()
        {
            return mFragments.size();
        }

        @Override
        public InjectedFragment getItem(int position)
        {
            return mFragments.get(position);
        }
    }
}
