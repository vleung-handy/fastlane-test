package com.handy.portal.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.model.HelpNode;
import com.handy.portal.ui.element.HandyWebView;
import com.handy.portal.util.TextUtils;

import butterknife.InjectView;

public final class HelpNodeView extends InjectedRelativeLayout
{
    @InjectView(R.id.help_webview)
    HandyWebView helpWebView;
    @InjectView(R.id.info_layout)
    RelativeLayout infoLayout;
    @InjectView(R.id.contact_button)
    public Button contactButton;
    @InjectView(R.id.nav_options_layout)
    public LinearLayout navOptionsLayout;

    public HelpNodeView(final Context context)
    {
        super(context);
    }

    public HelpNodeView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HelpNodeView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void updateDisplay(final HelpNode node)
    {
        //clear out the existing ctas and navigation buttons
        navOptionsLayout.removeAllViews();
        helpWebView.clearHtml();//prevent user from seeing previous article's content

        if (node == null)
        {
            Crashlytics.log("Trying to display a null help node");
            return;
        }

        if (node.getType() == null)
        {
            Crashlytics.log("Trying to display a help node with null type");
            return;
        }

        switch (node.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutNavList(node);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                layoutForArticle(node);
            }
            break;

            default:
            {
                Crashlytics.log("Unrecognized node type : " + node.getType());
            }
            break;
        }
    }

    private void layoutForArticle(final HelpNode node)
    {
        contactButton.setVisibility(GONE);
        //Turn these off, children nodes can turn them on

        helpWebView.loadHtml(node.getContent(), new HandyWebView.InvalidateCallback()
        {
            @Override
            public void invalidate()
            {
                if (infoLayout.getVisibility() != VISIBLE)
                {
                    infoLayout.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                    infoLayout.setVisibility(VISIBLE);
                }
            }
        });

        for (final HelpNode childNode : node.getChildren())
        {
            if(childNode == null)
            {
                Crashlytics.log("HelpNode " + node.getId() + " has a null child");
                continue;
            }

            if (childNode.getType() == null)
            {
                Crashlytics.log("HelpNode " + childNode.getId() + " has null type data");
                continue;
            }

            if (childNode.getType().equals(HelpNode.HelpNodeType.CONTACT))
            {
                contactButton.setVisibility(VISIBLE);
            }
        }
    }

    private void layoutNavList(final HelpNode node)
    {
        infoLayout.setVisibility(GONE);
        navOptionsLayout.setVisibility(VISIBLE);

        for (final HelpNode childNode : node.getChildren())
        {
            final View navView;

            if (childNode == null)
            {
                Crashlytics.log("HelpNode " + node.getId() + " has a null child or child missing a type");
                continue;
            }

            if (childNode.getType() == null)
            {
                Crashlytics.log("HelpNode " + node.getId() + " has a child missing a type, child id " + childNode.getId());
                continue;
            }

            if (childNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
            {
                navView = inflate(R.layout.list_item_help_booking_nav, navOptionsLayout);

                TextView textView = (TextView) navView.findViewById(R.id.service_text);
                textView.setText(childNode.getService());

                textView = (TextView) navView.findViewById(R.id.date_text);
                textView.setText(TextUtils.formatDate(childNode.getStartDate(), "EEEE',' MMMM d"));

                textView = (TextView) navView.findViewById(R.id.time_text);
                textView.setText(TextUtils.formatDate(childNode.getStartDate(), "h:mmaaa \u2013 ")
                        + TextUtils.formatDecimal(childNode.getHours(), "#.# ")
                        + getContext().getResources().getQuantityString(R.plurals.hour, (int) childNode.getHours()));
            }
            else
            {
                navView = inflate(R.layout.list_item_help_nav, navOptionsLayout);

                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);
                textView.setText(childNode.getLabel());
            }
        }
    }

}
