package com.handy.portal.ui.widget;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.helpcenter.model.HelpNode;

import org.json.JSONObject;

public final class CTANavigationData
{
    private static final String NODE_CONTENT_DATA_ACTION = "action";
    private static final String NODE_CONTENT_DATA_WEB_URL = "web_url";

    public String nodeContentWebUrl;
    public String navigationActionId;
    public String loginToken;

    public CTANavigationData()
    {
        initData("", ""); this.loginToken = "";
    }

    public CTANavigationData(String navigationActionId, String nodeContentWebUrl, String loginToken)
    {
        initData(navigationActionId, nodeContentWebUrl);
        this.loginToken = (loginToken != null ? loginToken : "");
    }

    public CTANavigationData(HelpNode node, String loginToken)
    {
        JSONObject nodeContentData;
        try
        {
            nodeContentData = new JSONObject(node.getContent());

            String action = "";
            if(nodeContentData.has(NODE_CONTENT_DATA_ACTION))
            {
                action = nodeContentData.getString(NODE_CONTENT_DATA_ACTION);
            }

            String webUrl = "";
            if(nodeContentData.has(NODE_CONTENT_DATA_WEB_URL))
            {
                webUrl = nodeContentData.getString(NODE_CONTENT_DATA_WEB_URL);
            }

            initData(action, webUrl);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }

        this.loginToken = (loginToken != null ? loginToken : "");
    }

    private void initData(String navigationActionId, String nodeContentWebUrl)
    {
        this.navigationActionId = navigationActionId;
        this.nodeContentWebUrl = nodeContentWebUrl;
    }
}