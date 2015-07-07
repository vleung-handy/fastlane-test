package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cdavis on 5/15/15.
 */
public class LoginDetails
{
        @SerializedName("success")
        private boolean success;
        @SerializedName("user_credentials")
        private String userCredentials;
        @SerializedName("user_credentials_id")
        private String userCredentialsId;

        public boolean getSuccess() { return success; }
        public String getUserCredentials() { return userCredentials; }
        public String getUserCredentialsCookie() { return "user_credentials="+ getUserCredentials();}

        public String getUserCredentialsId() { return userCredentialsId; }
}
