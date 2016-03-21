package com.handy.portal.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.util.UIUtils;
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

    @Bind(R.id.order_resupply_layout)
    ViewGroup mOrderResupplyLayout;
    @Bind(R.id.order_resupply_text)
    TextView mOrderResupplyText;
    @Bind(R.id.order_resupply_helper_text)
    TextView mOrderResupplyHelperText;
    @Bind(R.id.payment_tier_chevron)
    ImageView mPaymentTierChevron;

    @Bind(R.id.account_settings_layout)
    ViewGroup mAccountSettingsLayout;
    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorText;

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

    @OnClick(R.id.contact_info_layout)
    public void switchToProfile()
    {
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileSelected()));
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.PROFILE_UPDATE, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @OnClick(R.id.edit_payment_option)
    public void switchToPayments()
    {
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @OnClick(R.id.order_resupply_layout)
    public void getResupplyKit()
    {
        mBus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitSelected()));

        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_PROFILE, mProviderProfile);
        mBus.post(new NavigationEvent.NavigateToTab(
                MainViewTab.REQUEST_SUPPLIES, args, TransitionStyle.NATIVE_TO_NATIVE, true));
    }


    @OnClick(R.id.income_verification_layout)
    public void emailVerification()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestSendIncomeVerification());
    }

    @OnClick(R.id.try_again_button)
    public void retryProfileFetch()
    {
        requestProviderProfile();
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mAccountSettingsLayout.setVisibility(View.VISIBLE);
        mFetchErrorView.setVisibility(View.GONE);

        mProviderProfile = event.providerProfile;
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mAccountSettingsLayout.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.VISIBLE);
        mFetchErrorText.setText(getString(R.string.error_loading_profile));
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
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.ReceiveSendIncomeVerificationSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.ACCOUNT_SETTINGS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.ReceiveSendIncomeVerificationError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getContext(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }

    private void populateInfo()
    {
        mBus.post(new PaymentEvent.RequestPaymentFlow());

        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null)
        {
            mProviderNameText.setText(provider.getFullName());
        }

        mProviderProfile = mProviderManager.getCachedProviderProfile();
        if (mProviderProfile == null)
        {
            requestProviderProfile();
        }
        else
        {
            final ResupplyInfo resupplyInfo = mProviderProfile.getResupplyInfo();
            if (resupplyInfo != null && resupplyInfo.providerCanRequestSupplies())
            {
                if (!resupplyInfo.providerCanRequestSuppliesNow())
                {
                    disableResupplyOptionWithHelperText(resupplyInfo.getHelperText());
                }
            }
            else
            {
                disableResupplyOption();
            }
        }
    }

    private void requestProviderProfile()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new ProfileEvent.RequestProviderProfile());
    }

    private void disableResupplyOption()
    {
        mOrderResupplyLayout.setVisibility(View.GONE);
    }

    private void disableResupplyOptionWithHelperText(String resupplyHelperText)
    {
        mOrderResupplyLayout.setClickable(false);
        mPaymentTierChevron.setVisibility(View.GONE);

        mOrderResupplyText.setTextColor(
                ContextCompat.getColor(getContext(), R.color.subtitle_grey));
        if (resupplyHelperText != null && !resupplyHelperText.isEmpty())
        {
            mOrderResupplyLayout.setLayoutParams(UIUtils.MATCH_WIDTH_WRAP_HEIGHT_PARAMS);
            mOrderResupplyHelperText.setVisibility(View.VISIBLE);
            mOrderResupplyHelperText.setText(resupplyHelperText);
        }
    }
}
