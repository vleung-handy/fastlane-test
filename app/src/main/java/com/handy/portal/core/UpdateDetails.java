package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

public class UpdateDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("should_update")
        private boolean shouldUpdate;
        @SerializedName("download_url")
        private String downloadURL;

        public boolean getSuccess() { return success; }
        public boolean getShouldUpdate() { return shouldUpdate; }
        public String getDownloadUrl() { return downloadURL; }
}
