package com.handy.portal.ui.clicklisteners;

import android.content.DialogInterface;
import android.os.Bundle;

import com.handy.portal.consts.MainViewTab;
import com.handy.portal.consts.TransitionStyle;
import com.handy.portal.event.Event;

/**
 * Created by cdavis on 6/24/15.
 */
public class NavigateToTabOnClickListener extends InjectedClickListener
{
    private MainViewTab targetTab;
    private Bundle arguments = new Bundle();
    private TransitionStyle transitionStyle = TransitionStyle.NONE;

    public NavigateToTabOnClickListener(MainViewTab targetTab)
    {
        this.targetTab = targetTab;
    }

    public NavigateToTabOnClickListener(MainViewTab targetTab, TransitionStyle transitionStyle)
    {
        this.targetTab = targetTab;
        this.transitionStyle = transitionStyle;
    }

    public NavigateToTabOnClickListener(MainViewTab targetTab, Bundle arguments)
    {
        this.targetTab = targetTab;
        this.arguments = arguments;
    }

    public NavigateToTabOnClickListener(MainViewTab targetTab, Bundle arguments, TransitionStyle transitionStyle)
    {
        this.targetTab = targetTab;
        this.arguments = arguments;
        this.transitionStyle = transitionStyle;
    }

    public void onClick(DialogInterface dialog, int which)
    {
        bus.post(new Event.NavigateToTabEvent(targetTab, arguments, transitionStyle));
    }
}
