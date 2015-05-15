package com.handy.portal.core;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cdavis on 5/15/15.
 */
public class PinRequestDetails
{
    @SerializedName("success")
    private boolean success;

    public boolean getSuccess() {return success;}
}
