package com.handy.portal.constant;

import android.support.annotation.Nullable;

import com.handy.portal.helpcenter.helpcontact.ui.fragment.HelpContactFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.manager.WebUrlManager;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.ui.fragment.AccountSettingsFragment;
import com.handy.portal.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.ui.fragment.ReferAFriendFragment;
import com.handy.portal.ui.fragment.RequestSuppliesFragment;
import com.handy.portal.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.ui.fragment.booking.CancellationRequestFragment;
import com.handy.portal.ui.fragment.booking.NearbyBookingsFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardFeedbackFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardReviewsFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardTiersFragment;
import com.handy.portal.ui.fragment.dashboard.DashboardVideoLibraryFragment;
import com.handy.portal.ui.fragment.dashboard.RatingsAndFeedbackFragment;
import com.handy.portal.ui.fragment.dashboard.YoutubePlayerFragment;
import com.handy.portal.ui.fragment.payments.PaymentsDetailFragment;
import com.handy.portal.ui.fragment.payments.PaymentsFragment;
import com.handy.portal.ui.fragment.payments.PaymentsUpdateBankAccountFragment;
import com.handy.portal.ui.fragment.payments.PaymentsUpdateDebitCardFragment;
import com.handy.portal.ui.fragment.payments.SelectPaymentMethodFragment;
import com.handy.portal.ui.fragment.profile.ProfileUpdateFragment;
import com.handy.portal.webview.BlockScheduleFragment;
import com.handy.portal.webview.OnboardingFragment;

import java.io.Serializable;

public enum MainViewTab implements Serializable
{
    AVAILABLE_JOBS(AvailableBookingsFragment.class),
    SCHEDULED_JOBS(ScheduledBookingsFragment.class),
    COMPLEMENTARY_JOBS(ComplementaryBookingsFragment.class),
    NEARBY_JOBS(NearbyBookingsFragment.class),
    JOB_DETAILS(BookingDetailsFragment.class),
    CANCELLATION_REQUEST(CancellationRequestFragment.class),
    SEND_RECEIPT_CHECKOUT(SendReceiptCheckoutFragment.class),

    NOTIFICATIONS(NotificationsFragment.class),

    PAYMENTS(PaymentsFragment.class),
    PAYMENTS_DETAIL(PaymentsDetailFragment.class),
    SELECT_PAYMENT_METHOD(SelectPaymentMethodFragment.class),
    UPDATE_BANK_ACCOUNT(PaymentsUpdateBankAccountFragment.class),
    UPDATE_DEBIT_CARD(PaymentsUpdateDebitCardFragment.class),

    DASHBOARD(RatingsAndFeedbackFragment.class),
    DASHBOARD_TIERS(DashboardTiersFragment.class),
    DASHBOARD_REVIEWS(DashboardReviewsFragment.class),
    DASHBOARD_FEEDBACK(DashboardFeedbackFragment.class),
    DASHBOARD_VIDEO_LIBRARY(DashboardVideoLibraryFragment.class),
    YOUTUBE_PLAYER(YoutubePlayerFragment.class),

    ACCOUNT_SETTINGS(AccountSettingsFragment.class),
    REQUEST_SUPPLIES(RequestSuppliesFragment.class),
    PROFILE_UPDATE(ProfileUpdateFragment.class),

    REFER_A_FRIEND(ReferAFriendFragment.class),

    HELP(HelpFragment.class),
    HELP_WEBVIEW(HelpWebViewFragment.class),
    HELP_CONTACT(HelpContactFragment.class),

    BLOCK_PRO_WEBVIEW(BlockScheduleFragment.class, WebUrlManager.BLOCK_JOBS_PAGE),
    ONBOARDING_WEBVIEW(OnboardingFragment.class, WebUrlManager.USES_CONFIG_PARAM_ONBOARDING_PAGE),
    PAYMENT_BLOCKING(PaymentBlockingFragment.class),
    ;

    private Class mClassType;
    private
    @WebUrlManager.TargetPage
    String mWebViewTarget;

    MainViewTab(Class classType)
    {
        mClassType = classType;
    }

    MainViewTab(Class classType, @WebUrlManager.TargetPage String target)
    {
        mClassType = classType;
        mWebViewTarget = target;
    }

    public Class getClassType()
    {
        return mClassType;
    }

    @Nullable
    public
    @WebUrlManager.TargetPage
    String getWebViewTarget()
    {
        return mWebViewTarget;
    }

    //If this gets complex setup small state machines to have a transition for each to/from tab
    public TransitionStyle getDefaultTransitionStyle(MainViewTab targetTab)
    {
        if (this.equals(targetTab))
        {
            return TransitionStyle.REFRESH_TAB;
        }

        if (this.equals(MainViewTab.AVAILABLE_JOBS) && targetTab.equals(MainViewTab.JOB_DETAILS))
        {
            return TransitionStyle.JOB_LIST_TO_DETAILS;
        }

        return TransitionStyle.NATIVE_TO_NATIVE;
    }
}
