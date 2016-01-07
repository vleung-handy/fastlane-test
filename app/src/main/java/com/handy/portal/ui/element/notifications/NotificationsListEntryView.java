package com.handy.portal.ui.element.notifications;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends LinearLayout
{
//    @Bind()

    public NotificationsListEntryView(Context context) {
        super(context);
    }

    public NotificationsListEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotificationsListEntryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }
}
