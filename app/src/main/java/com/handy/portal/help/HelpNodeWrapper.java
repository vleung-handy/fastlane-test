package com.handy.portal.help;

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
