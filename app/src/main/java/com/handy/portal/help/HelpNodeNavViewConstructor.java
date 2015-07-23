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

public final class HelpNodeNavViewConstructor
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected int getLayoutResourceId()
    {
        return R.layout.element_help_node_nav;
    }

    @InjectView(R.id.close_img)
    ImageView closeImage;
    @InjectView(R.id.back_img)
    ImageView backImage;
    @InjectView(R.id.nav_text)
    TextView navText;

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


//View Construction

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
                layoutForRoot(node, container);
                backImage.setVisibility(View.GONE);
                closeImage.setVisibility(View.GONE);
            }
            break;

            case "navigation":
            case "dynamic-bookings-navigation":
            case "booking":
            {
                layoutForNavigation(node, container);
                backImage.setVisibility(View.VISIBLE);
                closeImage.setVisibility(View.GONE);
            }
            break;

            case "article":
            {
                layoutForArticle(node);
                backImage.setVisibility(View.VISIBLE);
                closeImage.setVisibility(View.GONE);
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

    private void layoutForRoot(final HelpNode node, final ViewGroup container)
    {

    }

    private void layoutForNavigation(final HelpNode node, final ViewGroup container)
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
