package com.handy.portal.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.ui.activity.LoginActivity;
import com.handy.portal.util.DeeplinkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountSettingsFragment extends ActionBarFragment
{
    @Inject
    EventBus mBus;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.provider_name_text)
    TextView mProviderNameText;
    @Bind(R.id.verification_status_text)
    TextView mVerificationStatusText;
    @Bind(R.id.order_resupply_layout)
    ViewGroup mOrderResupplyLayout;
    @Bind(R.id.account_settings_layout)
    ViewGroup mAccountSettingsLayout;
    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorText;

    private ProviderProfile mProviderProfile;
    private View fragmentView;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.ACCOUNT_SETTINGS;
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
        bus.register(this);

        setActionBar(getString(R.string.account_settings), false);
        populateInfo();
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.contact_info_layout)
    public void switchToProfile()
    {
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.EditProfileSelected()));
        mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.PROFILE_UPDATE, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @OnClick(R.id.edit_payment_option)
    public void switchToPayments()
    {
        mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.SELECT_PAYMENT_METHOD, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
    }

    @OnClick(R.id.order_resupply_layout)
    public void getResupplyKit()
    {
        mBus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitSelected()));

        mBus.post(new NavigationEvent.NavigateToPage(
                MainViewPage.REQUEST_SUPPLIES, new Bundle(), TransitionStyle.NATIVE_TO_NATIVE, true));
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

    @SuppressWarnings("deprecation")
    @OnClick(R.id.log_out_button)
    public void logOut()
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.log_out)
                .setMessage(R.string.are_you_sure_log_out)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //TODO this logout code should be moved somewhere else
                        mPrefsManager.clear();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            CookieManager.getInstance().removeAllCookies(null);
                            CookieManager.getInstance().flush();
                        }
                        else
                        {
                            CookieSyncManager.createInstance(getActivity());
                            CookieManager.getInstance().removeAllCookie();
                            CookieSyncManager.getInstance().sync();
                        }

                        final Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        final Uri data = getActivity().getIntent().getData();
                        final Bundle deeplinkBundle = DeeplinkUtils.createDeeplinkBundleFromUri(data);
                        if (deeplinkBundle != null)
                        {
                            intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkBundle);
                            intent.putExtra(BundleKeys.DEEPLINK_SOURCE, DeeplinkLog.Source.LINK);
                        }
                        startActivity(intent);
                        getActivity().finish();
                        bus.post(new HandyEvent.UserLoggedOut());
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
        mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.ACCOUNT_SETTINGS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
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

        if (mConfigManager.getConfigurationResponse() != null &&
                mConfigManager.getConfigurationResponse().isBoxedSuppliesEnabled())
        { mOrderResupplyLayout.setVisibility(View.VISIBLE); }
    }

    private void requestProviderProfile()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new ProfileEvent.RequestProviderProfile(true));
    }
}
