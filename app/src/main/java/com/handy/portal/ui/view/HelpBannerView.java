package com.handy.portal.ui.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.HelpNode;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpBannerView
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected int getLayoutResourceId()
    {
        return R.layout.element_help_node_banner;
    }

    @InjectView(R.id.back_img)
    public ImageView backImage;
    @InjectView(R.id.nav_text)
    public TextView navText;

    public HelpBannerView(ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);
        ButterKnife.inject(this, parentViewGroup);
    }

    public void updateDisplay(HelpNode helpNode)
    {
        if (helpNode == null)
        {
            System.err.println("Tried to construct a node view for a null node");
            return;
        }

        switch (helpNode.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            {
                layoutForArticle(helpNode);
                backImage.setVisibility(View.GONE);
            }
            break;


            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutForNavigation(helpNode);
                backImage.setVisibility(View.VISIBLE);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                layoutForArticle(helpNode);
                backImage.setVisibility(View.VISIBLE);
            }
            break;

            default:
            {
                System.err.println("Unrecognized node type : " + helpNode.getType());
                backImage.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void layoutForNavigation(final HelpNode node)
    {
        if (node.getType().equals(HelpNode.HelpNodeType.BOOKING))
        {
           navText.setText(activity.getString(R.string.help));
        }
        else
        {
           navText.setText(node.getLabel());
        }
    }

    private void layoutForArticle(final HelpNode node)
    {
        navText.setText(node.getLabel());
    }




}
