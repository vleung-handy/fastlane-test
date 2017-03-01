package com.handy.portal.deeplink;

import android.content.Intent;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.urbanairship.UAirship;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionResult;
import com.urbanairship.actions.Situation;
import com.urbanairship.util.UriUtils;

import javax.inject.Inject;

/**
 * Created by cdavis on 8/10/15.
 */
public class CustomDeepLinkAction extends Action {
    @Inject
    public CustomDeepLinkAction() {
    }

    @Override
    public ActionResult perform(ActionArguments arguments) {
        //Let the activity handle this, we are just catching the deep link here to prevent the validation from failing with the default OpenExternalUrlAction
        //Only activate if they opened a push
        //TODO: We may want to allow for automatically opened push notifs which would be PUSH_RECEIVED
        if (arguments.getSituation() == Situation.PUSH_OPENED) {
            Uri uri = UriUtils.parse(arguments.getValue().getString());

            if (uri == null) {
                Crashlytics.log("Deep link had a malformed URI : " + arguments.getValue().getString() + " aborting processing of deep link");
                return ActionResult.newEmptyResult();
            }

            Intent intent = new Intent("com.handy.portal.DeepLinkBroadcast", uri, UAirship.getApplicationContext(), DeepLinkService.class);
            intent.addFlags(268435456); //I'm not sure what this is but this code was copied from UA example
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES); //allows started of manually stopped apps or never launched apps
            UAirship.getApplicationContext().startService(intent);
            return ActionResult.newResult(arguments.getValue());
        }
        return ActionResult.newEmptyResult();
    }

    @Override
    // Do any argument validation here.  The action will only
    // perform if acceptsArguments is true.
    public boolean acceptsArguments(ActionArguments arguments) {
        return super.acceptsArguments(arguments);

    }
}
