package com.handy.portal.consts;

import com.handy.portal.R;

/**
 * Created by cdavis on 6/2/15.
 */
//have information about the anims and overlays that override the defaults
//
public enum OverlayStyle
{
    JOB_CLAIM_SUCCESS(R.string.job_claim_success, R.drawable.ic_check, R.drawable.circle_green),
    SERIES_CLAIM_SUCCESS(R.string.series_claim_success, R.drawable.ic_check, R.drawable.circle_green),
    JOB_CLAIM_FAIL(R.string.booking_action_error_not_available,R.drawable.ic_x, R.drawable.circle_red),
    NONE(-1, -1, -1)
    ;

    private int overlayStringId;
    private int overlayImageId;
    private int overlayBackingImageId;

    OverlayStyle(int overlayStringId, int overlayImageId, int overlayBackingImageId)
    {
        this.overlayStringId = overlayStringId;
        this.overlayImageId = overlayImageId;
        this.overlayBackingImageId = overlayBackingImageId;
    }

    public boolean shouldShowOverlay()
    {
        return(overlayStringId != -1 || overlayImageId != -1 || overlayBackingImageId != -1);
    }

    public int getOverlayStringId() { return overlayStringId; }
    public int getOverlayImageId() { return overlayImageId; }
    public int getOverlayBackingImageId() { return overlayBackingImageId; }
}

