package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class LoginDetails
{
    @SerializedName("user_credentials")
    private String userCredentials;
    @SerializedName("user_credentials_id")
    private String userCredentialsId;

    public String getAuthToken() { return userCredentials; }

    public String getProviderId() { return userCredentialsId; }

    public String getUserCredentialsCookie() { return "user_credentials=" + getAuthToken();}
}
