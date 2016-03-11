package com.handy.portal.updater.model;

import com.google.gson.annotations.SerializedName;

public class UpdateDetails
{
    @SerializedName("success")
    private boolean success;
    @SerializedName("should_update")
    private boolean shouldUpdate;
    @SerializedName("download_url")
    private String downloadURL;
    @SerializedName("is_blocking")
    private Boolean mIsUpdateBlocking;

    public boolean getSuccess() { return success; }
    public boolean getShouldUpdate() { return shouldUpdate; }
    public String getDownloadUrl() { return downloadURL; }

    public boolean isUpdateBlocking()
    {
//        return false; //TODO remove test only
        //the update flow should be blocking by default
        return mIsUpdateBlocking == null ? true : mIsUpdateBlocking.booleanValue();
    }
}
