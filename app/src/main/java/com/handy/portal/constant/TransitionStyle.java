package com.handy.portal.constant;

import com.handy.portal.R;

//have information about the anims and overlays that override the defaults
public enum TransitionStyle
{
    JOB_CLAIM_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.JOB_CLAIM_SUCCESS),
    JOB_REMOVE_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.JOB_REMOVE_SUCCESS),
    SERIES_CLAIM_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.SERIES_CLAIM_SUCCESS),
    SERIES_REMOVE_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.SERIES_REMOVE_SUCCESS),
    TAB_TO_TAB(R.anim.fade_in, R.anim.fade_out),
    JOB_LIST_TO_DETAILS(R.anim.fade_in, R.anim.fade_out),
    NATIVE_TO_NATIVE(R.anim.fade_in, R.anim.fade_out),
    NATIVE_TO_WEBVIEW(R.anim.fade_in, R.anim.fade_out),
    WEBVIEW_TO_NATIVE(R.anim.fade_in, R.anim.fade_out),
    REFRESH_TAB(R.anim.fade_in, R.anim.fade_out),
    SLIDE_UP(R.anim.slide_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_down),
    NONE();

    private int incomingAnimId;
    private int outgoingAnimId;
    private int popIncomingAnimId;
    private int popOutgoingAnimId;
    private OverlayStyle overlayStyle;

    TransitionStyle()
    {
        this.incomingAnimId = R.anim.none;
        this.outgoingAnimId = R.anim.none;
        this.popIncomingAnimId = R.anim.none;
        this.popOutgoingAnimId = R.anim.none;
        this.overlayStyle = OverlayStyle.NONE;
    }

    TransitionStyle(int incomingAnimId, int outgoingAnimId)
    {
        this.incomingAnimId = incomingAnimId;
        this.outgoingAnimId = outgoingAnimId;
        this.overlayStyle = OverlayStyle.NONE;
    }

    TransitionStyle(int incomingAnimId, int outgoingAnimId, OverlayStyle overlayStyle)
    {
        this.incomingAnimId = incomingAnimId;
        this.outgoingAnimId = outgoingAnimId;
        this.overlayStyle = overlayStyle;
    }

    TransitionStyle(int incomingAnimId, int outgoingAnimId, int popIncomingAnimId, int popOutgoingAnimId)
    {
        this(incomingAnimId, outgoingAnimId);
        this.popIncomingAnimId = popIncomingAnimId;
        this.popOutgoingAnimId = popOutgoingAnimId;
    }

    public boolean shouldShowOverlay()
    {
        return (overlayStyle.shouldShowOverlay());
    }

    public int getOverlayStringId()
    {
        return overlayStyle.getOverlayStringId();
    }

    public int getOverlayImageId()
    {
        return overlayStyle.getOverlayImageId();
    }

    public int getIncomingAnimId()
    {
        return incomingAnimId;
    }

    public int getOutgoingAnimId()
    {
        return outgoingAnimId;
    }

    public int getPopIncomingAnimId()
    {
        return popIncomingAnimId;
    }

    public int getPopOutgoingAnimId()
    {
        return popOutgoingAnimId;
    }

}

