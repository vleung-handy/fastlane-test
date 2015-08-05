package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.HelpNode;
import com.handy.portal.ui.view.HelpBannerView;
import com.handy.portal.ui.view.HelpNodeView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public final class HelpFragment extends InjectedFragment
{
    private final static String PATH_SEPARATOR = "->";

    @InjectView(R.id.help_node_view)
    HelpNodeView helpNodeView;
    @InjectView(R.id.help_banner_view)
    HelpBannerView helpBannerView;
    @InjectView(R.id.scroll_view)
    View scrollView;
    @InjectView(R.id.fetch_error_view)
    View errorView;
    @InjectView(R.id.fetch_error_text)
    TextView errorText;

    private String currentBookingId; //optional param, if help request is associated with a booking
    private String currentPath; //what nodes have we traversed to get to the current node

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
            currentBookingId = getArguments().getString(BundleKeys.BOOKING_ID);
        }
        else
        {
            currentBookingId = "";
        }

        if (getArguments() != null && getArguments().containsKey(BundleKeys.HELP_NODE_ID))
        {
            nodeIdToRequest = getArguments().getString(BundleKeys.HELP_NODE_ID);
        }

        if (nodeIdToRequest == null)
        {
            helpBannerView.navText.setText(R.string.help);
        }

        if (getArguments() != null && getArguments().containsKey(BundleKeys.PATH))
        {
            currentPath = getArguments().getString(BundleKeys.PATH);
        }
        else
        {
            currentPath = "";
        }

        setupBackClickListener();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!MainActivityFragment.clearingBackStack)
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
        }
    }

    //TODO: Make this smarter and recognize back tracking
    private void trackPath(HelpNode node)
    {
        String nodeId = Integer.toString(node.getId());
        currentPath += (!currentPath.isEmpty() ? PATH_SEPARATOR : "") + nodeId;
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
                setupNavigationListClickListeners(helpNode);
            }
            break;

            case HelpNode.HelpNodeType.ARTICLE:
            {
                setupArticleClickListeners(helpNode);
            }
            break;
        }
    }

    private void setupBackClickListener()
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

    private void setupNavigationListClickListeners(final HelpNode helpNode)
    {
        for (int i = 0; i < helpNode.getChildren().size(); i++)
        {
            final HelpNode childNode = helpNode.getChildren().get(i);
            final View navView = helpNodeView.navOptionsLayout.getChildAt(i);

            navView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    if (childNode.getType().equals(HelpNode.HelpNodeType.LOG_IN_FORM))
                    {
                        showToast(R.string.please_login);
                    }
                    else
                    {
                        Bundle arguments = new Bundle();
                        arguments.putString(BundleKeys.HELP_NODE_ID, Integer.toString(childNode.getId()));
                        arguments.putString(BundleKeys.PATH, currentPath);
                        bus.post(new HandyEvent.NavigateToTab(MainViewTab.HELP, arguments));
                    }
                }
            });
        }
    }

    private void setupArticleClickListeners(final HelpNode helpNode)
    {
        if (helpNode.getChildren().size() > 0)
        {
            for (final HelpNode child : helpNode.getChildren())
            {
                String nodeType = child.getType();
                if (nodeType == null)
                {
                    Crashlytics.log("HelpNode " + child.getId() + " has null data");
                    continue;
                }

                if (nodeType.equals(HelpNode.HelpNodeType.CONTACT))
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

//Event Listeners

    @Subscribe
    public void onReceiveHelpNodeSuccess(HandyEvent.ReceiveHelpNodeSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        scrollView.setVisibility(View.VISIBLE);
        HelpNode helpNode = event.helpNode;
        trackPath(helpNode);
        updateDisplay(helpNode);
    }

    @Subscribe
    public void onReceiveHelpNodeError(HandyEvent.ReceiveHelpNodeError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        scrollView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorText.setText(R.string.error_fetching_connectivity_issue);
    }

    @OnClick(R.id.try_again_button)
    public void doTryAgain()
    {
        errorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestHelpNode(nodeIdToRequest, currentBookingId));
    }
}
