package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TermsDetails implements Serializable
{
    @SerializedName("terms_code") private String code;
    @SerializedName("instructions") private String instructions;
    @SerializedName("action") private String action;
    @SerializedName("country") private String country;
    @SerializedName("content") private String content;

    public String getCode()
    {
        return code;
    }

    public String getInstructions()
    {
        return instructions;
    }

    public String getAction()
    {
        return action;
    }

    public String getCountry()
    {
        return country;
    }

    public String getContent()
    {
        return content;
    }
}
