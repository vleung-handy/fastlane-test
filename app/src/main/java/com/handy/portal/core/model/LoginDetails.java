package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

public class LoginDetails
{
    @SerializedName("success")
    private boolean success;
    @SerializedName("user_credentials")
    private String mUserCredentials;
    @SerializedName("user_credentials_id")
    private String mUserCredentialsId;

    public LoginDetails(final boolean success, final String userCredentials, final String userCredentialsId)
    {
        this.success = success;
        mUserCredentials = userCredentials;
        mUserCredentialsId = userCredentialsId;
    }

    public boolean getSuccess() { return success; }

    public String getAuthToken() { return mUserCredentials; }

    public String getProviderId() { return mUserCredentialsId; }

    public String getUserCredentialsCookie() { return "user_credentials=" + getAuthToken();}
}
