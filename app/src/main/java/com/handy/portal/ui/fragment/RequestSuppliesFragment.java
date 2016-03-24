package com.handy.portal.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.model.SupplyListItem;
import com.handy.portal.ui.element.SupplyListItemView;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestSuppliesFragment extends ActionBarFragment
{
    @Bind(R.id.request_supplies_button)
    Button mRequestSuppliesButton;

    @Bind(R.id.shipping_address_line_1_content_text)
    TextView mShippingAddressLine1ContentText;

    @Bind(R.id.shipping_address_line_2_content_text)
    TextView mShippingAddressLine2ContentText;

    @Bind(R.id.requested_supplies_list)
    LinearLayout mRequestedSuppliesList;

    @Bind(R.id.request_supplies_withholding_amount)
    TextView mRequestSuppliesWithholdingAmount;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.REQUEST_SUPPLIES;
    }

    private ProviderProfile mProviderProfile;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        final Bundle args = getArguments();
        mProviderProfile = (ProviderProfile) args.getSerializable(BundleKeys.PROVIDER_PROFILE);

        View view = inflater.inflate(R.layout.fragment_request_supplies, container, false);
        ButterKnife.bind(this, view);
        setRequestSuppliesButton();
        processProviderProfile(mProviderProfile);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.request_supplies, false);
        setBackButtonEnabled(true);
    }

    /**
     * this button should be disabled if the required request supplies info is missing
     *
     * see setRequestSuppliesButton() and canProviderRequestSupplies()
     */
    @OnClick(R.id.request_supplies_button)
    public void onRequestSuppliesButtonClicked()
    {
        requestSendResupplyKit();
    }

    public void requestSendResupplyKit()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitRequestSubmitted()));
        bus.post(new ProfileEvent.RequestSendResupplyKit());
    }

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(ProfileEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitRequestConfirmed()));
        // Verify with Kenny that this transition is ok; may need to refactor later
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.REQUEST_SUPPLIES, null, TransitionStyle.REQUEST_SUPPLY_SUCCESS));
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(ProfileEvent.ReceiveSendResupplyKitError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String errorMessage = event.error.getMessage();
        if (errorMessage == null)
        {
            errorMessage = getContext().getString(R.string.unable_to_process_request);
        }
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitRequestError(errorMessage)));
        showToast(errorMessage);
    }

    private void processProviderProfile(ProviderProfile providerProfile)
    {
        if (providerProfile == null
                || providerProfile.getProviderPersonalInfo() == null
                || providerProfile.getResupplyInfo() == null)
        {
            showToast(R.string.error_loading_profile);
            Crashlytics.logException(
                    new Exception("Unable to process provider profile due to null or missing profile info"));
            return;
        }
        ProviderPersonalInfo personalInfo = providerProfile.getProviderPersonalInfo();
        ResupplyInfo resupplyInfo = providerProfile.getResupplyInfo();
        Address shippingAddress = personalInfo.getAddress();
        displayShippingAddress(shippingAddress);
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

    private boolean canProviderRequestSupplies()
    {
        return mProviderProfile != null
                && mProviderProfile.getResupplyInfo() != null
                && mProviderProfile.getResupplyInfo().providerCanRequestSupplies()
                && mProviderProfile.getResupplyInfo().providerCanRequestSuppliesNow()
                && mProviderProfile.getProviderPersonalInfo() != null
                && mProviderProfile.getProviderPersonalInfo().getAddress() != null;
    }

    /**
     * enables/disables the request supplies button as necessary
     */
    private void setRequestSuppliesButton()
    {
        if (canProviderRequestSupplies())
        {
            mRequestSuppliesButton.setEnabled(true);
        }
        else
        {
            //TODO: we should show a message to the user explaining why they can't request supplies
            mRequestSuppliesButton.setEnabled(false);
        }
    }

    private void processResupplyInfo(@NonNull ResupplyInfo resupplyInfo)
    {
        createSupplyList(resupplyInfo.getSupplyList());
        setWithholdingAmountText(resupplyInfo.getWithholdingAmount());
    }

    private void createSupplyList(@NonNull List<SupplyListItem> supplyListItems)
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

}
