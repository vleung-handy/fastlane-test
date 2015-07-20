package com.handy.portal.help;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.data.DataManager;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.util.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

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

    //private HelpNode currentNode;
    //private static HelpNode rootNode;
    private String currentBookingId;
    private String currentLoginToken;
    private String path;


    private ViewGroup myContainer;


    @Inject
    Mixpanel mixpanel;

    @Inject
    DataManager dataManager;

    @InjectView(R.id.menu_button_layout)
    ViewGroup menuButtonLayout;
    @InjectView(R.id.nav_text)
    TextView navText;
    @InjectView(R.id.help_header)
    View helpHeader;
    @InjectView(R.id.help_header_title)
    TextView headerTitle;
    @InjectView(R.id.info_text)
    TextView infoText;
    @InjectView(R.id.nav_options_layout)
    LinearLayout navList;
    @InjectView(R.id.info_layout)
    View infoLayout;
    @InjectView(R.id.help_icon)
    ImageView helpIcon;
    @InjectView(R.id.help_triangle)
    ImageView helpTriangleView;
    @InjectView(R.id.cta_layout)
    ViewGroup ctaLayout;
    @InjectView(R.id.contact_button)
    Button contactButton;
    @InjectView(R.id.scroll_view)
    ScrollView scrollView;
    @InjectView(R.id.close_img)
    ImageView closeImage;
    @InjectView(R.id.back_img)
    ImageView backImage;

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
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!validateRequiredArguments())
        {
            System.err.println("Help fragment lacking required arguments can not create properly");
            return;
        }

        System.out.println("Creating help fragment");

        HelpNode overrideNode = getArguments().getParcelable(EXTRA_HELP_NODE);

        if (overrideNode == null)
        {
            //put in a request to the server for the root node
            System.out.println("Don't have an override node, request root");

            //get help info with null arg requests root node
            dataManager.getHelpInfo(null, null, helpNodeCallback);
        } else
        {
            currentBookingId = getArguments().getString(EXTRA_BOOKING_ID);
            currentLoginToken = getArguments().getString(EXTRA_LOGIN_TOKEN);
            path = getArguments().getString(EXTRA_PATH, "");

            if (savedInstanceState == null)
            {
                switch (overrideNode.getType())
                {
                    case "root":
                        //mixpanel.trackEventHelpCenterOpened();
                        break;

                    case "article":
                        //mixpanel.trackEventHelpCenterLeaf(Integer.toString(currentNode.getId()), currentNode.getLabel());
                        break;
                }
            }
        }


    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_page, container, false);

        ButterKnife.inject(this, view);


        myContainer = container;

        if (!validateRequiredArguments())
        {
            System.err.println("Help fragment lacking required arguments can not create view properly");
            //return;
        }

        System.out.println("Creating help fragment view");

        //currentNode = null;
        //currentNode = getArguments().getParcelable(EXTRA_HELP_NODE);


        System.out.println("Don't have any nodes while constructing view, request root");
        dataManager.getHelpInfo(null, null, helpNodeCallback);


//        if(rootNode == null)
//        {
//            //put in a request to the server for the root node
//
//        }
//        else
//        {


/*
        final MenuButton menuButton = new MenuButton(getActivity(), menuButtonLayout);
        menuButtonLayout.addView(menuButton);
*/





        /*
        if (savedInstanceState != null)
        {
            final int[] position = savedInstanceState.getIntArray(STATE_SCROLL_POSITION);
            if (position != null)
            {
                scrollView.post(new Runnable()
                {
                    public void run()
                    {
                        scrollView.scrollTo(position[0], position[1]);
                    }
                });
            }
        }
        */


        //May return to root of help screen without re-downloading root navigation currentNode
