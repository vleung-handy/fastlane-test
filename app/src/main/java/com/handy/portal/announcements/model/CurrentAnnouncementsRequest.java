package com.handy.portal.announcements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * sent to the server as part of the request to get current announcements
 */
public class CurrentAnnouncementsRequest implements Serializable {
    @SerializedName("announcements")
    private List<AnnouncementShownRecord> mAnnouncementShownRecords;

    public CurrentAnnouncementsRequest(List<AnnouncementShownRecord> announcementShownRecords)
    {
        mAnnouncementShownRecords = announcementShownRecords;
    }
}
