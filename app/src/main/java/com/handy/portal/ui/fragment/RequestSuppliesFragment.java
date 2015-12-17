package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.model.SupplyListItem;
import com.handy.portal.ui.element.SupplyListItemView;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RequestSuppliesFragment extends ActionBarFragment
{
    @InjectView(R.id.request_supplies_button)
    Button mRequestSuppliesButton;

    @InjectView(R.id.shipping_address_line_1_content_text)
    TextView mShippingAddressLine1ContentText;

    @InjectView(R.id.shipping_address_line_2_content_text)
    TextView mShippingAddressLine2ContentText;

    @InjectView(R.id.requested_supplies_list)
    LinearLayout mRequestedSuppliesList;

    @InjectView(R.id.request_supplies_withholding_amount)
    TextView mRequestSuppliesWithholdingAmount;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PROFILE;
    }

    private ProviderProfile mProviderProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final Bundle args = getArguments();
        mProviderProfile = (ProviderProfile) args.getSerializable(BundleKeys.PROVIDER_PROFILE);

        View view = inflater.inflate(R.layout.fragment_request_supplies, container, false);
        ButterKnife.inject(this, view);
        processProviderProfile(mProviderProfile);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.request_supplies, false);
    }

    @OnClick(R.id.request_supplies_button)
    public void onRequestSuppliesButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createResupplyKitConfirmedLog()));
        if(mProviderProfile != null)
        {
            if(mProviderProfile.getResupplyInfo().providerCanRequestSuppliesNow() && mProviderProfile.getProviderPersonalInfo().getAddress() != null)
            {
                requestSendResupplyKit();
            }
        }
    }

    public void requestSendResupplyKit()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProfileEvent.RequestSendResupplyKit());
    }

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(ProfileEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        // Verify with Kenny that this transition is ok; may need to refactor later
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.REQUEST_SUPPLY_SUCCESS));
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(ProfileEvent.ReceiveSendResupplyKitError event)
    {
        this.handleRequestError(event);
    }


    private void processProviderProfile(ProviderProfile providerProfile)
    {
        ProviderPersonalInfo personalInfo = providerProfile.getProviderPersonalInfo();
        Address shippingAddress = personalInfo.getAddress();
        displayShippingAddress(shippingAddress);
        ResupplyInfo resupplyInfo = providerProfile.getResupplyInfo();
        processResupplyInfo(resupplyInfo);
    }

    private void displayShippingAddress(Address address)
    {
        if (address != null)
        {
            mShippingAddressLine1ContentText.setText(address.getStreetAddress());
            mShippingAddressLine2ContentText.setText(address.getCityStateZip());
        }
        else
        {
            mShippingAddressLine1ContentText.setText(R.string.no_address_on_file);
            mShippingAddressLine2ContentText.setText("");
        }
    }

    private void processResupplyInfo(ResupplyInfo resupplyInfo)
    {
        createSupplyList(resupplyInfo.getSupplyList());
        setWithholdingAmountText(resupplyInfo.getWithholdingAmount());

        boolean canRequestSupplies = resupplyInfo.providerCanRequestSupplies();
        boolean canRequestSuppliesNow = resupplyInfo.providerCanRequestSuppliesNow();

        if (canRequestSupplies && canRequestSuppliesNow)
        {
            mRequestSuppliesButton.setEnabled(true);
        }
        else
        {
            mRequestSuppliesButton.setEnabled(false);
        }
    }

    private void createSupplyList(List<SupplyListItem> supplyListItems)
    {
        for (int i = 0; i < supplyListItems.size(); i++)
        {
            SupplyListItem supplyItem = supplyListItems.get(i);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            SupplyListItemView supplyListItemView = (SupplyListItemView) inflater.inflate(R.layout.element_supply_list_item, null);
            supplyListItemView.updateDisplay(supplyItem.getType(), String.valueOf(supplyItem.getAmount()));

            // Add it to the requested supplies list
            mRequestedSuppliesList.addView(supplyListItemView);
        }
    }

    private void setWithholdingAmountText(String withholdingAmount)
    {
        mRequestSuppliesWithholdingAmount.setText(withholdingAmount);
    }

    private void handleRequestError(HandyEvent.ReceiveErrorEvent event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String message = event.error.getMessage();
        if (message == null)
        {
            message = getContext().getString(R.string.unable_to_process_request);
        }
        showToast(message);
    }
}
