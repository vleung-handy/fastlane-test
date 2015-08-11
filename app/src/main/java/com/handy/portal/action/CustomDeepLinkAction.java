package com.handy.portal.action;

import android.content.Intent;
import android.net.Uri;

import com.squareup.otto.Bus;
import com.urbanairship.Logger;
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
public class CustomDeepLinkAction extends Action
{
    private Bus bus;

    @Inject
    public CustomDeepLinkAction(final Bus bus)
    {
        this.bus = bus;
        this.bus.register(this);
    }

    @Override
    public ActionResult perform(ActionArguments arguments)
    {
        //Let the activity handle this, we are just catching the deep link here to prevent the validation from failing with the default OpenExternalUrlAction
        //Only activate if they opened a push
        //TODO: We may want to allow for automatically opened push notifs which would be PUSH_RECEIVED
        if(arguments.getSituation() == Situation.PUSH_OPENED)
        {
            Uri uri = UriUtils.parse(arguments.getValue().getString());
            Logger.info("Opening URI: " + uri);
            //Intent should get grabbed by our main activity
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.addFlags(268435456);
            UAirship.getApplicationContext().startActivity(intent);
            return ActionResult.newResult(arguments.getValue());
        }
        return ActionResult.newEmptyResult();
    }

    @Override
    // Do any argument validation here.  The action will only
    // perform if acceptsArguments is true.
    public boolean acceptsArguments(ActionArguments arguments)
    {
        if (!super.acceptsArguments(arguments))
        {
            return false;
        }

        if(arguments.getValue() != null)
        {
            return true;
        }

        return false;
    }
}
