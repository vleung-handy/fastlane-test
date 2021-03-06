package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.ui.element.bookings.BookingResultBannerTextView;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.CompletedJobsLog;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsUtil;
import com.handy.portal.payments.model.BookingPaymentReviewRequest;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.PaymentReviewResponse;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.model.Transaction;
import com.handy.portal.payments.ui.element.TransactionView;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportReasonsDialogFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * fragment for handling bookings that are viewed for payment details
 */
public class BookingTransactionsFragment extends ActionBarFragment implements PaymentSupportReasonsDialogFragment.Callback {
    @Inject
    ConfigManager mConfigManager;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    PaymentsManager mPaymentsManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.booking_transactions_banner_text)
    BookingResultBannerTextView mBannerText;
    @BindView(R.id.booking_transactions_location_text)
    TextView mLocationText;
    @BindView(R.id.booking_transactions_date_text)
    TextView mDateText;
    @BindView(R.id.booking_transactions_time_text)
    TextView mTimeText;
    @BindView(R.id.booking_transactions_unassigned_time_text)
    TextView mUnassignedTimeText;
    @BindView(R.id.booking_transactions_unassigned_help_text)
    TextView mUnassignedHelpText;
    @BindView(R.id.booking_transactions_check_in_time_text)
    TextView mCheckInTimeText;
    @BindView(R.id.booking_transactions_check_in_label)
    TextView mCheckInLabelText;
    @BindView(R.id.booking_transactions_late_text)
    TextView mLateText;
    @BindView(R.id.booking_transactions_check_out_time_text)
    TextView mCheckOutTimeText;
    @BindView(R.id.booking_transactions_check_out_label)
    TextView mCheckOutLabelText;
    @BindView(R.id.booking_transactions_check_out_help_text)
    TextView mCheckOutHelpText;
    @BindView(R.id.booking_transactions_transactions_layout)
    ViewGroup mTransactionsLayout;
    @BindView(R.id.booking_transactions_net_earnings_amount_text)
    TextView mNetEarningAmountText;
    @BindView(R.id.booking_transactions_job_number_text)
    TextView mJobNumberText;
    @BindView(R.id.booking_transactions_help_text)
    TextView mHelpText;
    @BindView(R.id.booking_transactions_transactions_summary_layout)
    ViewGroup mTransactionSummary;
    @BindView(R.id.fragment_booking_transactions_payment_support_button)
    Button mPaymentSupportButton;
    @BindView(R.id.fragment_booking_transactions_content)
    CoordinatorLayout mContentLayout;

    private BookingTransactions mBookingTransactions;
    private Booking mBooking;
    private Transaction[] mTransactions;
    // Used to handle link clicked events when parsing html strings for text view
    private TextUtils.LaunchWebViewCallback mLaunchWebViewCallback = new TextUtils.LaunchWebViewCallback() {
        @Override
        public void launchUrl(final String url) {
            Bundle arguments = new Bundle();
            arguments.putString(BundleKeys.TARGET_URL, url);
            mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                    MainViewPage.WEB_PAGE, arguments, TransitionStyle.NATIVE_TO_NATIVE, true);

            bus.post(new CompletedJobsLog.HelpClicked(mBooking));
        }
    };

    public static BookingTransactionsFragment newInstance(
            @NonNull final BookingTransactions bookingTransactions) {
        BookingTransactionsFragment fragment = new BookingTransactionsFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING_TRANSACTIONS, bookingTransactions);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookingTransactions = (BookingTransactions)
                getArguments().getSerializable(BundleKeys.BOOKING_TRANSACTIONS);
        if (mBookingTransactions == null) {
            Crashlytics.log("Either booking or transactions is null in onReceiveBookingDetailsSuccess");
            return;
        }
        mBooking = mBookingTransactions.getBooking();
        mTransactions = mBookingTransactions.getTransactions();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_transactions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBarTitle(R.string.job_details);
        setDisplay();
    }

    private void setDisplay() {
        mLocationText.setText(mBooking.getRegionName());
        mDateText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));
        String startTime = DateTimeUtils.getTimeWithoutDate(mBooking.getStartDate());
        String endTime = DateTimeUtils.getTimeWithoutDate(mBooking.getEndDate());
        mTimeText.setText(getString(R.string.dash_formatted, startTime, endTime));
        if (mBooking.getCheckInTime() != null) {
            mCheckInTimeText.setText(DateTimeUtils.getTimeWithoutDate(mBooking.getCheckInTime()));
        }
        else {
            mCheckInLabelText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_light_gray));
        }
        if (mBooking.getCheckOutTime() != null) {
            mCheckOutTimeText.setText(DateTimeUtils.getTimeWithoutDate(mBooking.getCheckOutTime()));
        }
        else {
            mCheckOutLabelText.setTextColor(ContextCompat.getColor(getContext(), R.color.text_light_gray));
        }

        for (Transaction t : mTransactions) {
            TransactionView transactionView = new TransactionView(getContext());
            transactionView.setDisplay(t, mLaunchWebViewCallback);
            mTransactionsLayout.addView(transactionView);
        }
        mNetEarningAmountText.setText(CurrencyUtils.formatPriceWithCents(
                mBookingTransactions.getNetEarnings(), mBookingTransactions.getCurrencySymbol()));
        if (mBookingTransactions.getNetEarnings() < 0) {
            mNetEarningAmountText.setTextColor(ContextCompat.getColor(getContext(), R.color.plumber_red));
        }

        mJobNumberText.setText(getString(R.string.job_number_formatted, mBooking.getId()));

        mHelpText.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.partner_blue));
        mHelpText.setMovementMethod(LinkMovementMethod.getInstance());
        TextUtils.setTextViewHTML(mHelpText, getString(R.string.question_about_payment), mLaunchWebViewCallback);

        if (mConfigManager.getConfigurationResponse() == null ||
                !mConfigManager.getConfigurationResponse().showBookingTransactionSummary()) {
            mTransactionSummary.setVisibility(View.GONE);
        }

        //don't show payment support button if no payment support items
        mPaymentSupportButton.setVisibility(
                mBookingTransactions.getPaymentSupportItems() == null
                        || mBookingTransactions.getPaymentSupportItems().length == 0 ?
                        View.GONE : View.VISIBLE
        );
    }

    @OnClick(R.id.fragment_booking_transactions_payment_support_button)
    public void onPaymentSupportButtonClicked() {
        PaymentsUtil.showPaymentSupportReasonsDialog(this, mBookingTransactions.getPaymentSupportItems());
    }

    @Override
    public void onPaymentSupportItemSubmitted(PaymentSupportItem paymentSupportItem) {
        BookingPaymentReviewRequest paymentReviewRequest
                = new BookingPaymentReviewRequest(
                String.valueOf(mBooking.getId()),
                mBooking.getType().toString().toLowerCase(),
                paymentSupportItem.getMachineName(),
                null);

        bus.post(
                new PaymentsLog.BookingTransaction.SupportDialogSubmitButtonPressed(
                        mBooking.getId(),
                        paymentSupportItem.getMachineName(),
                        null
                ));

        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mPaymentsManager.submitBookingPaymentReviewRequest(paymentReviewRequest, new FragmentSafeCallback<PaymentReviewResponse>(this) {
            @Override
            public void onCallbackSuccess(final PaymentReviewResponse response) {
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                int drawableResourceId = response.isSuccess() ?
                        R.drawable.ic_green_envelope : R.drawable.ic_exclaimation_red;
                UIUtils.getDefaultSnackbarWithImage(getContext(),
                        mContentLayout,
                        response.getMessage(),
                        drawableResourceId).show();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                UIUtils.getDefaultSnackbarWithImage(getContext(),
                        mContentLayout,
                        getString(R.string.an_error_has_occurred),
                        R.drawable.ic_exclaimation_red).show();
            }
        });
    }
}
