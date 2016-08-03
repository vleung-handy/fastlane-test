package com.handy.portal.constant;

import android.support.annotation.Nullable;

import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.bookings.ui.fragment.BookingFragment;
import com.handy.portal.bookings.ui.fragment.CancellationRequestFragment;
import com.handy.portal.bookings.ui.fragment.ComplementaryBookingsFragment;
import com.handy.portal.bookings.ui.fragment.NearbyBookingsFragment;
import com.handy.portal.bookings.ui.fragment.ProRequestedJobsFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.SoftwareLicensesFragment;
import com.handy.portal.dashboard.fragment.DashboardFeedbackFragment;
import com.handy.portal.dashboard.fragment.DashboardReviewsFragment;
import com.handy.portal.dashboard.fragment.DashboardTiersFragment;
import com.handy.portal.dashboard.fragment.DashboardVideoLibraryFragment;
import com.handy.portal.dashboard.fragment.RatingsAndFeedbackFragment;
import com.handy.portal.dashboard.fragment.YoutubePlayerFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.manager.WebUrlManager;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.payments.ui.fragment.BookingTransactionsWrapperFragment;
import com.handy.portal.payments.ui.fragment.OutstandingFeesFragment;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateBankAccountFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateDebitCardFragment;
import com.handy.portal.payments.ui.fragment.SelectPaymentMethodFragment;
import com.handy.portal.ui.fragment.AccountSettingsFragment;
import com.handy.portal.ui.fragment.ProfileUpdateFragment;
import com.handy.portal.ui.fragment.ReferAFriendFragment;
import com.handy.portal.ui.fragment.RequestSuppliesFragment;
import com.handy.portal.ui.fragment.RequestSuppliesWebViewFragment;
import com.handy.portal.webview.BlockScheduleFragment;
import com.handy.portal.webview.PortalWebViewFragment;

import java.io.Serializable;

/**
 * a content page/fragment of this application that will be displayed inside
 * the main content view of MainActivityFragment
 * <p/>
 * easily deeplink-able - see DeeplinkMapper
 */
public enum MainViewPage implements Serializable
{
    AVAILABLE_JOBS(AvailableBookingsFragment.class),
    SCHEDULED_JOBS(ScheduledBookingsFragment.class),
    COMPLEMENTARY_JOBS(ComplementaryBookingsFragment.class),
    NEARBY_JOBS(NearbyBookingsFragment.class),
    JOB_DETAILS(BookingDetailsWrapperFragment.class),
    JOB_PAYMENT_DETAILS(BookingTransactionsWrapperFragment.class),
    CANCELLATION_REQUEST(CancellationRequestFragment.class),
    NOT_IN_PROGRESS_JOB_DETAILS(BookingFragment.class),
    SEND_RECEIPT_CHECKOUT(SendReceiptCheckoutFragment.class),
    SOFTWARE_LICENSES(SoftwareLicensesFragment.class),

    NOTIFICATIONS(NotificationsFragment.class),

    PAYMENTS(PaymentsFragment.class),
    OUTSTANDING_FEES(OutstandingFeesFragment.class),
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
    REQUEST_SUPPLIES_WEB_VIEW(RequestSuppliesWebViewFragment.class),
    PROFILE_UPDATE(ProfileUpdateFragment.class),

    REFER_A_FRIEND(ReferAFriendFragment.class),

    HELP_WEBVIEW(HelpWebViewFragment.class),

    WEB_PAGE(PortalWebViewFragment.class),

    BLOCK_PRO_WEBVIEW(BlockScheduleFragment.class, WebUrlManager.BLOCK_JOBS_PAGE),
    PAYMENT_BLOCKING(PaymentBlockingFragment.class),

    REQUESTED_JOBS(ProRequestedJobsFragment.class);

    private static final MainViewPage[] TOP_LEVEL_PAGES = {
            AVAILABLE_JOBS, SCHEDULED_JOBS, REQUESTED_JOBS, NOTIFICATIONS, DASHBOARD, PAYMENTS,
            REFER_A_FRIEND, ACCOUNT_SETTINGS, DASHBOARD_VIDEO_LIBRARY, HELP_WEBVIEW
    };
    private Class mClassType;
    private
    @WebUrlManager.TargetPage
    String mWebViewTarget;

    MainViewPage(Class classType)
    {
        mClassType = classType;
    }

    MainViewPage(Class classType, @WebUrlManager.TargetPage String target)
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

    public boolean isTopLevel()
    {
        for (final MainViewPage page : TOP_LEVEL_PAGES)
        {
            if (page == this)
            {
                return true;
            }
        }
        return false;
    }
}
