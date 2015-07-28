package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.model.HelpNode;

import butterknife.InjectView;

public final class HelpBannerView extends InjectedRelativeLayout
{
    @InjectView(R.id.back_img)
    public ImageView backImage;

    @InjectView(R.id.nav_text)
    public TextView navText;

    public HelpBannerView(final Context context)
    {
        super(context);
    }

    public HelpBannerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HelpBannerView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay()
    {
        backImage.setVisibility(View.VISIBLE);
    }

    public void updateDisplay(HelpNode helpNode)
    {
        if (helpNode == null)
        {
            Crashlytics.log("Tried to construct a help banner view for a null node");
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
                Crashlytics.log("Unrecognized node type : " + helpNode.getType());
                backImage.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void layoutForNavigation(final HelpNode node)
    {
        if (node.getType().equals(HelpNode.HelpNodeType.BOOKING))
        {
           navText.setText(getContext().getString(R.string.help));
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
