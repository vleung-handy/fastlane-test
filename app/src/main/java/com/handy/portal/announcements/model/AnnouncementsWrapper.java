package com.handy.portal.announcements.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * model from server
 */
public class AnnouncementsWrapper implements Serializable{
    @SerializedName("announcements")
    private Announcement[] mAnnouncements;

    public Announcement[] getAnnouncements() {
        return mAnnouncements;
    }
}
