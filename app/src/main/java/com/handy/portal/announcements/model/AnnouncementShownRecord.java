package com.handy.portal.announcements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * a bundle of these are sent to the server so that it can determine what announcements to send back
 * based on the configured announcement show frequency
 */
public class AnnouncementShownRecord implements Serializable {
    @SerializedName("id")
    private String mAnnouncementId;

    /**
     * epoch timestamp in seconds representing when the announcement was displayed
     */
    @SerializedName("timestamp")
    private long mDisplayedEpochTimestampSeconds;

    public AnnouncementShownRecord(final String announcementId,
                            final long displayedEpochTimestampSeconds) {
        mAnnouncementId = announcementId;
        mDisplayedEpochTimestampSeconds = displayedEpochTimestampSeconds;
    }
}