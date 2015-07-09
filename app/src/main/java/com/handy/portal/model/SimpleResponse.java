package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cdavis on 5/15/15.
 */
public class SimpleResponse
{
    @SerializedName("success")
    private boolean success;

    public boolean getSuccess() { return success; }

}