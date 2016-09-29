package com.handy.portal.chat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by idhir on 9/30/16.
 */

public class LayerResponseWrapper implements Serializable
{
    @SerializedName("success")
    private boolean mSuccess;
    @SerializedName("identity_token")
    private String mIdentityToken;


    public LayerResponseWrapper(final boolean success, final String identityToken)
    {
        mSuccess = success;
        mIdentityToken = identityToken;
    }

    public boolean getSuccess()
    {
        return mSuccess;
    }

    public String getIdentityToken()
    {
        return mIdentityToken;
    }
}
