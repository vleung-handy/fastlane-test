package com.handy.portal.constant;

import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.ui.fragment.HelpContactFragment;
import com.handy.portal.ui.fragment.HelpFragment;
import com.handy.portal.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.ui.fragment.PaymentsFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;

public enum MainViewTab
{
    AVAILABLE_JOBS(null, AvailableBookingsFragment.class),
    SCHEDULED_JOBS(null, ScheduledBookingsFragment.class),
    COMPLEMENTARY_JOBS(null, ComplementaryBookingsFragment.class),
    PAYMENTS(null, PaymentsFragment.class),
    PAYMENTS_DETAIL(null, PaymentsDetailFragment.class),
    PROFILE(PortalWebViewFragment.Target.PROFILE, null),
    HELP(null, HelpFragment.class),
    DETAILS(null, BookingDetailsFragment.class),
    HELP_CONTACT(null, HelpContactFragment.class),
    ;

    private PortalWebViewFragment.Target target;
    private Class classType;

    MainViewTab(PortalWebViewFragment.Target target, Class classType)
    {
        this.target = target;
        this.classType = classType;
    }

    public PortalWebViewFragment.Target getTarget()
    {
        return target;
    }

    public Class getClassType()
    {
        return classType;
    }

    //Eventually all tabs will be native tabs
    public boolean isNativeTab()
    {
        return (this.classType != null);
    }

    //If this gets complex setup small state machines to have a transition for each to/from tab
    public TransitionStyle getDefaultTransitionStyle(MainViewTab targetTab)
    {
        if (this.equals(targetTab))
        {
            return TransitionStyle.REFRESH_TAB;
        }

        if (this.equals(MainViewTab.AVAILABLE_JOBS) && targetTab.equals(MainViewTab.DETAILS))
        {
            return TransitionStyle.JOB_LIST_TO_DETAILS;
        }

        if (this.isNativeTab() && targetTab.isNativeTab())
        {
            return TransitionStyle.NATIVE_TO_NATIVE;
        }

        if (this.isNativeTab() && !targetTab.isNativeTab())
        {
            return TransitionStyle.NATIVE_TO_WEBVIEW;
        }

        if (!this.isNativeTab() && targetTab.isNativeTab())
        {
            return TransitionStyle.WEBVIEW_TO_NATIVE;
        }

        return TransitionStyle.NONE;
    }

}
