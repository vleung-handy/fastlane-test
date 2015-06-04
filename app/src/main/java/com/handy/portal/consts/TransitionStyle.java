package com.handy.portal.consts;

import com.handy.portal.R;

/**
 * Created by cdavis on 6/2/15.
 */
//have information about the anims and overlays that override the defaults
//
public enum TransitionStyle
{
    JOB_CLAIM_SUCCESS(R.anim.fade_in, R.anim.fade_and_shrink_away, R.string.job_claim_success, R.drawable.circle_green),
    JOB_CLAIM_FAIL(R.anim.fade_in, R.anim.fade_and_shrink_away, R.string.booking_action_error_not_available, R.drawable.circle_teal),
    TAB_TO_TAB(R.anim.fade_in, R.anim.fade_out,  -1, -1),
    JOB_LIST_TO_DETAILS(R.anim.fade_in, R.anim.fade_out,  -1, -1),
    NATIVE_TO_NATIVE(R.anim.slide_in_left, R.anim.slide_out_left,  -1, -1),
    NATIVE_TO_WEBVIEW(R.anim.fade_in, R.anim.fade_out,  -1, -1),
    WEBVIEW_TO_NATIVE(R.anim.fade_in, R.anim.fade_out,  -1, -1),
    NONE(R.anim.none, R.anim.none, -1, -1)
    ;

    private int incomingAnimId;
    private int outgoingAnimId;
    private int overlayStringId;
    private int overlayImageId;

    TransitionStyle(int incomingAnimId, int outgoingAnimId, int overlayStringId, int overlayImageId)
    {
        this.incomingAnimId = incomingAnimId;
        this.outgoingAnimId = outgoingAnimId;
        this.overlayStringId = overlayStringId;
        this.overlayImageId = overlayImageId;
    }

    public boolean shouldShowOverlay()
    {
        return(overlayStringId != -1 || overlayImageId != -1);
    }

    public int getOverlayStringId() { return overlayStringId; }
    public int getOverlayImageId() { return overlayImageId; }
    public int getIncomingAnimId() { return incomingAnimId; }
    public int getOutgoingAnimId() { return outgoingAnimId; }



}

