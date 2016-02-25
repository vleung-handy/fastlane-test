package com.handy.portal.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountSettingsFragment extends ActionBarFragment
{
    @Inject
    Bus mBus;
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.provider_name_text)
    TextView mProviderNameText;
    @Bind(R.id.verification_status_text)
    TextView mVerificationStatusText;

    private ProviderProfile mProviderProfile;
    private View fragmentView;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.ACCOUNT_SETTINGS;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null)
        {
            fragmentView = inflater.inflate(R.layout.fragment_account_settings, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(getString(R.string.account_settings), false);

        populateInfo();
    }

    private void populateInfo()
    {
        mBus.post(new PaymentEvent.RequestPaymentFlow());

        Provider provider = mProviderManager.getCachedActiveProvider();
        mProviderProfile = mProviderManager.getCachedProviderProfile();
        if (provider != null)
        {
            mProviderNameText.setText(provider.getFullName());
        }
        if (mProviderProfile == null)
        {
            requestProviderProfile();
        }
    }

    private void requestProviderProfile()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new ProfileEvent.RequestProviderProfile());
    }

    @OnClick(R.id.contact_info_layout)
    public void switchToProfile()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE));
    }

    @OnClick(R.id.edit_payment_option)
    public void switchToPayments()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE));
    }

    @OnClick(R.id.order_resupply_layout)
    public void getResupplyKit()
    {
        mBus.post(new LogEvent.AddLogEvent(
                mEventLogFactory.createResupplyKitSelectedLog()));
        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_PROFILE, mProviderProfile);
        mBus.post(new HandyEvent.NavigateToTab(
                MainViewTab.REQUEST_SUPPLIES, args, TransitionStyle.NATIVE_TO_NATIVE));
    }


    @OnClick(R.id.income_verification_layout)
    public void emailVerification()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestSendIncomeVerification());
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mProviderProfile = event.providerProfile;
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvent.ReceivePaymentFlowSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        String accountDetails = event.paymentFlow.getAccountDetails();
        if (accountDetails != null)
        {
            mVerificationStatusText.setText(event.paymentFlow.getStatus());
        }
    }

    @Subscribe
    public void onGetPaymentFlowError(PaymentEvent.ReceivePaymentFlowError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
//        showToast(R.string.payment_flow_error);
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.ReceiveSendIncomeVerificationSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.ACCOUNT_SETTINGS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.ReceiveSendIncomeVerificationError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getContext(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }
}
