package com.handy.portal.ui.view;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.model.HelpNode;
import com.handy.portal.ui.widget.CTAButton;
import com.handy.portal.util.TextUtils;

import butterknife.InjectView;

public final class HelpNodeView extends InjectedRelativeLayout
{
    @InjectView(R.id.info_layout)
    RelativeLayout infoLayout;
    @InjectView(R.id.info_text)
    TextView infoText;
    @InjectView(R.id.cta_layout)
    public ViewGroup ctaLayout;
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
        ctaLayout.removeAllViews();
        navOptionsLayout.removeAllViews();

        if (node == null)
        {
            Crashlytics.log("Trying to display a null help node");
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
        infoLayout.setVisibility(View.VISIBLE);

        String info = node.getContent();

        //Turn these off, children nodes can turn them on
        ctaLayout.setVisibility(View.INVISIBLE);
        contactButton.setVisibility(View.INVISIBLE);

        //TODO: Any inline images from the HTML are displayed as placeholders, need to figure out how to grab the images and display them
        infoText.setText(TextUtils.trim(Html.fromHtml(info)));

        //TODO: this determine what happens when you click on inline links, currently crashing on emulator?
        infoText.setMovementMethod(LinkMovementMethod.getInstance());

        for (final HelpNode child : node.getChildren())
        {
            if (child.getType() == null)
            {
                continue;
            }

            if (child.getType().equals(HelpNode.HelpNodeType.FAQ))
            {
                info += "<br/><br/><b>" + getContext().getString(R.string.related_faq) + ":</b>";

                for (final HelpNode faqChild : child.getChildren())
                {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            } else if (child.getType().equals(HelpNode.HelpNodeType.CTA))
            {
                //TODO: Re-enable CTAs when we support them
                //ctaLayout.setVisibility(View.VISIBLE);
                //addCtaButton(child);
                Crashlytics.log("No support for CTAs, required for node : " + child.getId());
            } else if (child.getType().equals(HelpNode.HelpNodeType.CONTACT))
            {
                contactButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void layoutNavList(final HelpNode node)
    {
        infoLayout.setVisibility(View.GONE);
        navOptionsLayout.setVisibility(View.VISIBLE);

        int count = 0;
        int size = node.getChildren().size();

        for (final HelpNode helpNode : node.getChildren())
        {
            final View navView;

            if (helpNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
            {
                navView = inflate(R.layout.list_item_help_booking_nav, navOptionsLayout);

                TextView textView = (TextView) navView.findViewById(R.id.service_text);
                textView.setText(helpNode.getService());

                textView = (TextView) navView.findViewById(R.id.date_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "EEEE',' MMMM d"));

                textView = (TextView) navView.findViewById(R.id.time_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "h:mmaaa \u2013 ")
                        + TextUtils.formatDecimal(helpNode.getHours(), "#.# ")
                        + getContext().getResources().getQuantityString(R.plurals.hour, (int) helpNode.getHours()));
            } else
            {
                navView = inflate(R.layout.list_item_help_nav, navOptionsLayout);

                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);
                textView.setText(helpNode.getLabel());
            }

            //round out the visuals of the last cell
            if (count == size - 1)
            {
                navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));
            }

            count++;
        }
    }

    private void addCtaButton(final HelpNode node)
    {
        final CTAButton ctaButton = (CTAButton) inflate(R.layout.fragment_cta_button_template, ctaLayout);
        ctaButton.initFromHelpNode(node, null); //TODO: Get real login/auth token?
    }

}
