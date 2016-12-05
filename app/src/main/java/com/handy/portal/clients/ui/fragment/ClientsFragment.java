package com.handy.portal.clients.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.view.TabWithCountView;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClientsFragment extends ActionBarFragment
        implements LayerHelper.UnreadConversationsCountChangedListener
{
    public static final String CONVERSATIONS_DEEPLINK_SUFFIX = "conversations";
    @Inject
    BookingManager mBookingManager;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    EventBus mBus;

    @BindView(R.id.clients_pager)
    ViewPager mViewPager;
    @BindView(R.id.clients_tab_layout)
    TabLayout mTabLayout;

    private boolean mShouldShowMessagesTab;
    private TabWithCountView mRequestsTab;
    private TabWithCountView mMessagesTab;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.CLIENTS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final ConfigurationResponse configuration = configManager.getConfigurationResponse();
        mShouldShowMessagesTab = configuration != null && configuration.isChatEnabled();
        mBus.register(this);
        if (mShouldShowMessagesTab)
        {
            mLayerHelper.registerUnreadConversationsCountChangedListener(this);
        }
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
        setActionBarTitle(R.string.your_clients);
        final TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
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

        if (mShouldShowMessagesTab)
        {
            mTabLayout.setVisibility(View.VISIBLE);
            mMessagesTab = new TabWithCountView(getActivity());
            mMessagesTab.setTitle(R.string.messages);
            mTabLayout.getTabAt(1).setCustomView(mMessagesTab);

            final Bundle deeplinkData =
                    getActivity().getIntent().getBundleExtra(BundleKeys.DEEPLINK_DATA);
            if (deeplinkData != null)
            {
                final String deeplink = deeplinkData.getString(BundleKeys.DEEPLINK);
                if (!TextUtils.isEmpty(deeplink)
                        && deeplink.endsWith(CONVERSATIONS_DEEPLINK_SUFFIX))
                {
                    mViewPager.setCurrentItem(1);
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Integer lastUnreadRequestsCount = mBookingManager.getLastUnreadRequestsCount();
        if (lastUnreadRequestsCount != null)
        {
            mRequestsTab.setCount((long) lastUnreadRequestsCount);
        }
        if (mShouldShowMessagesTab)
        {
            mMessagesTab.setCount(mLayerHelper.getUnreadConversationsCount());
        }
    }

    @Subscribe
    public void onReceiveProRequestedJobsCountSuccess(
            final BookingEvent.ReceiveProRequestedJobsCountSuccess event)
    {
        mRequestsTab.setCount((long) event.getCount());
    }

    @Override
    public void onUnreadConversationsCountChanged(final long count)
    {
        mMessagesTab.setCount(count);
    }

    private class TabAdapter extends FragmentPagerAdapter
    {
        private List<InjectedFragment> mFragments = new ArrayList<>();

        public TabAdapter(final FragmentManager fragmentManager)
        {
            super(fragmentManager);
            mFragments.add(ProRequestedJobsFragment.newInstance());
            if (mShouldShowMessagesTab)
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

    @Override
    public void onDestroy()
    {
        if (mShouldShowMessagesTab)
        {
            mLayerHelper.unregisterUnreadConversationsCountChangedListener(this);
        }
        mBus.unregister(this);
        super.onDestroy();
    }
}
