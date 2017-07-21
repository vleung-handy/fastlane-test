package com.handy.portal.core.ui.fragment;


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

import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.ProfileEvent;
import com.handy.portal.core.manager.AppseeManager;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.activity.LoginActivity;
import com.handy.portal.deeplink.DeeplinkUtils;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.payments.PaymentEvent;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountSettingsFragment extends ActionBarFragment {
    @Inject
    EventBus mBus;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.provider_name_text)
    TextView mProviderNameText;
    @BindView(R.id.verification_status_text)
    TextView mVerificationStatusText;
    @BindView(R.id.order_resupply_layout)
    ViewGroup mOrderResupplyLayout;
    @BindView(R.id.account_settings_layout)
    ViewGroup mAccountSettingsLayout;
    @BindView(R.id.build_version_text)
    TextView mBuildVersionText;
    @BindView(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;

    private ProviderProfile mProviderProfile;
    private View fragmentView;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.ACCOUNT_SETTINGS;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_account_settings, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        AppseeManager.markViewsAsSensitive(mProviderNameText);
        return fragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBuildVersionText.setText(getString(R.string.build_version_formatted,
                BuildConfig.VERSION_NAME));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        setActionBar(getString(R.string.account_settings), true);
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        requestProviderProfile();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.contact_info_layout)
    public void switchToProfile() {
        bus.post(new ProfileLog.EditProfileSelected());
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.PROFILE_UPDATE, null, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @OnClick(R.id.edit_payment_option)
    public void switchToPayments() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.SELECT_PAYMENT_METHOD, null, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @OnClick(R.id.order_resupply_layout)
    public void getResupplyKit() {
        mBus.post(new ProfileLog.ResupplyKitSelected());
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.REQUEST_SUPPLIES, null, TransitionStyle.NATIVE_TO_NATIVE, true);
    }


    @OnClick(R.id.income_verification_layout)
    public void emailVerification() {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestSendIncomeVerification());
    }

    @OnClick(R.id.try_again_button)
    public void retryProfileFetch() {
        requestProviderProfile();
    }

    @SuppressWarnings("deprecation")
    @OnClick(R.id.log_out_button)
    public void logOut() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.log_out)
                .setMessage(R.string.are_you_sure_log_out)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO this logout code should be moved somewhere else
                        mPrefsManager.clear();
                        mBookingManager.clearCache();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().removeAllCookies(null);
                            CookieManager.getInstance().flush();
                        }
                        else {
                            CookieSyncManager.createInstance(getActivity());
                            CookieManager.getInstance().removeAllCookie();
                            CookieSyncManager.getInstance().sync();
                        }

                        if (mLayerHelper.getLayerClient().isAuthenticated()) {
                            mLayerHelper.deauthenticate();
                        }

                        final Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        final Uri data = getActivity().getIntent().getData();
                        final Bundle deeplinkBundle = DeeplinkUtils.createDeeplinkBundleFromUri(data);
                        if (deeplinkBundle != null) {
                            intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkBundle);
                            intent.putExtra(BundleKeys.DEEPLINK_SOURCE, DeeplinkLog.Source.LINK);
                        }
                        bus.post(new HandyEvent.UserLoggedOut());
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @OnClick(R.id.software_licenses_text)
    public void showSoftwareLicenses() {
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.SOFTWARE_LICENSES, null, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mProviderProfile = event.providerProfile;
        populateInfo();
        mAccountSettingsLayout.setVisibility(View.VISIBLE);
        mFetchErrorView.setVisibility(View.GONE);
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @Subscribe
    public void onGetPaymentFlowSuccess(PaymentEvent.ReceivePaymentFlowSuccess event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        String accountDetails = event.paymentFlow.getAccountDetails();
        if (accountDetails != null) {
            mVerificationStatusText.setText(event.paymentFlow.getStatus());
        }
    }

    @Subscribe
    public void onGetPaymentFlowError(PaymentEvent.ReceivePaymentFlowError event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.ReceiveSendIncomeVerificationSuccess event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.ACCOUNT_SETTINGS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS, false);
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.ReceiveSendIncomeVerificationError event) {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getContext(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }

    private void populateInfo() {
        mBus.post(new PaymentEvent.RequestPaymentFlow());
        if (mProviderProfile.getProviderPersonalInfo() != null) {
            mProviderNameText.setText(mProviderProfile.getProviderPersonalInfo().getFullName());
        }
        if (mConfigManager.getConfigurationResponse().isBoxedSuppliesEnabled()) {
            mOrderResupplyLayout.setVisibility(View.VISIBLE);
        }
    }

    private void requestProviderProfile() {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new ProfileEvent.RequestProviderProfile(true));
    }
}
