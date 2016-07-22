package com.handy.portal.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.handy.portal.library.util.Utils;
import com.handy.portal.manager.HandyConnectivityManager;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConnectivityReceiver extends BaseIntentReceiver
{
    @Inject
    EventBus mBus;

    @Inject
    HandyConnectivityManager mHandyConnectivityManager;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        Utils.inject(context, this);

        System.out.println("Connectivity receiver has received something");

//        final String action = intent.getAction();
//        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION))
//        {
//            if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false))
//            {
//                //do stuff
//            }
//            else
//            {
//                // wifi connection was lost
//            }
//        }

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        mHandyConnectivityManager.requestRefreshConnectivityStatus(context);

        super.onReceive(context, intent);
    }

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
    protected boolean onNotificationOpened(@NonNull Context context,
                                           @NonNull PushMessage pushMessage,
                                           int notificationId)
    {
        return false;
    }

    @Override
    protected void onBackgroundPushReceived(@NonNull Context context,
                                            @NonNull PushMessage pushMessage)
    { }

    @Override
    protected boolean onNotificationActionOpened(@NonNull Context context,
                                                 @NonNull PushMessage pushMessage,
                                                 int notificationId,
                                                 @NonNull String buttonId,
                                                 boolean isForeground)
    { return false; }
}
