package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

public final class UpdateDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("should_update")
        private boolean shouldUpdate;
        @SerializedName("download_url")
        private String downloadURL;

        public final boolean getSuccess() { return success; }
        public final boolean getShouldUpdate() { return shouldUpdate; }
        public final String getDownloadUrl() { return downloadURL; }
}
