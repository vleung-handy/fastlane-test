package com.handy.portal.constant;

import com.handy.portal.R;

/**
 * Created by cdavis on 6/2/15.
 */
//have information about the anims and overlays that override the defaults
//
public enum OverlayStyle
{
    JOB_CLAIM_SUCCESS(R.string.job_claim_success, R.drawable.ic_success_circle),
    SERIES_CLAIM_SUCCESS(R.string.series_claim_success, R.drawable.ic_success_circle),
    JOB_REMOVE_SUCCESS(R.string.job_remove_success, R.drawable.ic_success_circle),
    SERIES_REMOVE_SUCCESS(R.string.series_remove_success, R.drawable.ic_success_circle),
    NONE(-1, -1)
    ;

    private int overlayStringId;
    private int overlayImageId;

    OverlayStyle(int overlayStringId, int overlayImageId)
    {
        this.overlayStringId = overlayStringId;
        this.overlayImageId = overlayImageId;
    }

    public boolean shouldShowOverlay()
    {
        return(overlayStringId != -1 || overlayImageId != -1);
    }

    public int getOverlayStringId() { return overlayStringId; }
    public int getOverlayImageId() { return overlayImageId; }

}

