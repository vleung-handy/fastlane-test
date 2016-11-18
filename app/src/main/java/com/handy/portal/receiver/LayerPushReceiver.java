package com.handy.portal.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.handybook.shared.LayerConstants;


public class LayerPushReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        final Intent orderedBroadcastIntent = new Intent(LayerConstants.ACTION_SHOW_NOTIFICATION);
        orderedBroadcastIntent.putExtras(intent.getExtras());
        context.sendOrderedBroadcast(orderedBroadcastIntent, null);
    }
}
