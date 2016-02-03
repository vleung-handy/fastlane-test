package com.handy.portal.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.service.AutoCheckInService;
import com.handy.portal.util.PushUtils;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

public class HandyPushReceiver extends BaseIntentReceiver
{
    public static final String TYPE_AUTO_CHECK_IN = "P_AUTO_CHECKIN";

    @Override
    protected void onChannelRegistrationSucceeded(@NonNull Context context,
                                                  @NonNull String s)
    {
    }

    @Override
    protected void onChannelRegistrationFailed(@NonNull Context context)
    {
    }

    @Override
    protected void onPushReceived(@NonNull Context context,
                                  @NonNull PushMessage pushMessage,
                                  int notificationId)
    {
    }

    @Override
    protected void onBackgroundPushReceived(@NonNull Context context,
                                            @NonNull PushMessage pushMessage)
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
    protected boolean onNotificationOpened(@NonNull Context context,
                                           @NonNull PushMessage pushMessage,
                                           int notificationId)
    {

        final Bundle pushBundle = pushMessage.getPushBundle();
        return PushUtils.handleDeeplink(context, pushBundle);
    }

    @Override
    protected boolean onNotificationActionOpened(@NonNull Context context,
                                                 @NonNull PushMessage pushMessage,
                                                 int notificationId,
                                                 @NonNull String buttonId,
                                                 boolean isForeground)
    {
        return false;
    }
}
