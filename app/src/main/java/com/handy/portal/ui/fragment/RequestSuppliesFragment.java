package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RequestSuppliesFragment extends ActionBarFragment
{
    @InjectView(R.id.fetch_error_view)
    ViewGroup fetchErrorLayout;

    @InjectView(R.id.fetch_error_text)
    TextView fetchErrorText;

    @InjectView(R.id.request_supplies_layout)
    ViewGroup requestSuppliesLayout;

    @InjectView(R.id.request_supplies_button)
    Button requestSuppliesButton;

    @InjectView(R.id.complete_purchase_disclaimer_text)
    TextView completePurchaseDisclaimerText;

    @InjectView(R.id.shipping_address_content_text)
    TextView shippingAddressContentText;

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

        View view = inflater.inflate(R.layout.fragment_request_supplies, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestProviderProfile();
        requestSupplyInfo();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.request_supplies, false);
    }

    @OnClick(R.id.try_again_button)
    public void onTryAgainButtonClicked()
    {
        requestProviderProfile();
        requestSupplyInfo();
    }


    @OnClick(R.id.request_supplies_button)
    public void onRequestSuppliesButtonClicked()
    {
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
        bus.post(new HandyEvent.RequestSendResupplyKit());
    }


    public void requestProviderProfile()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestProviderProfile());
    }

    public void requestSupplyInfo()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestSupplyKitInfo()); //TODO: No one is listening to this yet
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(HandyEvent.ReceiveProviderProfileSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false)); //todo: check if also have the resupply info, or maybe use a non-blocking swirly while supply info is loading?
        mProviderProfile = event.providerProfile;
        processProviderProfile(mProviderProfile);
    }

    @Subscribe
    public void onReceiveProviderProfileError(HandyEvent.ReceiveProviderProfileError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String message = event.error.getMessage();
        if (message == null)
        {
            message = getContext().getString(R.string.unable_to_process_request);
        }
        showToast(message);
    }


    @Subscribe
    public void onReceiveSendResupplyKitSuccess(HandyEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        //TODO : Success transition / pop back to the profile tab
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.REQUEST_SUPPLY_SUCCESS));
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(HandyEvent.ReceiveSendResupplyKitError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String message = event.error.getMessage();
        if (message == null)
        {
            message = getContext().getString(R.string.unable_to_process_request);
        }
        showToast(message);
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
        shippingAddressContentText.setText(address != null ? address.getShippingAddress() : "No Address On File");
    }

    private void processResupplyInfo(ResupplyInfo resupplyInfo)
    {
        String resupplyHelperText = resupplyInfo.getHelperText();
        boolean canRequestSupplies = resupplyInfo.providerCanRequestSupplies();
        boolean canRequestSuppliesNow = resupplyInfo.providerCanRequestSuppliesNow();

        if (canRequestSupplies && canRequestSuppliesNow)
        {
            requestSuppliesButton.setEnabled(true);
        }
        else
        {
            requestSuppliesButton.setEnabled(false);
        }
    }


}
