package com.handy.portal.core;

import com.handy.portal.bookings.ui.fragment.SoftwareLicensesFragment;
import com.handy.portal.core.manager.UrbanAirshipManager;
import com.handy.portal.core.ui.activity.BaseActivity;
import com.handy.portal.core.ui.activity.FragmentContainerActivity;
import com.handy.portal.core.ui.activity.LoginActivity;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.core.ui.activity.ProShareActivity;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.core.ui.element.SupportActionView;
import com.handy.portal.core.ui.fragment.AccountSettingsFragment;
import com.handy.portal.core.ui.fragment.EditPhotoFragment;
import com.handy.portal.core.ui.fragment.LoginActivityFragment;
import com.handy.portal.core.ui.fragment.LoginSltFragment;
import com.handy.portal.core.ui.fragment.MoreNavItemsFragment;
import com.handy.portal.core.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.core.ui.fragment.ReferAFriendFragment;
import com.handy.portal.core.ui.fragment.RequestSuppliesFragment;
import com.handy.portal.core.ui.fragment.RequestSuppliesWebViewFragment;
import com.handy.portal.dashboard.fragment.DashboardFeedbackFragment;
import com.handy.portal.dashboard.fragment.DashboardReviewsFragment;
import com.handy.portal.dashboard.fragment.DashboardTiersFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.dashboard.view.DashboardFeedbackView;
import com.handy.portal.dashboard.view.DashboardOptionsPerformanceView;
import com.handy.portal.dashboard.view.FiveStarRatingPercentageView;
import com.handy.portal.deeplink.DeepLinkService;
import com.handy.portal.onboarding.ui.activity.ActivationWelcomeActivity;
import com.handy.portal.onboarding.ui.activity.FirstDayActivity;
import com.handy.portal.onboarding.ui.fragment.CameraPermissionsBlockerDialogFragment;
import com.handy.portal.onboarding.ui.fragment.IDVerificationFragment;
import com.handy.portal.receiver.HandyPushReceiver;
import com.handy.portal.receiver.LayerPushReceiver;
import com.handy.portal.webview.PortalWebViewClient;
import com.handy.portal.webview.PortalWebViewFragment;
import com.handy.portal.webview.RequestSuppliesWebViewClient;
import com.handy.portal.webview.ShareProviderWebViewFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                LoginActivityFragment.class,
                LoginActivity.class,
                BaseActivity.class,
                SplashActivity.class,
                MainActivity.class,
                UrbanAirshipManager.class,
                DeepLinkService.class,
                PortalWebViewFragment.class,
                RequestSuppliesFragment.class,
                ProfileUpdateFragment.class,
                EditPhotoFragment.class,
                SupportActionView.class,
                DashboardTiersFragment.class,
                DashboardFeedbackFragment.class,
                DashboardReviewsFragment.class,
                DashboardOptionsPerformanceView.class,
                HandyPushReceiver.class,
                AccountSettingsFragment.class,
                RatingsAndFeedbackFragment.class,
                ReferAFriendFragment.class,
                FiveStarRatingPercentageView.class,
                DashboardVideoLibraryFragment.class,
                DashboardFeedbackView.class,
                RequestSuppliesWebViewFragment.class,
                ActivationWelcomeActivity.class,
                RequestSuppliesWebViewFragment.class,
                DashboardTiersFragment.class,
                SoftwareLicensesFragment.class,
                CameraPermissionsBlockerDialogFragment.class,
                IDVerificationFragment.class,
                FirstDayActivity.class,
                LoginSltFragment.class,
                LayerPushReceiver.class,
                PortalWebViewClient.class,
                RequestSuppliesWebViewClient.class,
                MoreNavItemsFragment.class,
                ShareProviderWebViewFragment.class,
                ProShareActivity.class,
                FragmentContainerActivity.class,
        })
public final class InjectionModule {}
