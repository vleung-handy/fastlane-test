package com.handy.portal.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpFragment extends InjectedFragment
{
    private final String STATE_SCROLL_POSITION = "SCROLL_POSITION";
    static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    private static String HELP_CONTACT_FORM_NODE_TYPE = "help-contact-form";
    static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";
    static final String EXTRA_LOGIN_TOKEN = "com.handy.handy.EXTRA_LOGIN_TOKEN";
    static final String EXTRA_PATH = "com.handy.handy.EXTRA_PATH";

//
//    @InjectView(R.id.back_img)
//    ImageView backImage;


    @InjectView(R.id.help_page_content)
    RelativeLayout helpPageContent;

    @InjectView(R.id.nav_content)
    RelativeLayout navContent;


    public static HelpFragment newInstance(final HelpNode node,
                                           final String bookingId,
                                           final String loginToken,
                                           final String path)
    {
        final HelpFragment fragment = new HelpFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_HELP_NODE, node);
        args.putString(EXTRA_BOOKING_ID, bookingId);
        args.putString(EXTRA_LOGIN_TOKEN, loginToken);
        args.putString(EXTRA_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.BOOKING_ID);
        requiredArguments.add(BundleKeys.HELP_NODE);
        requiredArguments.add(BundleKeys.LOGIN_TOKEN);
        requiredArguments.add(BundleKeys.PATH);
        return requiredArguments;
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_page, container, false);

        ButterKnife.inject(this, view);

        bus.post(new HandyEvent.RequestHelpNode(null, null));

       // final Activity linkToActivity = this.getActivity();

//      backImage.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(final View v)
//            {
//                fragmentActivity.onBackPressed();
//            }
//        });
        return view;
    }


//Event Listeners
    @Subscribe
     public void onReceiveHelpNodeSuccess(HandyEvent.ReceiveHelpNodeSuccess event)
    {
        HelpNode helpNode = event.helpNode;
        if (helpNode == null)
        {
            System.err.println("The help node returned from the data was null, didn't parse properly?");
            return;
        }
        constructNodeView(helpNode);
    }

    @Subscribe
    public void onReceiveHelpNodeError(HandyEvent.ReceiveHelpNodeError event)
    {
        //TODO: Hardcoded string
        showToast("Error retrieving help node " + event.error.getMessage());
    }

    @Subscribe
    public void onReceiveHelpBookingNodeSuccess(HandyEvent.ReceiveHelpBookingNodeSuccess event)
    {
        HelpNode helpNode = event.helpNode;
        if (helpNode == null)
        {
            System.err.println("The booking help node returned from the data was null, didn't parse properly?");
            return;
        }
        constructNodeView(helpNode);
    }

    @Subscribe
    public void onReceiveHelpBookingNodeError(HandyEvent.ReceiveHelpBookingNodeError event)
    {
        showToast("Error retrieving booking help node " + event.error.getMessage());
    }





    public void postForMe(HandyEvent.NavigateToTab navigateEvent)
    {
        bus.post(navigateEvent);
    }

//View Construction



    private void constructNodeView(final HelpNode node)
    {
        helpPageContent.removeAllViews();
        navContent.removeAllViews();

        HelpNodeViewConstructor constructor = new HelpNodeViewConstructor();
        constructor.constructView(node, helpPageContent, getActivity(), this);

        HelpNodeNavViewConstructor navViewConstructor = new HelpNodeNavViewConstructor();
        navViewConstructor.constructView(node, navContent, getActivity());

//        //todo: move this to the fragment
//        Button contactButton = (Button) getActivity().findViewById(R.id.contact_button);
//
//        contactButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                System.out.println("Clicked on contact button");
//                //TODO: Open up a help contact activity with the node info attached
//
//                Bundle arguments = new Bundle();
//                arguments.putString(BundleKeys.PATH, "fakepathdata");
//                arguments.putParcelable(BundleKeys.HELP_NODE, child);
//
//                HandyEvent.NavigateToTab navigateEvent = new HandyEvent.NavigateToTab(MainViewTab.HELP_CONTACT, arguments);
//
//
//            }
//        });


    }


    public void requestNodeData(final HelpNode node)
    {
        if (node.getType().equals("booking"))
        {
            //currentBookingId = Integer.toString(node.getId()); //TODO: What is this? It makes no sense
            bus.post(new HandyEvent.RequestHelpBookingNode(Integer.toString(node.getId()), null));
        } else
        {
            //dataManager.getHelpInfo(Integer.toString(node.getId()), currentBookingId, helpNodeCallback);
            bus.post(new HandyEvent.RequestHelpNode(Integer.toString(node.getId()), null));
        }
    }







//    private void addCtaButton(final HelpNode node)
//    {
//        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
//        final CTAButton ctaButton = (CTAButton) ((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);
//        ctaButton.initFromHelpNode(node, currentLoginToken);
//        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection
//        ctaButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(final View v)
//            {
//                HashMap<String, String> params = new HashMap<String, String>();
//                System.out.println("TODO Clicked on CTA Button Functionality");
//            }
//        });
//    }

/*
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
//                        activity.toast.setText(getString(R.string.please_login));
//                        toast.show();
                    }
                    else
                    {
                        //requestNextNode(helpNode);
                    }
                }
            });


            navOptionsLayout.addView(navView);
            count++;
        }
    }
*/





























}
