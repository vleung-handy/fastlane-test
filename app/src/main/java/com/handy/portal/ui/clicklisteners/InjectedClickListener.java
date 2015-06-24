package com.handy.portal.ui.clicklisteners;

import android.content.DialogInterface;

import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by cdavis on 6/24/15.
 */
public abstract class InjectedClickListener implements DialogInterface.OnClickListener
{
    @Inject
    Bus bus;

    InjectedClickListener()
    {
    }

    public abstract void onClick(DialogInterface dialog, int which);
}
