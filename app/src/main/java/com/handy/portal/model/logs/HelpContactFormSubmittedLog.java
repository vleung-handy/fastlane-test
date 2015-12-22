package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public class HelpContactFormSubmittedLog extends EventLog
{
    private static String EVENT_CONTEXT = "help";
    private static String EVENT_TYPE = "contact_form_submitted";

    @SerializedName("path")
    private String mPath;
    @SerializedName("help_node_id")
    private int mHelpNodeId;
    @SerializedName("help_node_title")
    private String mHelpNodeTitle;

    public HelpContactFormSubmittedLog(
            String providerId, String versionTrack, String path, int helpNodeId, String helpNodeTitle)
    {
        super(providerId, versionTrack, EVENT_TYPE, EVENT_CONTEXT);
        mPath = path;
        mHelpNodeId = helpNodeId;
        mHelpNodeTitle = helpNodeTitle;
    }
}