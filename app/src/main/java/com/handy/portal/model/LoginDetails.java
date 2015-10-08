package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class LoginDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("user_credentials")
        private String userCredentials;
        @SerializedName("user_credentials_id")
        private String userCredentialsId;

        public boolean getSuccess() { return success; }
        public String getAuthToken() { return userCredentials; }

        public String getProviderId() { return userCredentialsId; }
}
