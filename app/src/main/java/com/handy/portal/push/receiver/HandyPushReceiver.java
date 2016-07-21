package com.handy.portal.push.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.util.Utils;
import com.handy.portal.location.LocationPingService;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.EventLog;
import com.handy.portal.logger.handylogger.model.PushNotificationLog;
import com.handy.portal.model.LocationData;
import com.handy.portal.push.PushActionConstants;
import com.handy.portal.push.PushActionHandler;
import com.handy.portal.ui.activity.SplashActivity;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class HandyPushReceiver extends BaseIntentReceiver
{
    @Inject
    EventBus mBus;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Utils.inject(context, this);
        super.onReceive(context, intent);
    }

    public static final String TYPE_LOCATION_PING = "P_LOCATION_PING";

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

        final EventLog eventLog = new PushNotificationLog.Received(pushMessage);
        mBus.post(new LogEvent.AddLogEvent(eventLog));
        final Bundle pushBundle = pushMessage.getPushBundle();
        final String type = pushBundle.getString(BundleKeys.HANDY_PUSH_TYPE, "");
        switch (type)
        {
            case TYPE_LOCATION_PING:
                final Intent intent = new Intent(context, LocationPingService.class);
                final String eventName = pushBundle.getString(BundleKeys.EVENT_NAME);
                intent.putExtra(BundleKeys.EVENT_NAME, eventName);
                context.startService(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onBackgroundPushReceived(@NonNull Context context,
                                            @NonNull PushMessage pushMessage)
    { }

    @Override
    protected boolean onNotificationOpened(@NonNull Context context,
                                           @NonNull PushMessage pushMessage,
                                           int notificationId)
    {
        final EventLog eventLog = new PushNotificationLog.Opened(pushMessage);
        mBus.post(new LogEvent.AddLogEvent(eventLog));

        final Bundle pushBundle = pushMessage.getPushBundle();
        final String deeplink = pushBundle.getString(BundleKeys.DEEPLINK);
        if (deeplink != null)
        {
            // BaseActivity will preserve the bundle through activity launches
            launchSplashActivityWithDeeplinkData(context, pushBundle);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected boolean onNotificationActionOpened(final Context context,
                                                 final PushMessage pushMessage,
                                                 final int i,
                                                 final String action,
                                                 final boolean b)
    {
        boolean handled = false;
        if(action != null)
        {
            final Bundle pushBundle = pushMessage.getPushBundle();
            handled = isValidUser(pushBundle) &&
                    PushActionHandler.handleAction(context, action, pushMessage.getPushBundle());
            if(!handled)//fixme super hacky
            {
                switch(action)
                {
                    case PushActionConstants.ACTION_GROUP_OMW:
                        handleOnMyWayAction(context, pushMessage.getPushBundle());
                        handled = true;
                        break;
                }

            }
        }
        return handled;

    }

    private boolean handleOnMyWayAction(final Context context, final Bundle arguments)
    {
        final String bookingId = arguments.getString("booking_id");
        mBus.post(new HandyEvent.RequestNotifyJobOnMyWay(
                bookingId, new LocationData(null)));

        Toast.makeText(context, "Notified on my way!", Toast.LENGTH_SHORT).show();
        //todo detect success and show confirmation toast
        //todo remove the sticky notification

        return true;
    }

    private boolean isValidUser(@NonNull final Bundle pushBundle)
    {
        // FIXME
        return true;
    }

    @Override
    protected void onNotificationDismissed(
            @NonNull final Context context,
            @NonNull final PushMessage pushMessage,
            final int notificationId)
    {
        final EventLog eventLog = new PushNotificationLog.Dismissed(pushMessage);
        mBus.post(new LogEvent.AddLogEvent(eventLog));
    }

    private static void launchSplashActivityWithDeeplinkData(@NonNull final Context context,
                                                             @NonNull final Bundle deeplinkData)
    {
        final Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkData);
        intent.putExtra(BundleKeys.DEEPLINK_SOURCE, DeeplinkLog.Source.PUSH_NOTIFICATION);
        context.startActivity(intent);
    }
}
