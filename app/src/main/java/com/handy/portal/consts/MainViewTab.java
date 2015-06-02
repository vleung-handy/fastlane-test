package com.handy.portal.consts;

import com.handy.portal.R;
import com.handy.portal.ui.element.TransitionOverlayView;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.PortalWebViewFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;

/**
 * Created by cdavis on 6/2/15.
 */
public enum MainViewTab
{
    JOBS(PortalWebViewFragment.Target.JOBS, AvailableBookingsFragment.class),
    SCHEDULE(PortalWebViewFragment.Target.SCHEDULE, ScheduledBookingsFragment.class),
    PROFILE(PortalWebViewFragment.Target.PROFILE, null),
    HELP(PortalWebViewFragment.Target.HELP, null),
    DETAILS(null, BookingDetailsFragment.class),;

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
    public Class getClassType() { return classType; }

    //If this gets back and complex setup a basic state machine for tab transitions with the relevant overlays and anims along the transitions
    public int[] getTransitionAnimationIds(MainViewTab targetTab)
    {
        int[] transitionAnimationIds = null;

        if (this.equals(MainViewTab.JOBS) && targetTab.equals(MainViewTab.DETAILS))
        {
            transitionAnimationIds = new int[2];
            transitionAnimationIds[TransitionAnimationIndex.INCOMING] = R.anim.slide_out_left;
            transitionAnimationIds[TransitionAnimationIndex.OUTGOING] = R.anim.slide_in_right;
        }

        return transitionAnimationIds;
    }

    //If this gets back and complex setup a basic state machine for tab transitions with the relevant overlays and anims along the transitions

    public boolean setupOverlay(MainViewTab targetTab, TransitionOverlayView transitionOverlayView)
    {
        return setupOverlay(targetTab, transitionOverlayView, null);
    }

    public boolean setupOverlay(MainViewTab targetTab, TransitionOverlayView transitionOverlayView, TransitionStyle overrideTransitionStyle)
    {
        boolean shouldShowOverlay = false;

        //TODO: Are there any defaults that should be in here based on basic tab to tab transitions?

        if (overrideTransitionStyle != null)
        {
            shouldShowOverlay = true;
            transitionOverlayView.setText(overrideTransitionStyle.getOverlayStringId());
            transitionOverlayView.setImage(overrideTransitionStyle.getOverlayImageId());
        }

        return shouldShowOverlay;
    }

    //Eventually all tabs will be native tabs
    public boolean isNativeTab()
    {
        return (this.classType != null);
    }

}
