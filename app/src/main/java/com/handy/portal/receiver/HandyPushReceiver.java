package com.handy.portal.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.service.AutoCheckInService;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

public class HandyPushReceiver extends BaseIntentReceiver
{
    public static final String TYPE_AUTO_CHECK_IN = "P_AUTO_CHECKIN";

    @Override
    protected void onChannelRegistrationSucceeded(Context context, String s)
    {
    }

    @Override
    protected void onChannelRegistrationFailed(Context context)
    {
    }

    @Override
    protected void onPushReceived(Context context, PushMessage pushMessage, int notificationId)
    {
    }

    @Override
    protected void onBackgroundPushReceived(Context context, PushMessage pushMessage)
    {
        Bundle pushBundle = pushMessage.getPushBundle();
        String type = pushBundle.getString(BundleKeys.PUSH_TYPE, "");
        switch (type)
        {
            case TYPE_AUTO_CHECK_IN:
                Intent autoCheckInServiceIntent = new Intent(context, AutoCheckInService.class);
                autoCheckInServiceIntent.putExtras(pushBundle);
                context.startService(autoCheckInServiceIntent);
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean onNotificationOpened(Context context, PushMessage pushMessage, int notificationId)
    {
        return false;
    }

    @Override
    protected boolean onNotificationActionOpened(Context context, PushMessage pushMessage, int notificationId, String buttonId, boolean isForeground)
    {
        return false;
    }
}
