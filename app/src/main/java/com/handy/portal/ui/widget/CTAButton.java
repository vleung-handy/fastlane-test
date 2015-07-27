package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.handy.portal.model.HelpNode;

public final class CTAButton extends Button
{
//    @Inject
//    NavigationManager navigationManager;

    public CTANavigationData navigationData;
    public String nodeLabel;
    public int nodeId;

    public CTAButton(Context context) {
        super(context);
    }

    public CTAButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CTAButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CTAButton(Context context, View parent) {
        super(context);
    }

    public void initFromHelpNode(HelpNode node, String loginToken)
    {
        this.nodeLabel = node.getLabel();
        this.nodeId = node.getId();
        this.setText(this.nodeLabel);
        this.navigationData = new CTANavigationData(node, loginToken);
    }
}