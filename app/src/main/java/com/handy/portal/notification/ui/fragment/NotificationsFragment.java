package com.handy.portal.notification.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NotificationEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.core.ui.widget.TitleView;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.notification.model.NotificationAction;
import com.handy.portal.notification.model.NotificationMessage;
import com.handy.portal.notification.ui.view.NotificationsListView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class NotificationsFragment extends ActionBarFragment {
    private static final int NUMBER_OF_NOTIFICATIONS_PER_REQUEST = 20;

    @BindView(R.id.notifications_list_view)
    NotificationsListView mNotificationsListView;

    @BindView(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorTextView;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    @BindView(R.id.no_notifications_view)
    SwipeRefreshLayout mNoNotificationsRefreshLayout;

    @Inject
    PageNavigationManager mPageNavigationManager;

    private View mFragmentView;
    private boolean isRequestingNotifications = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_notifications, container, false);
        }

        ButterKnife.bind(this, mFragmentView);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNotifications(true);
            }
        });
        mRefreshLayout.setColorSchemeResources(R.color.handy_blue);

        mNotificationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final NotificationMessage message =
                        mNotificationsListView.getWrappedAdapter().getItem(position);
                if (!message.isInteracted()) {
                    bus.post(new NotificationEvent.RequestMarkNotificationsAsInteracted(
                            Lists.newArrayList(message.getId())));
                }
                triggerMessageActions(message.getActions());
            }
        });

        return mFragmentView;
    }

    private void triggerMessageActions(final List<NotificationAction> actions) {
        if (actions != null && !actions.isEmpty()) {
            if (actions.size() == 1) {
                triggerMessageAction(actions.get(0));
            }
            else {
                showMessageActionsDialog(actions);
            }
        }
    }

    private void showMessageActionsDialog(final List<NotificationAction> actions) {
        final List<String> actionNames = new ArrayList<>(actions.size());
        for (final NotificationAction action : actions) {
            actionNames.add(action.getText());
        }
        final TitleView titleView = new TitleView(getActivity());
        titleView.setText(R.string.select_action);
        new AlertDialog.Builder(getActivity())
                .setCustomTitle(titleView)
                .setAdapter(new ArrayAdapter<>(getActivity(), R.layout.view_selection_text,
                                actionNames),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                triggerMessageAction(actions.get(which));
                            }
                        })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void triggerMessageAction(final NotificationAction notificationAction) {
        final String deeplinkUriString = notificationAction.getDeeplink();
        if (deeplinkUriString != null) {
            mPageNavigationManager.handleDeeplinkUrl(DeeplinkLog.Source.NOTIFICATION_FEED, deeplinkUriString);
        }
    }

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.NOTIFICATIONS;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.tab_notifications, false);

        bus.register(this);

        if (mNotificationsListView.shouldRequestMoreNotifications()) {
            setLoadingOverlayVisible(true);
            requestNotifications(false);
        }
        else {
            setLoadingOverlayVisible(false);
        }

        bus.post(new NotificationEvent.ReceiveUnreadCountSuccess(0));
    }

    @Override
    public void onPause() {
        setLoadingOverlayVisible(false); //don't want overlay to persist when this fragment is paused
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.try_again_button)
    public void doInitialRequestAgain() {
        setLoadingOverlayVisible(true);
        requestNotifications(true);
    }

    @Subscribe
    public void onReceiveNotificationMessagesSuccess(NotificationEvent.ReceiveNotificationMessagesSuccess event) {
        NotificationMessage[] notificationMessages = event.getNotificationMessages();
        boolean isFirstRequest = mNotificationsListView.isEmpty();
        if (isLoadingInitialNotifications(notificationMessages)) {
            mNotificationsListView.reset();
        }
        mNotificationsListView.appendData(notificationMessages);
        cleanUpView();
        markUnreadNotificationsAsRead(notificationMessages);
        markActionlessUninteractedNotificationsAsInteracted(notificationMessages);

        if (isFirstRequest && mNotificationsListView.isEmpty()) {
            mNoNotificationsRefreshLayout.setVisibility(View.VISIBLE);
            mNoNotificationsRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestNotifications(true);
                }
            });
            mNoNotificationsRefreshLayout.setColorSchemeResources(R.color.handy_blue);
            mRefreshLayout.setVisibility(View.GONE);
        }
        else {
            mNoNotificationsRefreshLayout.setVisibility(View.GONE);
            mRefreshLayout.setVisibility(View.VISIBLE);

            if (mNotificationsListView.shouldRequestMoreNotifications()) {
                mNotificationsListView.setFooterVisible(false);
                if (isFirstRequest) {
                    setNotificationsListViewOnScrollListener();
                }
            }
            else {
                mNotificationsListView.setFooterText(R.string.no_more_notifications);
            }
        }
    }

    @Subscribe
    public void onReceiveNotificationMessagesError(NotificationEvent.ReceiveNotificationMessagesError event) {
        cleanUpView();
        if (mNotificationsListView.isEmpty()) {
            mFetchErrorView.setVisibility(View.VISIBLE);
            mFetchErrorTextView.setText(R.string.error_loading_notifications);
        }
        else {
            mNotificationsListView.stopRequestingNotifications();
            mNotificationsListView.showFooter(R.string.error_loading_notifications);
        }
    }

    public void setLoadingOverlayVisible(boolean visible) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(visible));
    }

    private void markUnreadNotificationsAsRead(final NotificationMessage[] notificationMessages) {
        ArrayList<Integer> unreadNotifications = new ArrayList<>();
        for (NotificationMessage notificationMessage : notificationMessages) {
            if (!notificationMessage.isRead()) {
                unreadNotifications.add(notificationMessage.getId());
            }
        }

        if (!unreadNotifications.isEmpty()) {
            bus.post(new NotificationEvent.RequestMarkNotificationsAsRead(unreadNotifications));
        }
    }

    private void markActionlessUninteractedNotificationsAsInteracted(
            final NotificationMessage[] notificationMessages) {
        ArrayList<Integer> notificationIds = new ArrayList<>();
        for (final NotificationMessage notificationMessage : notificationMessages) {
            final List<NotificationAction> actions = notificationMessage.getActions();
            if (!notificationMessage.isInteracted() && actions != null && actions.isEmpty()) {
                notificationIds.add(notificationMessage.getId());
            }
        }

        if (!notificationIds.isEmpty()) {
            bus.post(new NotificationEvent.RequestMarkNotificationsAsInteracted(notificationIds));
        }
    }

    private void setNotificationsListViewOnScrollListener() {
        mNotificationsListView.setOnScrollToBottomListener(new InfiniteScrollListView.OnScrollToBottomListener() {
            @Override
            public void onScrollToBottom() {
                if (mNotificationsListView != null) {
                    requestNotifications(false);
                }
            }
        });
    }

    private boolean isLoadingInitialNotifications(final NotificationMessage[] notificationMessages) {
        Integer firstNotificationId = mNotificationsListView.getFirstNotificationId();
        return (firstNotificationId == null || notificationMessages[0].getId() >= firstNotificationId);
    }

    private void requestNotifications(boolean refresh) {
        if ((mNotificationsListView.shouldRequestMoreNotifications() || refresh) && !isRequestingNotifications) {
            isRequestingNotifications = true;
            Integer untilId = refresh ? null : mNotificationsListView.getLastNotificationId();
            bus.post(new NotificationEvent.RequestNotificationMessages(null, untilId, NUMBER_OF_NOTIFICATIONS_PER_REQUEST));
            mNotificationsListView.showFooter(R.string.load_notifications);
        }
    }

    private void cleanUpView() {
        mFetchErrorView.setVisibility(View.GONE);
        setLoadingOverlayVisible(false);
        mRefreshLayout.setRefreshing(false);
        mNoNotificationsRefreshLayout.setRefreshing(false);
        isRequestingNotifications = false;
    }
}
