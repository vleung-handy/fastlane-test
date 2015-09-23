package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.HelpNode;
import com.handy.portal.ui.adapter.HelpNodesAdapter;
import com.handy.portal.ui.layout.SlideUpPanelContainer;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PaymentsFragment extends ActionBarFragment
{
    @InjectView(R.id.slide_up_panel_container)
    SlideUpPanelContainer slideUpPanelContainer;

    private ListView helpNodesListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_payments, null);

        ButterKnife.inject(this, view);

        helpNodesListView = new ListView(getActivity());
        helpNodesListView.setDivider(null);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments, false);
        bus.post(new HandyEvent.RequestHelpPaymentsNode());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_payments, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_update_banking:
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.REFRESH_TAB));
                return true;
            case R.id.action_email_verification:
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                bus.post(new HandyEvent.RequestSendIncomeVerification());
                return true;
            case R.id.action_help:
                slideUpPanelContainer.showPanel(R.string.payment_help, new SlideUpPanelContainer.ContentInitializer()
                {
                    @Override
                    public void initialize(ViewGroup panel)
                    {
                        panel.addView(helpNodesListView);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.ReceiveSendIncomeVerificationSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.PAYMENTS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.ReceiveSendIncomeVerificationError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.send_verification_failed);
    }

    @Subscribe
    public void onReceiveHelpPaymentsNodeSuccess(final HandyEvent.ReceiveHelpPaymentsNodeSuccess event)
    {
        HelpNodesAdapter adapter =
            new HelpNodesAdapter(getActivity(), R.layout.list_item_support_action, event.helpNode.getChildren());
        helpNodesListView.setAdapter(adapter);
        helpNodesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final HelpNode childNode = event.helpNode.getChildren().get(position);
                if (childNode == null || childNode.getType() == null)
                {
                    return;
                }

                Bundle arguments = new Bundle();
                arguments.putString(BundleKeys.HELP_NODE_ID, Integer.toString(childNode.getId()));
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.HELP, arguments));
            }
        });
    }

    @Subscribe
    public void onReceiveHelpPaymentsNodeError(HandyEvent.ReceiveHelpPaymentsNodeError event)
    {
        showToast(R.string.request_payments_help_failed);
    }
}
