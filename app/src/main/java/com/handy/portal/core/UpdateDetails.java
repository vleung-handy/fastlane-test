package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

public final class UpdateDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("should_update")
        private boolean shouldUpdate;

        public final boolean getSuccess() { return success; }
        public final boolean getShouldUpdate() { return shouldUpdate; }
}