//            if (currentNode == null)
//            {
//                currentNode = rootNode;
//            }

        //constructNodeView(container);
        // }


        closeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                /*
                final Intent toHomeScreenIntent = new Intent(getActivity(), ServiceCategoriesActivity.class);
                toHomeScreenIntent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                startActivity(toHomeScreenIntent);
                */
            }
        });


        final Activity fragmentActivity = this.getActivity();

        backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                fragmentActivity.onBackPressed();
            }
        });

        return view;
    }


    private void constructNodeView(final HelpNode node, final ViewGroup container)
    {

        if (node == null)
        {
            System.err.println("Tried to construct while current node is null");
            return;
        }


        System.out.println("Constructing node of type : " + node.getType());

        switch (node.getType())
        {
            case "root":
            {
                //cache the root currentNode so we can navigate back to it from anywhere in our flow
                layoutForRoot(node, container);
                backImage.setVisibility(View.GONE);
            }
            break;

            case "navigation":
            case "dynamic-bookings-navigation":
            case "booking":
            {
                layoutForNavigation(node, container);

                menuButtonLayout.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);

                //((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);
            }
            break;

            case "article":
            {
                layoutForArticle(node);

                menuButtonLayout.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);
                // ((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);

                contactButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        System.out.println("Clicked on contact button");


//                        mixpanel.trackEventHelpCenterNeedHelpClicked(Integer
//                                .toString(currentNode.getId()), currentNode.getLabel());

                        //TODO: Open up a help contact activity with the node info attached
//                        final Intent intent = new Intent(getActivity(), HelpContactActivity.class);
//
//                        for (HelpNode n : node.getChildren())
//                        {
//                            if (n.getType().equals(HELP_CONTACT_FORM_NODE_TYPE))
//                            {
//                                intent.putExtra(HelpContactActivity.EXTRA_HELP_NODE, n);
//                                intent.putExtra(HelpContactActivity.EXTRA_HELP_PATH, path);
//                                break;
//                            }
//                        }
//
//                        startActivity(intent);

                    }
                });

            }
            break;

            default:
            {

                System.err.println("Don't recognize this node type : " + node.getType());

                menuButtonLayout.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);
                //((MenuDrawerActivity) getActivity()).setDrawerDisabled(true);

            }
            break;
        }
    }


    @Override
    public void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);

        /*
        outState.putIntArray(STATE_SCROLL_POSITION,
                new int[]{scrollView.getScrollX(), scrollView.getScrollY()});
                */
    }

    private void layoutForRoot(final HelpNode node, final ViewGroup container)
    {
        closeImage.setVisibility(View.GONE);
        headerTitle.setText(getResources().getString(R.string.what_need_help_with));
        setHeaderColor(getResources().getColor(R.color.handy_blue));
        helpIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_smiley));
        layoutNavList(node, container);
    }

    private void layoutForNavigation(final HelpNode node, final ViewGroup container)
    {
        if (node.getType().equals("booking"))
        {
            navText.setText(getString(R.string.help));
        }
        else
        {
            navText.setText(node.getLabel());
        }
        layoutNavList(node, container);
    }

    private void layoutForArticle(final HelpNode node)
    {
        navText.setText(node.getLabel());
        setHeaderColor(getResources().getColor(R.color.handy_yellow));
        helpIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_help_bulb));
        helpTriangleView.setVisibility(View.VISIBLE);

        String info = node.getContent();

        for (final HelpNode child : node.getChildren())
        {
            if (child.getType() == null)
            {
                System.err.println("HelpNode " + child.getId() + " has null data");
                continue;
            }

            if (child.getType().equals("help-faq-container"))
            {
                info += "<br/><br/><b>" + getString(R.string.related_faq) + ":</b>";

                for (final HelpNode faqChild : child.getChildren())
                {
                    info += "<br/><a href=" + faqChild.getContent() + ">" + faqChild.getLabel() + "</a>";
                }
            } else if (child.getType().equals("help-cta"))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                addCtaButton(child);
            } else if (child.getType().equals("help-contact-form"))
            {
                ctaLayout.setVisibility(View.VISIBLE);
                contactButton.setVisibility(View.VISIBLE);
            }
        }

        infoText.setText(TextUtils.trim(Html.fromHtml(info)));
        infoText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void addCtaButton(final HelpNode node)
    {
        int newChildIndex = ctaLayout.getChildCount(); //new index is equal to the old count since the new count is +1
        final CTAButton ctaButton = (CTAButton) ((ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.fragment_cta_button_template, ctaLayout)).getChildAt(newChildIndex);
        ctaButton.initFromHelpNode(node, currentLoginToken);
        //can't inject into buttons so need to set the on click listener here to take advantage of fragments injection
        ctaButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                HashMap<String, String> params = new HashMap<String, String>();

                System.out.println("TODO Clicked on CTA Button Functionality");

                /*
                if (currentBookingId != null && !currentBookingId.isEmpty())
                {
                    params.put(NavigationManager.PARAM_BOOKING_ID, currentBookingId);
                }
                Boolean success = navigationManager.navigateTo(ctaButton.navigationData, params);
                mixpanel.trackEventHelpCenterDeepLinkClicked(Integer.toString(ctaButton.nodeId), ctaButton.nodeLabel);
                */
            }
        });
    }

    private void layoutNavList(final HelpNode node, final ViewGroup container)
    {
        infoLayout.setVisibility(View.GONE);
        navList.setVisibility(View.VISIBLE);

        if (node.getType().equals("dynamic-bookings-navigation"))
        {
            setHeaderColor(getResources().getColor(R.color.handy_teal));
        }

        int count = 0;
        int size = node.getChildren().size();

        for (final HelpNode helpNode : node.getChildren())
        {
            final View navView;

            if (helpNode.getType().equals("booking"))
            {
                navView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_help_booking_nav, container, false);

                TextView textView = (TextView) navView.findViewById(R.id.service_text);
                textView.setText(helpNode.getService());

                textView = (TextView) navView.findViewById(R.id.date_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "EEEE',' MMMM d"));

                textView = (TextView) navView.findViewById(R.id.time_text);
                textView.setText(TextUtils.formatDate(helpNode.getStartDate(), "h:mmaaa \u2013 ")
                        + TextUtils.formatDecimal(helpNode.getHours(), "#.# ")
                        + getResources().getQuantityString(R.plurals.hour, (int) helpNode.getHours()));
            } else
            {
                if (node.getType().equals("root"))
                {
                    navView = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item_help_nav_main, container, false);
                } else
                {
                    navView = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item_help_nav, container, false);
                }

                final TextView textView = (TextView) navView.findViewById(R.id.nav_item_text);
                textView.setText(helpNode.getLabel());

                if (node.getType().equals("root"))
                {
                    textView.setTextAppearance(getActivity(), R.style.TextView_Large);
                    //textView.setTypeface(TextUtils.get(getActivity(), "CircularStd-Book.otf"));
                }
            }

            if (count == size - 1)
                navView.setBackgroundResource((R.drawable.cell_booking_last_rounded));

            navView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (helpNode.getType().equals("help-log-in-form"))
                    {
                        toast.setText(getString(R.string.please_login));
                        toast.show();
                    } else
                    {
                        displayNextNode(helpNode);
                    }
                }
            });

            navList.addView(navView);
            count++;
        }
    }

    private void displayNextNode(final HelpNode node)
    {

        System.out.println("Display next node ");


//        progressDialog.show();

        /*
        final User user = userManager.getCurrentUser();
        final String authToken = user != null ? user.getAuthToken() : null;

        //Why are we assigning currentBookingId here but we need to wait on the CB for loginToken to come back correctly?
        if (node.getType().equals("booking"))
        {
            currentBookingId = Integer.toString(node.getId());
            dataManager.getHelpBookingsInfo(Integer.toString(node.getId()), authToken, currentBookingId, helpNodeCallback);
        } else
        {
            dataManager.getHelpInfo(Integer.toString(node.getId()), authToken, currentBookingId, helpNodeCallback);
        }
        */

        if (node.getType().equals("booking"))
        {
            currentBookingId = Integer.toString(node.getId());
            dataManager.getHelpBookingsInfo(Integer.toString(node.getId()), currentBookingId, helpNodeCallback);
        } else
        {
            dataManager.getHelpInfo(Integer.toString(node.getId()), currentBookingId, helpNodeCallback);
        }


    }

    private void setHeaderColor(final int color)
    {
        final Drawable header = getResources().getDrawable(R.drawable.help_header_purple);
        header.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            helpHeader.setBackgroundDrawable(header);
        else helpHeader.setBackground(header);
    }

    private DataManager.Callback<HelpNodeWrapper> helpNodeCallback = new DataManager.Callback<HelpNodeWrapper>()
    {
        @Override
        public void onSuccess(final HelpNodeWrapper helpNodeWrapper)
        {

            if (helpNodeWrapper == null)
            {
                System.err.println("The wrapper is null");
                return;
            }

            //HelpNode helpNode = helpNodeWrapper.getHelpNode();


            HelpNode innerHelpNode = helpNodeWrapper.getHelpNode();


//            if (helpNode.getType().equals("article"))
//            {
//                currentLoginToken = helpNode.getLoginToken();
//            }

            System.out.println("Heard back success for help node callback");

//            if (!allowCallbacks) return;
//
//            final Intent intent = new Intent(getActivity(), HelpActivity.class);
//            intent.putExtra(HelpActivity.EXTRA_HELP_NODE, helpNode);
//            intent.putExtra(HelpActivity.EXTRA_BOOKING_ID, currentBookingId);
//            intent.putExtra(HelpActivity.EXTRA_LOGIN_TOKEN, currentLoginToken);
//
//            intent.putExtra(HelpActivity.EXTRA_PATH, path.length() > 0 ? path += " -> "
//                    + helpNode.getLabel() : helpNode.getLabel());


            //startActivity(intent);

            //progressDialog.dismiss();
            //mixpanel.trackEventHelpCenterNavigation(helpNode.getLabel());

            System.out.println("Going to construct node view");

            if (innerHelpNode == null)
            {
                System.err.println("The help node returned from the data was null, didn't parse properly?");
                return;
            }

            System.out.println("See node content " + innerHelpNode.getContent());

            //currentNode = innerHelpNode;
            constructNodeView(innerHelpNode, myContainer);

        }

        @Override
        public void onError(final DataManager.DataManagerError error)
        {
            if (!allowCallbacks) return;

            System.err.println("Error when retrieving help node : " + error.getMessage());

            //progressDialog.dismiss();
            //dataManagerErrorHandler.handleError(getActivity(), error);
        }
    };
}
