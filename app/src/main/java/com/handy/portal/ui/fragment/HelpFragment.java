package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.HelpNode;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.view.HelpBannerView;
import com.handy.portal.ui.view.HelpNodeView;
import com.handy.portal.ui.widget.CTAButton;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class HelpFragment extends InjectedFragment
{
    private final static String PATH_SEPARATOR = "->";

    @InjectView(R.id.help_node_view)
    HelpNodeView helpNodeView;

    @InjectView(R.id.help_banner_view)
    HelpBannerView helpBannerView;

    private String currentBookingId; //optional param, if help request is associated with a booking
    private String currentPath; //what nodes have we traversed to get to the current node

    private String lastNodeId = "";

    private String nodeIdToRequest = null;


    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        //TODO: Should we bother inflating if we're popping the fragment stack?

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_page, container, false);

        ButterKnife.inject(this, view);

        if (getArguments() != null && getArguments().containsKey(BundleKeys.BOOKING_ID))
        {
            this.currentBookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        }
        else
        {
            this.currentBookingId = "";
        }

        this.nodeIdToRequest = null; //default is to request null which will get us our root node

        if(false) //in the future we may want to remember the last node we saw
        {
            this.nodeIdToRequest = lastNodeId;
            lastNodeId = null;
        }

        if (getArguments() != null && getArguments().containsKey(BundleKeys.HELP_NODE_ID))
        {
            this.nodeIdToRequest = getArguments().getString(BundleKeys.HELP_NODE_ID);
        }

        currentPath = "";

        setupNavigationClickListeners();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!MainActivityFragment.clearingBackStack)
        {
            bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, this.currentBookingId));
        }
    }


    //TODO: Make this smarter and recognize back tracking
    private void trackPath(HelpNode node)
    {
        String nodeId = Integer.toString(node.getId());
        currentPath +=  (!currentPath.isEmpty() ? PATH_SEPARATOR : "" ) + nodeId;
    }

    private void updateDisplay(final HelpNode node)
    {
        helpBannerView.updateDisplay(node);
        helpNodeView.updateDisplay(node);
        setupClickListeners(node);
    }

    private void setupClickListeners(HelpNode helpNode)
    {
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
        helpBannerView.backImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                getActivity().onBackPressed();
            }
        });
    }

    private void setupNavListClickListeners(final HelpNode helpNode)
    {
        for (int i = 0; i < helpNode.getChildren().size(); i++)
        {
            final HelpNode childNode = helpNode.getChildren().get(i);
            final View navView = (View) helpNodeView.navOptionsLayout.getChildAt(i);

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
                        //Request data for node, will trigger a display when it returns
                        requestNodeData(childNode);
                    }
                }
            });
        }
    }

    private void setupArticleClickListeners(final HelpNode helpNode)
    {
        if (helpNode.getChildren().size() > 0)
        {
            int ctaButtonIndex = 0;
            for (final HelpNode child : helpNode.getChildren())
            {
                String nodeType = child.getType();
                if (nodeType == null)
                {
                    Crashlytics.log("HelpNode " + child.getId() + " has null data");
                    continue;
                }

                if (child.getType().equals(HelpNode.HelpNodeType.CTA))
                {
                    //TODO: Add the controllers for CTAs when we support them
                    //All the CTA buttons should have been constructed already, we now hook up their functionality
                    final CTAButton ctaButton = (CTAButton) helpNodeView.ctaLayout.getChildAt(ctaButtonIndex);

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
                    helpNodeView.contactButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Bundle arguments = new Bundle();
                            arguments.putString(BundleKeys.PATH, currentPath);
                            arguments.putParcelable(BundleKeys.HELP_NODE, helpNode);
                            HandyEvent.NavigateToTab navigateEvent = new HandyEvent.NavigateToTab(MainViewTab.HELP_CONTACT, arguments);
                            bus.post(navigateEvent);
                        }
                    });
                }
            }
        }
    }

    private void requestNodeData(final HelpNode node)
    {
        if (node.getType().equals(HelpNode.HelpNodeType.BOOKING))
        {
            currentBookingId = Integer.toString(node.getId()); //TODO: What is this? Does this make sense? It seems odd.....
            bus.post(new HandyEvent.RequestHelpBookingNode(Integer.toString(node.getId()), null));
        } else
        {
            bus.post(new HandyEvent.RequestHelpNode(Integer.toString(node.getId()), null));
        }
    }

//Event Listeners

    @Subscribe
    public void onReceiveHelpNodeSuccess(HandyEvent.ReceiveHelpNodeSuccess event)
    {
        HelpNode helpNode = event.helpNode;
        lastNodeId = Integer.toString(helpNode.getId());
        trackPath(helpNode);
        updateDisplay(helpNode);
    }

    @Subscribe
    public void onReceiveHelpNodeError(HandyEvent.ReceiveHelpNodeError event)
    {
        showToast(R.string.error_connectivity);
    }
}
