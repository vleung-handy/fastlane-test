package com.handy.portal.help;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.ui.widget.CTAButton;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpNodeView
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected int getLayoutResourceId()
    {
        return R.layout.element_help_node;
    }

    @InjectView(R.id.help_header)
    View helpHeader;
    @InjectView(R.id.help_header_title)
    TextView headerTitle;
    @InjectView(R.id.info_text)
    TextView infoText;
    @InjectView(R.id.nav_options_layout)
    public LinearLayout navOptionsLayout;
    @InjectView(R.id.info_layout)
    RelativeLayout infoLayout;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_triangle)
    ImageView helpTriangleView;
    @InjectView(R.id.cta_layout)
    public ViewGroup ctaLayout;
    @InjectView(R.id.contact_button)
    public Button contactButton;

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

//View Construction

    private void constructNodeView(final HelpNode node, final ViewGroup container)
    {
        //clear out the stuff that should be cleared
        navOptionsLayout.removeAllViews();
        ctaLayout.removeAllViews();

        System.out.println("Construct node view : " + node.getId() + " : " + container.getId());

        if (node == null)
        {
            System.err.println("Tried to construct a node view for a null node");
            return;
        }

        switch (node.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            {
                layoutForRoot(node, container);
                layoutNavList(node, container);
            }
            break;

            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                layoutNavList(node, container);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                layoutForArticle(node);
            }
            break;

            default:
            {
                System.err.println("Unrecognized node type : " + node.getType());
            }
            break;
        }
    }

    private void layoutForRoot(final HelpNode node, final ViewGroup container)
    {
        headerTitle.setText(activity.getResources().getString(R.string.what_need_help_with));
        setHeaderColor(activity.getResources().getColor(R.color.handy_blue));
        helpIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_help_smiley));
    }

    private void layoutForArticle(final HelpNode node)
    {
        infoLayout.setVisibility(View.VISIBLE);

        setHeaderColor(activity.getResources().getColor(R.color.handy_yellow));
        helpIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_help_bulb));
        helpTriangleView.setVisibility(View.VISIBLE);

        String info = node.getContent();

        //Turn these off, children nodes can turn them on
        ctaLayout.setVisibility(View.INVISIBLE);
        contactButton.setVisibility(View.INVISIBLE);

        System.out.println("Article node info : " + info);
        System.out.println("See num node children : " + node.getChildren().size());

        //TODO: Any inline images from the HTML are displayed as placeholders, need to figure out how to grab the images and display them
        infoText.setText(TextUtils.trim(Html.fromHtml(info)));

        //TODO: this determine what happens when you click on inline links, currently crashing on emulator?
        infoText.setMovementMethod(LinkMovementMethod.getInstance());

        for (final HelpNode child : node.getChildren())
        {
            if (child.getType() == null)
            {
                System.err.println("HelpNode " + child.getId() + " has null data");
                continue;
            }

            System.out.println("See node child : " + child.getType());

            if (child.getType().equals(HelpNode.HelpNodeType.FAQ))
            {
                info += "<br/><br/><b>" + activity.getString(R.string.related_faq) + ":</b>";

                for (final HelpNode faqChild : child.getChildren())
                {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            }
            else if (child.getType().equals(HelpNode.HelpNodeType.CTA))
            {
                //TODO: Re-enable CTAs when we support them
                    //ctaLayout.setVisibility(View.VISIBLE);
                    //addCtaButton(child);
                System.err.println("No support for CTAs, required for node : " + child.getId());
            }
            else if (child.getType().equals(HelpNode.HelpNodeType.CONTACT))
            {
                contactButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void layoutNavList(final HelpNode node, final ViewGroup container)
    {
        infoLayout.setVisibility(View.GONE);
        navOptionsLayout.setVisibility(View.VISIBLE);

        if (node.getType().equals(HelpNode.HelpNodeType.BOOKINGS_NAV))
        {
            setHeaderColor(activity.getResources().getColor(R.color.handy_teal));
        }

        int count = 0;
        int size = node.getChildren().size();

        for (final HelpNode helpNode : node.getChildren())
        {
            final View navView;

            if (helpNode.getType().equals(HelpNode.HelpNodeType.BOOKING))
            {
                navView = activity.getLayoutInflater()
                        .inflate(R.layout.list_item_help_booking_nav, container, false);

                TextView textView = (TextView) navView.findViewById(R.id.service_text);
                textView.setText(helpNode.getService());

                textView = (TextView) navView.findViewById(R.id.date_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "EEEE',' MMMM d"));

                textView = (TextView) navView.findViewById(R.id.time_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "h:mmaaa \u2013 ")
                        + TextUtils.formatDecimal(helpNode.getHours(), "#.# ")
                        + activity.getResources().getQuantityString(R.plurals.hour, (int) helpNode.getHours()));
            } else
            {
                if (node.getType().equals(HelpNode.HelpNodeType.ROOT))
                {
                    navView = activity.getLayoutInflater()
                            .inflate(R.layout.list_item_help_nav_main, container, false);
                } else
                {
                    navView = activity.getLayoutInflater()
                            .inflate(R.layout.list_item_help_nav, container, false);
                }

                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);
                textView.setText(helpNode.getLabel());

                if (node.getType().equals(HelpNode.HelpNodeType.ROOT))
                {
                    textView.setTextAppearance(activity, R.style.TextView_Large);
                }
            }

            //round out the visuals of the last cell
            if (count == size - 1)
            {
                navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));
            }

            navOptionsLayout.addView(navView);
            count++;
        }
    }

    private void setHeaderColor(final int color)
    {
        final Drawable header = activity.getResources().getDrawable(R.drawable.help_header_purple);

        header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
        {
            helpHeader.setBackgroundDrawable(header);
        }
        else
        {
            helpHeader.setBackground(header);
        }
    }

    private void addCtaButton(final HelpNode node)
    {
        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
        final CTAButton ctaButton = (CTAButton) ((ViewGroup) activity.getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);
        ctaButton.initFromHelpNode(node, null); //TODO: Get real login/auth token?
    }

}
