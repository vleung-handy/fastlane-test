package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cdavis on 5/15/15.
 */
public final class LoginDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("user_credentials")
        private String userCredentials;
        @SerializedName("_handybook_session_id")
        private String handybookSessionId;

        public final boolean getSuccess() { return success; }
        public final String getUserCredentials() { return userCredentials; }
        public final String getHandybookSessionId() { return handybookSessionId; }

        public final String getUserCredentialsCookie() { return "user_credentials="+ getUserCredentials();}
        public final String getHandybookSessionIdCookie() { return "_handybook_session_id="+ getUserCredentials();}
}
