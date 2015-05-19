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
        @SerializedName("user_credentials_id")
        private String userCredentialsId;

        public final boolean getSuccess() { return success; }
        public final String getUserCredentials() { return userCredentials; }
        public final String getUserCredentialsCookie() { return "user_credentials="+ getUserCredentials();}

        public final String getUserCredentialsId() { return userCredentialsId; }
}
