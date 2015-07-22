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
        public String getUserCredentialsToken() { return userCredentials; }
        public String getUserCredentialsCookie() { return "user_credentials="+ getUserCredentialsToken();}

        public String getUserCredentialsId() { return userCredentialsId; }
}
