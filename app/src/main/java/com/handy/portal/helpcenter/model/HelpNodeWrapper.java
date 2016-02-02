package com.handy.portal.helpcenter.model;

import com.google.gson.annotations.SerializedName;

public final class HelpNodeWrapper
{
    @SerializedName("node") private HelpNode helpNode;

    public HelpNodeWrapper(){}

    public HelpNode getHelpNode()
    {
        return helpNode;
    }
}
