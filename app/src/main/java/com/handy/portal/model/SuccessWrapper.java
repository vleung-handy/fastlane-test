package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SuccessWrapper implements Serializable
{
    @SerializedName("success")
    private Boolean success;


    public Boolean getSuccess()
    {
        return success;
    }
}
