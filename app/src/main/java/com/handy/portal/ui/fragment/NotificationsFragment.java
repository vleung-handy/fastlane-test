package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NotificationEvent;
import com.handy.portal.ui.element.notifications.NotificationsListView;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class NotificationsFragment extends ActionBarFragment
{
    @Bind(R.id.notifications_list_view)
    NotificationsListView mNotificationsListView;

    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    private View mFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mFragmentView == null)
        {
            mFragmentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        }
        ButterKnife.bind(this, mFragmentView);
        return mFragmentView;
    }

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.NOTIFICATIONS;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.tab_notifications, false);

        if (mNotificationsListView.shouldRequestMoreNotifications())
        {
            setLoadingOverlayVisible(true);
            requestNotifications();
        }
        else
        {
            setLoadingOverlayVisible(false);
        }
    }

    @Override
    public void onPause()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));//don't want overlay to persist when this fragment is paused
        super.onPause();
    }

    @Subscribe
    public void onReceiveNotificationMessagesSuccess(NotificationEvent.ReceiveNotificationMessagesSuccess event)
    {
        mNotificationsListView.appendData(event.getNotificationMessages());
        mNotificationsListView.setFooterVisible(false);
        mFetchErrorView.setVisibility(View.GONE);
        setLoadingOverlayVisible(false);
    }

    private void requestNotifications()
    {
        // TODO: use untilId; get from last notification in feed
        bus.post(new NotificationEvent.RequestNotificationMessages(null, null, 20));
        mNotificationsListView.showFooter(R.string.loading_more_payments);
    }

    public void setLoadingOverlayVisible(boolean visible)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(visible));
    }
}
