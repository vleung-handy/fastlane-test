package com.handy.portal.clients.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.view.TabWithCountView;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ClientsFragment extends ActionBarFragment {
    @Inject
    BookingManager mBookingManager;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    EventBus mBus;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TabWithCountView mRequestsTab;
    private TabWithCountView mClientsTab;

    private boolean mShowTabs;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.CLIENTS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShowTabs = configManager.getConfigurationResponse().isMyClientsViewEnabled();
        mBus.register(this);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view;
        if(mShowTabs) {
            view = inflater.inflate(R.layout.fragment_clients, container, false);
            mViewPager = (ViewPager) view.findViewById(R.id.clients_pager);
            mTabLayout = (TabLayout) view.findViewById(R.id.clients_tab_layout);
        } else {
            view = inflater.inflate(R.layout.fragment_clients_no_tab, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        if(mShowTabs) {
            final TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
            mViewPager.setAdapter(tabAdapter);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
            mTabLayout.setupWithViewPager(mViewPager);
            initTabViews();
        } else {
            // Add the new tab fragment
            getFragmentManager().beginTransaction()
                    .replace(R.id.clients_no_tab_fragment_container, ProRequestedJobsFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void initTabViews() {
        mRequestsTab = new TabWithCountView(getActivity());
        mRequestsTab.setTitle(R.string.job_requests);
        mTabLayout.getTabAt(0).setCustomView(mRequestsTab);

        mTabLayout.setVisibility(View.VISIBLE);
        mClientsTab = new TabWithCountView(getActivity());
        mClientsTab.setTitle(R.string.messages);
        mTabLayout.getTabAt(1).setCustomView(mClientsTab);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.your_clients, false);
        if(mShowTabs) {
            final Integer lastUnreadRequestsCount = mBookingManager.getLastUnreadRequestsCount();
            if (lastUnreadRequestsCount != null) {
                mRequestsTab.setCount((long) lastUnreadRequestsCount);
            }
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsCountSuccess(
            final BookingEvent.ReceiveProRequestedJobsCountSuccess event) {
        if(mShowTabs)
            mRequestsTab.setCount((long) event.getCount());
    }

    private class TabAdapter extends FragmentPagerAdapter {
        private List<InjectedFragment> mFragments = new ArrayList<>();

        public TabAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
            mFragments.add(ProRequestedJobsFragment.newInstance());
            mFragments.add(ClientConversationsFragment.newInstance());
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public InjectedFragment getItem(int position) {
            return mFragments.get(position);
        }
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        super.onDestroy();
    }
}
