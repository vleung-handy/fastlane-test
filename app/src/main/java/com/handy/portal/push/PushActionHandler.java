package com.handy.portal.push;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.library.util.Utils;

public class PushActionHandler
{
    public static boolean handleAction(@NonNull final Context context,
                                       @NonNull final String action,
                                       @NonNull final Bundle arguments)
    {
        switch (action)
        {
            case PushActionConstants.ACTION_CONTACT_CALL:
                return handleContactCallAction(context, arguments);
            case PushActionConstants.ACTION_CONTACT_TEXT:
                return handleContactTextAction(context, arguments);
//            case PushActionConstants.ACTION_GROUP_OMW:
//                return handleOnMyWayAction(context, arguments);
        }
        return false;
    }

    private static boolean handleContactCallAction(final Context context, final Bundle arguments)
    {

        return handleContactAction(context, arguments, "tel");
    }

    private static boolean handleContactTextAction(final Context context, final Bundle arguments)
    {
        return handleContactAction(context, arguments, "sms");
    }

    private static boolean handleContactAction(final Context context, final Bundle arguments,
                                               final String scheme)
    {
        final String bookingPhone = arguments.getString("booking_phone");
        if (bookingPhone != null)
        {
            final Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.fromParts(scheme, bookingPhone, null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return Utils.safeLaunchIntent(intent, context);
        }
        return false;
    }

    private static boolean handleOnMyWayAction(final Context context, final Bundle arguments)
    {
        final String bookingId = arguments.getString("booking_id");
        //todo

        return true;
    }
}
