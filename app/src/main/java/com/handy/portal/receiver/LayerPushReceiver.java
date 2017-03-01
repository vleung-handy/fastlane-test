package com.handy.portal.receiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ConversationsLog;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.receiver.PushNotificationReceiver;
import com.handybook.shared.layer.ui.MessagesListActivity;
import com.layer.sdk.messaging.Message;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;


public class LayerPushReceiver extends PushNotificationReceiver {
    @Inject
    EventBus mBus;

    private static final String MESSAGES_DEEPLINK = "handypro://handy.com/hp/conversations";

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (LayerConstants.ACTION_PUSH.equals(intent.getAction())) {
            Utils.inject(context, this);
            final Intent orderedBroadcastIntent = new Intent(LayerConstants.ACTION_SHOW_NOTIFICATION);
            orderedBroadcastIntent.putExtras(intent.getExtras());
            context.sendOrderedBroadcast(orderedBroadcastIntent, null);
            mBus.post(new LogEvent.AddLogEvent(new ConversationsLog.PushNotificationReceived()));
        }
        else {
            super.onReceive(context, intent);
        }
    }

    @Nullable
    @Override
    protected PendingIntent createNotificationClickIntent(final Context context,
                                                          @Nullable final Message message) {
        Intent intent;
        if (message != null) {
            intent = new Intent(context, MessagesListActivity.class)
                    .setPackage(context.getApplicationContext().getPackageName())
                    .putExtra(LayerConstants.LAYER_CONVERSATION_KEY, message.getConversation().getId())
                    .putExtra(LayerConstants.LAYER_MESSAGE_KEY, message.getId())
                    .putExtra(LayerConstants.KEY_HIDE_ATTACHMENT_BUTTON, true)
                    .putExtra(LayerConstants.KEY_BACK_NAVIGATION_DEEPLINK, MESSAGES_DEEPLINK);
        }
        else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MESSAGES_DEEPLINK))
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
