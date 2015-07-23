package com.handy.portal.help;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.widget.CTAButton;
import com.handy.portal.util.TextUtils;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpNodeViewConstructor
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;
    protected HelpFragment helpFragment; //horrible hack should not know about fragment

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
    LinearLayout navOptionsLayout;
    @InjectView(R.id.info_layout)
    RelativeLayout infoLayout;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_triangle)
    ImageView helpTriangleView;
    @InjectView(R.id.cta_layout)
    ViewGroup ctaLayout;
    @InjectView(R.id.contact_button)
    Button contactButton;




    public void constructView(HelpNode helpNode, ViewGroup parentViewGroup, Activity activity, HelpFragment helpFragment)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        this.helpFragment = helpFragment;

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
        //clear out the stuff that should be cleared
        navOptionsLayout.removeAllViews();
        ctaLayout.removeAllViews();

        if (node == null)
        {
            System.err.println("Tried to construct a node view for a null node");
            return;
        }

        System.out.println("Setting up for node of type : " + node.getType());

        switch (node.getType())
        {
            case "root":
            {
                layoutForRoot(node, container);
               // backImage.setVisibility(View.GONE);
            }
            break;

            case "navigation":
            case "dynamic-bookings-navigation":
            case "booking":
            {
                layoutForNavigation(node, container);
                //backImage.setVisibility(View.VISIBLE);
            }
            break;

            case "article":
            {

                System.out.println("Going to layout for article");

                layoutForArticle(node);
               // backImage.setVisibility(View.VISIBLE);

            }
            break;

            default:
            {
                System.err.println("Unrecognized node type : " + node.getType());
                //backImage.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void layoutForRoot(final HelpNode node, final ViewGroup container)
    {
       // closeImage.setVisibility(View.GONE);
        headerTitle.setText(activity.getResources().getString(R.string.what_need_help_with));
        setHeaderColor(activity.getResources().getColor(R.color.handy_blue));
        helpIcon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_help_smiley));
        layoutNavList(node, container);
    }

    private void layoutForNavigation(final HelpNode node, final ViewGroup container)
    {
        if (node.getType().equals("booking"))
        {
          // navText.setText(activity.getString(R.string.help));
        }
        else
        {
          // navText.setText(node.getLabel());
        }
        layoutNavList(node, container);
    }

    private void layoutForArticle(final HelpNode node)
    {

        System.out.println("Turning on info layout");

        infoLayout.setVisibility(View.VISIBLE);

        //navText.setText(node.getLabel());
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

        //TODO: this determine what happens when you click on inline links, currently crashing
        //infoText.setMovementMethod(LinkMovementMethod.getInstance());

        for (final HelpNode child : node.getChildren())
        {
            if (child.getType() == null)
            {
                System.err.println("HelpNode " + child.getId() + " has null data");
                continue;
            }

            System.out.println("See node child : " + child.getType());

            if (child.getType().equals("help-faq-container"))
            {
                info += "<br/><br/><b>" + activity.getString(R.string.related_faq) + ":</b>";

                for (final HelpNode faqChild : child.getChildren())
                {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            }
            else if (child.getType().equals("help-cta"))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                addCtaButton(child);
            }
            else if (child.getType().equals("help-contact-form"))
            {

                ctaLayout.setVisibility(View.VISIBLE);
                contactButton.setVisibility(View.VISIBLE);

                //todo: move this to the fragment
                contactButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        System.out.println("Clicked on contact button");
                        //TODO: Open up a help contact activity with the node info attached

                        Bundle arguments = new Bundle();
                        arguments.putString(BundleKeys.PATH, "fakepathdata");
                        arguments.putParcelable(BundleKeys.HELP_NODE, child);
                        HandyEvent.NavigateToTab navigateEvent = new HandyEvent.NavigateToTab(MainViewTab.HELP_CONTACT, arguments);

                        helpFragment.postForMe(navigateEvent);
                    }
                });

            }
        }


    }








    private void layoutNavList(final HelpNode node, final ViewGroup container)
    {
        infoLayout.setVisibility(View.GONE);
        navOptionsLayout.setVisibility(View.VISIBLE);

        if (node.getType().equals("dynamic-bookings-navigation"))
        {
            setHeaderColor(activity.getResources().getColor(R.color.handy_teal));
        }

        int count = 0;
        int size = node.getChildren().size();

        for (final HelpNode helpNode : node.getChildren())
        {
            final View navView;

            if (helpNode.getType().equals("booking"))
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
                if (node.getType().equals("root"))
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

                if (node.getType().equals("root"))
                {
                    textView.setTextAppearance(activity, R.style.TextView_Large);
                }
            }

            if (count == size - 1)
            {
                navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));
            }

            navView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (helpNode.getType().equals("help-log-in-form"))
                    {
                        //helpFragment.showToast(getString(R.string.please_login);

                        //activity.toast.setText(getString(R.string.please_login));
                        //toast.show();
                    }
                    else
                    {
                        helpFragment.requestNodeData(helpNode);
                    }
                }
            });

            navOptionsLayout.addView(navView);
            count++;
        }
    }

//    private void displayNextNode(final HelpNode node)
//    {
//        if (node.getType().equals("booking"))
//        {
//            //currentBookingId = Integer.toString(node.getId()); //TODO: What is this? It makes no sense
//            bus.post(new HandyEvent.RequestHelpBookingNode(Integer.toString(node.getId()), null));
//        } else
//        {
//            //dataManager.getHelpInfo(Integer.toString(node.getId()), currentBookingId, helpNodeCallback);
//            bus.post(new HandyEvent.RequestHelpNode(Integer.toString(node.getId()), null));
//        }
//    }

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

        System.out.println("Adding cta button");

        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
        final CTAButton ctaButton = (CTAButton) ((ViewGroup) activity.getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);

        ctaButton.initFromHelpNode(node, null); //TODO: Get real login token?

        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection
        ctaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                HashMap<String, String> params = new HashMap<String, String>();
                System.out.println("TODO Clicked on CTA Button Functionality");
            }
        });
    }

}
