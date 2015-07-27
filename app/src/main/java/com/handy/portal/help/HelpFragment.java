package com.handy.portal.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.fragment.InjectedFragment;
import com.handy.portal.ui.widget.CTAButton;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

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

    @InjectView(R.id.help_page_content)
    RelativeLayout helpPageContent;

    @InjectView(R.id.nav_content)
    RelativeLayout navContent;

    private HelpNodeNavView nodeNavView;
    private HelpNodeView nodeView;
    private String currentBookingId;

//    public static HelpFragment newInstance(final HelpNode node,
//                                           final String bookingId,
//                                           final String loginToken,
//                                           final String path)
//    {
//        final HelpFragment fragment = new HelpFragment();
//        final Bundle args = new Bundle();
//        args.putParcelable(EXTRA_HELP_NODE, node);
//        args.putString(EXTRA_BOOKING_ID, bookingId);
//        args.putString(EXTRA_LOGIN_TOKEN, loginToken);
//        args.putString(EXTRA_PATH, path);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    protected List<String> requiredArguments()
//    {
//        List<String> requiredArguments = new ArrayList<>();
//        requiredArguments.add(BundleKeys.BOOKING_ID);
//        requiredArguments.add(BundleKeys.HELP_NODE);
//        requiredArguments.add(BundleKeys.LOGIN_TOKEN);
//        requiredArguments.add(BundleKeys.PATH);
//        return requiredArguments;
//    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_page, container, false);
        ButterKnife.inject(this, view);

        if (getArguments() != null && getArguments().containsKey(BundleKeys.HELP_NODE_ID))
        {
            System.out.println("See override node : " + getArguments().getString(BundleKeys.HELP_NODE_ID));
            bus.post(new HandyEvent.RequestHelpNode(getArguments().getString(BundleKeys.HELP_NODE_ID), null));
        } else
        {
            //null = request root
            bus.post(new HandyEvent.RequestHelpNode(null, null));
        }

        nodeView = new HelpNodeView();
        nodeView.initView(helpPageContent, getActivity());
        nodeNavView = new HelpNodeNavView();
        nodeNavView.initView(navContent, getActivity());

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
        showToast(R.string.error_connectivity);
    }

//    @Subscribe
//    public void onReceiveHelpBookingNodeSuccess(HandyEvent.ReceiveHelpBookingNodeSuccess event)
//    {
//        HelpNode helpNode = event.helpNode;
//        if (helpNode == null)
//        {
//            System.err.println("The booking help node returned from the data was null, didn't parse properly?");
//            return;
//        }
//        constructNodeView(helpNode);
//    }
//
//    @Subscribe
//    public void onReceiveHelpBookingNodeError(HandyEvent.ReceiveHelpBookingNodeError event)
//    {
//        showToast("Error retrieving booking help node " + event.error.getMessage());
//    }


//View Construction

    private void constructNodeView(final HelpNode node)
    {
        //for all but root node add a special back listeners to return to the node
//        if(node.getType() != HelpNode.HelpNodeType.ROOT)
//        {
//            ((BaseActivity) getActivity()).addOnBackPressedListener(new BaseActivity.OnBackPressedListener()
//            {
//                @Override
//                public void onBackPressed()
//                {
//                    //navigate to this help node
//                    bus.post(new HandyEvent.RequestHelpNode(Integer.toString(node.getId()), null));
//                }
//            });
//        }

        nodeNavView.updateDisplay(node);
        nodeView.updateDisplay(node);
        setupClickListeners(node);
    }

    public void requestNodeData(final HelpNode node)
    {
        if (node.getType().equals(HelpNode.HelpNodeType.BOOKING))
        {
            //currentBookingId = Integer.toString(node.getId()); //TODO: What is this? It makes no sense
            bus.post(new HandyEvent.RequestHelpBookingNode(Integer.toString(node.getId()), null));
        } else
        {
            bus.post(new HandyEvent.RequestHelpNode(Integer.toString(node.getId()), null));
        }
    }

    public void setupClickListeners(HelpNode helpNode)
    {
        setupNavigationClickListeners();

        switch (helpNode.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.BOOKING:
            {
                setupNavListClickListeners(helpNode);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                setupArticleClickListeners(helpNode);
            }
            break;
        }
    }

    private void setupNavigationClickListeners()
    {
        nodeNavView.backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getActivity().onBackPressed();
            }
        });

        nodeNavView.closeImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                //TODO: like a super back, clear out all of the help node backs and then hit back a final time
                getActivity().onBackPressed();
            }
        });
    }

    private void setupNavListClickListeners(final HelpNode helpNode)
    {
        for (int i = 0; i < helpNode.getChildren().size(); i++)
        {
            final HelpNode childNode = helpNode.getChildren().get(i);
            final View navView = (View) nodeView.navOptionsLayout.getChildAt(i);

            navView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (childNode.getType().equals(HelpNode.HelpNodeType.LOG_IN_FORM))
                    {
                        showToast(R.string.please_login);
                    } else
                    {
                        //setup the back listener to be able to return to here
                        ((BaseActivity) getActivity()).addOnBackPressedListener(new BaseActivity.OnBackPressedListener()
                        {
                            @Override
                            public void onBackPressed()
                            {
                                //navigate to this help node
                                bus.post(new HandyEvent.RequestHelpNode(Integer.toString(helpNode.getId()), null));
                            }
                        });

                        //Request data for node, will trigger a display
                        requestNodeData(childNode);
                    }
                }
            });
        }
    }


    public void setupArticleClickListeners(HelpNode helpNode)
    {

        if (helpNode.getChildren().size() > 0)
        {
            int ctaButtonIndex = 0;
            for (final HelpNode child : helpNode.getChildren())
            {
                String nodeType = child.getType();
                if (nodeType == null)
                {
                    System.err.println("HelpNode " + child.getId() + " has null data");
                    continue;
                }

                if (child.getType().equals(HelpNode.HelpNodeType.CTA))
                {
                    //TODO: Add the controllers for CTAs when we support them
                    //All the CTA buttons should have been constructed already, we now hook up their functionality
                    final CTAButton ctaButton = (CTAButton) nodeView.ctaLayout.getChildAt(ctaButtonIndex);

                    ctaButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            //TODO: Real CTA functionality, whatever that ends up being
                            HashMap<String, String> params = new HashMap<String, String>();
                            if (currentBookingId != null && !currentBookingId.isEmpty())
                            {
                                //params.put(NavigationManager.PARAM_BOOKING_ID, currentBookingId);
                            }
                            //Boolean success = navigationManager.navigateTo(ctaButton.navigationData, params);
                            //mixpanel.trackEventHelpCenterDeepLinkClicked(Integer.toString(ctaButton.nodeId), ctaButton.nodeLabel);
                        }
                    });

                    ctaButtonIndex++;
                } else if (nodeType.equals(HelpNode.HelpNodeType.CONTACT))
                {
                    nodeView.contactButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Bundle arguments = new Bundle();
                            arguments.putString(BundleKeys.PATH, "fakepathdata"); //TODO: What is the real path data that help contact cares about?
                            arguments.putParcelable(BundleKeys.HELP_NODE, child);
                            HandyEvent.NavigateToTab navigateEvent = new HandyEvent.NavigateToTab(MainViewTab.HELP_CONTACT, arguments);
                            bus.post(navigateEvent);
                        }
                    });
                }
            }
        }
    }
}
