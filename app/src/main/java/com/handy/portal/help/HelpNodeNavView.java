package com.handy.portal.help;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpNodeNavView
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected int getLayoutResourceId()
    {
        return R.layout.element_help_node_nav;
    }

    @InjectView(R.id.back_img)
    public ImageView backImage;
    @InjectView(R.id.nav_text)
    public TextView navText;

    public void initView(ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);
        ButterKnife.inject(this, parentViewGroup);
    }

    public void updateDisplay(HelpNode helpNode)
    {
        constructNodeView(helpNode, this.parentViewGroup);
    }

    ///////////////////

    public void constructView(HelpNode helpNode, ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;

        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);

        ButterKnife.inject(this, parentViewGroup);

        constructHelpNodeView(helpNode);
    }

    protected void constructHelpNodeView(HelpNode helpNode)
    {
        constructNodeView(helpNode, this.parentViewGroup);
    }

    private void constructNodeView(final HelpNode node, final ViewGroup container)
    {
        if (node == null)
        {
            System.err.println("Tried to construct a node view for a null node");
            return;
        }

        switch (node.getType())
        {
            case "root":
            {
                layoutForArticle(node);
                backImage.setVisibility(View.GONE);
            }
            break;

            case "navigation":
            case "dynamic-bookings-navigation":
            case "booking":
            {
                layoutForNavigation(node);
                backImage.setVisibility(View.VISIBLE);
            }
            break;

            case "article":
            {
                layoutForArticle(node);
                backImage.setVisibility(View.VISIBLE);
            }
            break;

            default:
            {
                System.err.println("Unrecognized node type : " + node.getType());
                backImage.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void layoutForNavigation(final HelpNode node)
    {
        if (node.getType().equals("booking"))
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
