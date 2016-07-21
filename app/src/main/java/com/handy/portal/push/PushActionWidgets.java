package com.handy.portal.push;

import com.handy.portal.R;
import com.urbanairship.push.notifications.NotificationActionButton;
import com.urbanairship.push.notifications.NotificationActionButtonGroup;

public class PushActionWidgets
{
    public static NotificationActionButtonGroup createContactActionButtonGroup()
    {
        NotificationActionButton contactCallButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_CALL)
                        .setLabel(R.string.call)
                        .setIcon(R.drawable.ic_phone)
                        .build();

        NotificationActionButton contactTextButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_TEXT)
                        .setLabel(R.string.text)
                        .setIcon(R.drawable.ic_arrow_solid)
                        .build();

        return new NotificationActionButtonGroup.Builder()
                .addNotificationActionButton(contactCallButton)
                .addNotificationActionButton(contactTextButton)
                .build();
    }

    public static NotificationActionButtonGroup createOnMyWayActionButtonGroup()
    {
        NotificationActionButton contactCallButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_CALL)
                        .setLabel(R.string.call)
                        .setIcon(R.drawable.ic_phone)
                        .setPerformsInForeground(true)
                        .build();

        NotificationActionButton contactTextButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_TEXT)
                        .setLabel(R.string.text)
                        .setIcon(R.drawable.ic_arrow_solid)
                        .setPerformsInForeground(true)
                        .build();

        NotificationActionButton onMyWayButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_GROUP_OMW)
                        .setLabel(R.string.on_my_way)
                        .setIcon(R.drawable.ic_arrow_solid)
                        .build();

        return new NotificationActionButtonGroup.Builder()
                .addNotificationActionButton(onMyWayButton)
                .addNotificationActionButton(contactCallButton)
                .addNotificationActionButton(contactTextButton)
                .build();
    }
}
