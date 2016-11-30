package com.handy.portal.receiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.handybook.shared.LayerConstants;
import com.handybook.shared.PushNotificationReceiver;
import com.handybook.shared.builtin.MessagesListActivity;
import com.layer.sdk.messaging.Message;

import static com.handybook.shared.LayerConstants.LAYER_CONVERSATION_KEY;
import static com.handybook.shared.LayerConstants.LAYER_MESSAGE_KEY;


public class LayerPushReceiver extends PushNotificationReceiver
{
    private static final String MESSAGES_BACK_NAVIGATION_DEEPLINK =
            "handypro://handy.com/hp/conversations";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (LayerConstants.ACTION_PUSH.equals(intent.getAction()))
        {
            final Intent orderedBroadcastIntent = new Intent(LayerConstants.ACTION_SHOW_NOTIFICATION);
            orderedBroadcastIntent.putExtras(intent.getExtras());
            context.sendOrderedBroadcast(orderedBroadcastIntent, null);
        }
        else
        {
            super.onReceive(context, intent);
        }
    }

    @Override
    protected PendingIntent createNotificationClickIntent(final Context context,
                                                          final Message message)
    {
        final Intent intent = new Intent(context, MessagesListActivity.class)
                .setPackage(context.getApplicationContext().getPackageName())
                .putExtra(LAYER_CONVERSATION_KEY, message.getConversation().getId())
                .putExtra(LAYER_MESSAGE_KEY, message.getId())
                .putExtra(LayerConstants.KEY_HIDE_ATTACHMENT_BUTTON, true)
                .putExtra(LayerConstants.KEY_BACK_NAVIGATION_DEEPLINK,
                        MESSAGES_BACK_NAVIGATION_DEEPLINK)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
