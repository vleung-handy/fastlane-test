package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import butterknife.OnClick;

public final class NotificationsFragment extends ActionBarFragment
{
    private static final int NUMBER_OF_NOTIFICATIONS_PER_REQUEST = 20;

    @Bind(R.id.notifications_list_view)
    NotificationsListView mNotificationsListView;

    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorTextView;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

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

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                requestNotifications(true);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);

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
            requestNotifications(false);
        }
        else
        {
            setLoadingOverlayVisible(false);
        }
    }

    @Override
    public void onPause()
    {
        setLoadingOverlayVisible(false); //don't want overlay to persist when this fragment is paused
        super.onPause();
    }

    @OnClick(R.id.try_again_button)
    public void doInitialRequestAgain()
    {
        setLoadingOverlayVisible(true);
        requestNotifications(true);
    }

    @Subscribe
    public void onReceiveNotificationMessagesSuccess(NotificationEvent.ReceiveNotificationMessagesSuccess event)
    {
        NotificationMessage[] notificationMessages = event.getNotificationMessages();
        boolean isFirstRequest = mNotificationsListView.isEmpty();
        if (isLoadingInitialNotifications(notificationMessages))
        {
            mNotificationsListView.reset();
        }
        mNotificationsListView.appendData(notificationMessages);
        cleanUpView();
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

    @Subscribe
    public void onReceiveNotificationMessagesError(NotificationEvent.ReceiveNotificationMessagesError event)
    {
        cleanUpView();
        if (mNotificationsListView.isEmpty())
        {
            mFetchErrorView.setVisibility(View.VISIBLE);
            mFetchErrorTextView.setText(R.string.error_loading_notifications);
        }
        else
        {
            mNotificationsListView.stopRequestingNotifications();
            mNotificationsListView.showFooter(R.string.error_loading_notifications);
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
                    requestNotifications(false);
                }
            }
        });
    }

    private boolean isLoadingInitialNotifications(final NotificationMessage[] notificationMessages)
    {
        Integer firstNotificationId = mNotificationsListView.getFirstNotificationId();
        return (firstNotificationId == null || notificationMessages[0].getId() >= firstNotificationId);
    }

    private void requestNotifications(boolean refresh)
    {
        if ((mNotificationsListView.shouldRequestMoreNotifications() || refresh) && !isRequestingNotifications)
        {
            isRequestingNotifications = true;
            Integer untilId = refresh ? null : mNotificationsListView.getLastNotificationId();
            bus.post(new NotificationEvent.RequestNotificationMessages(null, untilId, NUMBER_OF_NOTIFICATIONS_PER_REQUEST));
            mNotificationsListView.showFooter(R.string.load_notifications);
        }
    }

    private void cleanUpView()
    {
        mFetchErrorView.setVisibility(View.GONE);
        setLoadingOverlayVisible(false);
        mRefreshLayout.setRefreshing(false);
        isRequestingNotifications = false;
    }
}
