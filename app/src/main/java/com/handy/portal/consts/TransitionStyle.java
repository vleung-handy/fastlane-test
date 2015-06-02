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

    public int[] getAnimationsIds()
    {
        int[] animationIds = new int[2];
        if(incomingAnimId != 0)
        {
            animationIds[TransitionAnimationIndex.INCOMING] = incomingAnimId;
        }
        if(outgoingAnimId != 0)
        {
            animationIds[TransitionAnimationIndex.OUTGOING] = outgoingAnimId;
        }
        return animationIds;
    }

    public int getOverlayStringId() { return overlayStringId; }
    public int getOverlayImageId() { return overlayImageId; }

}

