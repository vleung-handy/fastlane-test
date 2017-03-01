package com.handy.portal.updater.model;

import com.google.gson.annotations.SerializedName;

public class UpdateDetails {
    @SerializedName("success")
    private boolean success;
    @SerializedName("should_update")
    private boolean shouldUpdate;
    @SerializedName("download_url")
    private String downloadURL;
    @SerializedName("is_blocking")
    private Boolean mIsUpdateBlocking;
    @SerializedName("hide_nonblocking_update_duration_mins")
    private Integer mHideNonBlockingUpdateDurationMins; //min number of minutes to wait between showing NON-blocking update screen

    private static final int DEFAULT_HIDE_NONBLOCKING_UPDATE_DURATION_MINS = 5;

    public boolean getSuccess() { return success; }

    public boolean getShouldUpdate() { return shouldUpdate; }

    public String getDownloadUrl() { return downloadURL; }

    public boolean isUpdateBlocking() {
        //the update flow should be blocking by default
        return mIsUpdateBlocking == null ? true : mIsUpdateBlocking;
    }

    /**
     * min number of minutes to wait between showing NON-blocking update screen
     *
     * @return DEFAULT_HIDE_NONBLOCKING_UPDATE_DURATION_MINS if server doesn't return a backoff duration value
     */
    public int getHideNonBlockingUpdateDurationMins() {
        return mHideNonBlockingUpdateDurationMins == null ? DEFAULT_HIDE_NONBLOCKING_UPDATE_DURATION_MINS : mHideNonBlockingUpdateDurationMins;
    }
}
