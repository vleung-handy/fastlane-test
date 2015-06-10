package com.handy.portal.consts;

import com.handy.portal.R;

/**
 * Created by cdavis on 6/2/15.
 */
//have information about the anims and overlays that override the defaults
//
public enum TransitionStyle
{
    JOB_CLAIM_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.JOB_CLAIM_SUCCESS),
    JOB_CLAIM_FAIL(R.anim.fade_in, R.anim.fade_and_shrink_away, OverlayStyle.JOB_CLAIM_FAIL),
    TAB_TO_TAB(R.anim.fade_in, R.anim.fade_out),
    JOB_LIST_TO_DETAILS(R.anim.fade_in, R.anim.fade_out),
    NATIVE_TO_NATIVE(R.anim.slide_in_left, R.anim.slide_out_left),
    NATIVE_TO_WEBVIEW(R.anim.fade_in, R.anim.fade_out),
    WEBVIEW_TO_NATIVE(R.anim.fade_in, R.anim.fade_out),
    REFRESH_TAB(R.anim.fade_in, R.anim.fade_out),
    NONE()
    ;

    private int incomingAnimId;
    private int outgoingAnimId;
    private OverlayStyle overlayStyle;

    TransitionStyle()
    {
        this.incomingAnimId = R.anim.none;
        this.outgoingAnimId = R.anim.none;
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

    public boolean shouldShowOverlay()
    {
        return(overlayStyle.shouldShowOverlay());
    }

    public int getOverlayStringId() { return overlayStyle.getOverlayStringId();}
    public int getOverlayImageId() { return overlayStyle.getOverlayImageId(); }
    public int getOverlayBackingImageId() { return overlayStyle.getOverlayBackingImageId(); }

    public int getIncomingAnimId() { return incomingAnimId; }
    public int getOutgoingAnimId() { return outgoingAnimId; }

}

