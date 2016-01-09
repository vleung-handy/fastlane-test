package com.handy.portal.ui.element.notifications;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationMessage;
import com.handy.portal.ui.adapter.NotificationsListAdapter;
import com.handy.portal.ui.widget.InfiniteScrollListView;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public final class NotificationsListView extends InfiniteScrollListView
{
    @Inject
    Bus mBus;

    private TextView mFooterView;

    public NotificationsListView(final Context context)
    {
        super(context);
        Utils.inject(context, this);

    }

    public NotificationsListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        Utils.inject(context, this);
    }

    public NotificationsListView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
        Utils.inject(context, this);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        NotificationsListAdapter notificationsListAdapter = new NotificationsListAdapter(getContext());

        mFooterView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.element_payments_batch_list_footer, null);
        addFooterView(mFooterView, null, false);
        setAdapter(notificationsListAdapter);
        // Override the StickyListHeaderView not setting these correctly
        InsetDrawable divider = (InsetDrawable) this.getResources().getDrawable(R.drawable.left_inset_divider);
        getWrappedList().setDivider(divider);
        getWrappedList().setDividerHeight(1);
    }

    public void showFooter(int stringResourceId)
    {
        setFooterVisible(true);
        setFooterText(stringResourceId);
    }

    public void setFooterText(int resourceId)
    {
        mFooterView.setText(resourceId);
    }

    public void setFooterVisible(boolean visible)
    {
        mFooterView.setVisibility(visible ? VISIBLE : GONE);
    }

    public NotificationsListAdapter getWrappedAdapter()
    {
        return (NotificationsListAdapter) getAdapter();
    }

    public boolean shouldRequestMoreNotifications()
    {
        return getWrappedAdapter().shouldRequestMoreNotifications();
    }

    public void appendData(NotificationMessage[] notificationMessages)
    {
        getWrappedAdapter().appendData(notificationMessages);
    }

    public void markNotificationsAsRead(final NotificationMessage[] notificationMessages)
    {
        for (NotificationMessage notificationMessage : notificationMessages)
        {
            getWrappedAdapter().markNotificationAsRead(notificationMessage);
        }
    }

    public boolean isEmpty()
    {
        return getWrappedAdapter().isEmpty();
    }

    public Integer getLastNotificationId()
    {
        return getWrappedAdapter().getLastNotificationId();
    }
}
