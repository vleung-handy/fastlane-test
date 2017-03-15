package com.handy.portal.core.constant;

import com.handy.portal.bookings.ui.fragment.AvailableBookingsFragment;
import com.handy.portal.bookings.ui.fragment.BookingDetailsWrapperFragment;
import com.handy.portal.bookings.ui.fragment.BookingFragment;
import com.handy.portal.bookings.ui.fragment.CancellationRequestFragment;
import com.handy.portal.bookings.ui.fragment.ScheduledBookingsFragment;
import com.handy.portal.bookings.ui.fragment.SendReceiptCheckoutFragment;
import com.handy.portal.bookings.ui.fragment.SoftwareLicensesFragment;
import com.handy.portal.clients.ui.fragment.ClientsFragment;
import com.handy.portal.core.ui.fragment.AccountSettingsFragment;
import com.handy.portal.core.ui.fragment.EditPhotoFragment;
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
import com.handy.portal.dashboard.fragment.YoutubePlayerFragment;
import com.handy.portal.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.payments.ui.fragment.BookingTransactionsWrapperFragment;
import com.handy.portal.payments.ui.fragment.OutstandingFeesFragment;
import com.handy.portal.payments.ui.fragment.PaymentsDetailFragment;
import com.handy.portal.payments.ui.fragment.PaymentsFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateBankAccountFragment;
import com.handy.portal.payments.ui.fragment.PaymentsUpdateDebitCardFragment;
import com.handy.portal.payments.ui.fragment.SelectPaymentMethodFragment;
import com.handy.portal.proavailability.fragment.EditAvailableHoursFragment;
import com.handy.portal.proavailability.fragment.EditWeeklyAvailableHoursFragment;
import com.handy.portal.webview.PortalWebViewFragment;

import java.io.Serializable;

/**
 * a content page/fragment of this application that will be displayed inside
 * the main content view of MainActivityFragment
 * <p/>
 * easily deeplink-able - see DeeplinkMapper
 */
public enum MainViewPage implements Serializable {
    AVAILABLE_JOBS(AvailableBookingsFragment.class),
    SCHEDULED_JOBS(ScheduledBookingsFragment.class),
    JOB_DETAILS(BookingDetailsWrapperFragment.class),
    JOB_PAYMENT_DETAILS(BookingTransactionsWrapperFragment.class),
    CANCELLATION_REQUEST(CancellationRequestFragment.class),
    NOT_IN_PROGRESS_JOB_DETAILS(BookingFragment.class),
    SEND_RECEIPT_CHECKOUT(SendReceiptCheckoutFragment.class),
    SOFTWARE_LICENSES(SoftwareLicensesFragment.class),

    NOTIFICATIONS(NotificationsFragment.class),

    MORE_ITEMS(MoreNavItemsFragment.class),

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
    PROFILE_PICTURE(EditPhotoFragment.class),

    REFER_A_FRIEND(ReferAFriendFragment.class),

    HELP_WEBVIEW(HelpWebViewFragment.class),

    WEB_PAGE(PortalWebViewFragment.class),

    CLIENTS(ClientsFragment.class),
    EDIT_AVAILABLE_HOURS(EditAvailableHoursFragment.class),
    EDIT_WEEKLY_AVAILABLE_HOURS(EditWeeklyAvailableHoursFragment.class),;

    private static final MainViewPage[] TOP_LEVEL_PAGES = {
            AVAILABLE_JOBS, SCHEDULED_JOBS, CLIENTS, NOTIFICATIONS, DASHBOARD, PAYMENTS,
            REFER_A_FRIEND, ACCOUNT_SETTINGS, DASHBOARD_VIDEO_LIBRARY, HELP_WEBVIEW
    };

    private Class mClassType;

    MainViewPage(Class classType) {
        mClassType = classType;
    }

    public Class getClassType() {
        return mClassType;
    }

    public boolean isTopLevel() {
        for (final MainViewPage page : TOP_LEVEL_PAGES) {
            if (page == this) {
                return true;
            }
        }
        return false;
    }
}
