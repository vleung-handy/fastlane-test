package com.handy.portal.ui.fragment;

import android.os.Build;
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
import com.handy.portal.ui.view.HelpNodeView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public final class HelpFragment extends ActionBarFragment
{
    private final static String PATH_SEPARATOR = " > ";

    @InjectView(R.id.help_node_view)
    HelpNodeView helpNodeView;
    @InjectView(R.id.scroll_view)
    View scrollView;
    @InjectView(R.id.fetch_error_view)
    View errorView;
    @InjectView(R.id.fetch_error_text)
    TextView errorText;

    private String currentBookingId; //optional param, if help request is associated with a booking
    private String currentPathNodeLabels; //what nodes have we traversed to get to the current node

    private String nodeIdToRequest = null;

    private String title = "";

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        //TODO: Should we bother inflating if we're popping the fragment stack?

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_help_page, container, false);

        ButterKnife.inject(this, view);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) //needed to workaround a bug in android 4.4 that cause webview artifacts to show.
        {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

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

        if (getArguments() != null && getArguments().containsKey(BundleKeys.PATH))
        {
            currentPathNodeLabels = getArguments().getString(BundleKeys.PATH);
        }
        else
        {
            currentPathNodeLabels = "";
        }

        title = "";
        return view;
    }


    @Override
    MainViewTab getTab()
    {
        return MainViewTab.HELP;
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
        //Don't add the root node to the path as per CX spec
        if (!node.getType().equals(HelpNode.HelpNodeType.ROOT))
        {
            currentPathNodeLabels += (!currentPathNodeLabels.isEmpty() ? PATH_SEPARATOR : "") + node.getLabel();
        }
    }

    private void updateActionBar(HelpNode node)
    {
        switch (node.getType())
        {
            case HelpNode.HelpNodeType.ROOT:
            case HelpNode.HelpNodeType.BOOKING:
            {
                title = getResources().getString(R.string.help);
            }
            break;

            case HelpNode.HelpNodeType.NAVIGATION:
            case HelpNode.HelpNodeType.BOOKINGS_NAV:
            case HelpNode.HelpNodeType.ARTICLE:
            {
                title = node.getLabel();
            }
            break;

            default:
            {
                Crashlytics.log("Unrecognized node type : " + node.getType());
            }
            break;
        }
        boolean enabled = !HelpNode.HelpNodeType.ROOT.equals(node.getType());
        setActionBar(title, enabled);
    }

    private void updateDisplay(final HelpNode node)
    {
        updateActionBar(node);
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

    private void setupNavigationListClickListeners(final HelpNode helpNode)
    {
        for (int i = 0; i < helpNode.getChildren().size(); i++)
        {
            final HelpNode childNode = helpNode.getChildren().get(i);
            if (childNode == null || childNode.getType() == null)
            {
                continue;
            }

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
                        arguments.putString(BundleKeys.PATH, currentPathNodeLabels);
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
            for (final HelpNode childNode : helpNode.getChildren())
            {
                if (childNode == null)
                {
                    continue;
                }

                String nodeType = childNode.getType();
                if (nodeType == null)
                {
                    Crashlytics.log("HelpNode " + childNode.getId() + " has null data");
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
                            arguments.putString(BundleKeys.PATH, currentPathNodeLabels);
                            arguments.putParcelable(BundleKeys.HELP_NODE, childNode);
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
