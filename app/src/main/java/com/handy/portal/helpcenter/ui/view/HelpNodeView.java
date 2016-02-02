package com.handy.portal.helpcenter.ui.view;

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
import com.handy.portal.helpcenter.model.HelpNode;
import com.handy.portal.ui.element.HandyWebView;
import com.handy.portal.ui.view.InjectedRelativeLayout;
import com.handy.portal.util.TextUtils;

import butterknife.Bind;

public final class HelpNodeView extends InjectedRelativeLayout
{
    @Bind(R.id.help_webview)
    HandyWebView helpWebView;
    @Bind(R.id.info_layout)
    RelativeLayout infoLayout;
    @Bind(R.id.contact_button)
    public Button contactButton;
    @Bind(R.id.nav_options_layout)
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

    public void updateDisplay(final HelpNode helpNode)
    {
        //clear out the existing ctas and navigation buttons
        navOptionsLayout.removeAllViews();
        helpWebView.clearHtml();//prevent user from seeing previous article's content

        if (!HelpNode.isValid(helpNode))
        {
            Crashlytics.log("Trying to display a null or invalid help node");
            return;
        }

        switch (helpNode.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutNavList(helpNode);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                layoutForArticle(helpNode);
            }
            break;

            default:
            {
                Crashlytics.log("Unrecognized node type : " + helpNode.getType());
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
            if (!HelpNode.isValid(childNode))
            {
                Crashlytics.log("HelpNode " + node.getId() + " has an invalid child");
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

            if (!HelpNode.isValid(childNode))
            {
                Crashlytics.log("HelpNode " + node.getId() + " has an invalid child");
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
