package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NotificationEvent;
import com.handy.portal.model.notifications.NotificationMessage;
import com.handy.portal.ui.element.notifications.NotificationsListView;
import com.handy.portal.ui.widget.InfiniteScrollListView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class NotificationsFragment extends ActionBarFragment
{
    private static final int NUMBER_OF_NOTIFICATIONS_PER_REQUEST = 20;

    @Bind(R.id.notifications_list_view)
    NotificationsListView mNotificationsListView;

    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    private View mFragmentView;
    private boolean isRequestingNotifications = false;

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
        isRequestingNotifications = false;
        boolean isFirstRequest = mNotificationsListView.isEmpty();
        NotificationMessage[] notificationMessages = event.getNotificationMessages();
        mNotificationsListView.appendData(notificationMessages);
        mFetchErrorView.setVisibility(View.GONE);
        setLoadingOverlayVisible(false);
        markUnreadNotificationsAsRead(notificationMessages);

        if (mNotificationsListView.shouldRequestMoreNotifications())
        {
            mNotificationsListView.setFooterVisible(false);
            if (isFirstRequest)
            {
                setNotificationsListViewOnScrollListener();
            }
        }
        else
        {
            mNotificationsListView.setFooterText(R.string.no_more_notifications);
        }
    }

    public void setLoadingOverlayVisible(boolean visible)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(visible));
    }

    private void markUnreadNotificationsAsRead(final NotificationMessage[] notificationMessages)
    {
        ArrayList<Integer> unreadNotifications = new ArrayList<>();
        for (NotificationMessage notificationMessage : notificationMessages)
        {
            if (!notificationMessage.isRead())
            {
                unreadNotifications.add(notificationMessage.getId());
            }
        }

        if (!unreadNotifications.isEmpty())
        {
            bus.post(new NotificationEvent.RequestMarkNotificationsAsRead(unreadNotifications));
        }
    }

    private void setNotificationsListViewOnScrollListener()
    {
        mNotificationsListView.setOnScrollToBottomListener(new InfiniteScrollListView.OnScrollToBottomListener()
        {
            @Override
            public void onScrollToBottom()
            {
                if (mNotificationsListView != null)
                {
                    requestNotifications();
                }
            }
        });
    }

    private void requestNotifications()
    {
        if (mNotificationsListView.shouldRequestMoreNotifications() && !isRequestingNotifications)
        {
            isRequestingNotifications = true;
            Integer lastNotificationId = mNotificationsListView.getLastNotificationId();
            bus.post(new NotificationEvent.RequestNotificationMessages(null, lastNotificationId, NUMBER_OF_NOTIFICATIONS_PER_REQUEST));
            mNotificationsListView.showFooter(R.string.load_notifications);
        }
    }
}
