package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.bookings.ui.element.PostCheckoutRequestedBookingElementView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.CheckOutFlowLog;
import com.handy.portal.onboarding.model.claim.JobClaim;
import com.handy.portal.onboarding.ui.view.SelectableJobsViewGroup;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;
import com.handy.portal.payments.model.PaymentInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class PostCheckoutDialogFragment extends InjectedDialogFragment
        implements SelectableJobsViewGroup.OnJobCheckedChangedListener {
    public static final String TAG = PostCheckoutDialogFragment.class.getSimpleName();
    private static final String KEY_POST_CHECKOUT_INFO = "post_checkout_info";
    private Booking mBooking;
    private PostCheckoutInfo mPostCheckoutInfo;
    private BookingsWrapperViewModel mBookingsWrapperViewModel;
    private Boolean mCustomerPreferred;

    @Inject
    EventBus mBus;
    @Inject
    BookingManager mBookingManager;

    @BindView(R.id.post_checkout_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.post_checkout_header)
    ViewGroup mHeader;
    @BindView(R.id.post_checkout_customer_preference_section)
    ViewGroup mCustomerPreferenceSection;
    @BindView(R.id.post_checkout_jobs_section)
    ViewGroup mJobsSection;
    @BindView(R.id.post_checkout_feedback_section)
    ViewGroup mFeedbackSection;
    @BindView(R.id.customer_preference_prompt)
    TextView mCustomerPreferencePrompt;
    @BindView(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @BindView(R.id.no_jobs_view)
    ViewGroup mNoJobsView;
    @BindView(R.id.no_jobs_text)
    TextView mNoJobsText;
    @BindView(R.id.claim_prompt_text)
    TextView mClaimPromptText;
    @BindView(R.id.claim_prompt_subtext)
    TextView mClaimPromptSubtext;
    @BindView(R.id.submit_button)
    Button mSubmitButton;
    @BindView(R.id.booking_amount_text)
    TextView mBookingAmountText;
    @BindView(R.id.booking_bonus_amount_text)
    TextView mBookingBonusAmountText;
    @BindView(R.id.feedback_prompt_text)
    TextView mFeedbackPromptText;
    @BindView(R.id.feedback_edit_text)
    EditText mFeedbackEditText;

    public static PostCheckoutDialogFragment newInstance(
            final Booking booking,
            final PostCheckoutInfo postCheckoutInfo
    ) {
        final PostCheckoutDialogFragment dialogFragment = new PostCheckoutDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(KEY_POST_CHECKOUT_INFO, postCheckoutInfo);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mPostCheckoutInfo = (PostCheckoutInfo) getArguments()
                .getSerializable(KEY_POST_CHECKOUT_INFO);
        mBus.register(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation_slide_up_down_from_bottom);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view =
                inflater.inflate(R.layout.fragment_dialog_post_checkout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        updateSubmitButton();
        initHeader();
        initCustomerPreferenceSection();
        initJobsList();
        initFeedbackHeader();
    }

    private void initHeader() {
        final PaymentInfo paymentToProvider = mBooking.getPaymentToProvider();
        final PaymentInfo bonusPaymentToProvider = mBooking.getBonusPaymentToProvider();
        final String currencySymbol = paymentToProvider.getCurrencySymbol();
        mBookingAmountText.setText(getString(
                R.string.post_checkout_booking_amount_formatted,
                CurrencyUtils.formatPriceWithoutCents(paymentToProvider.getAmount(), currencySymbol)
        ));
        if (bonusPaymentToProvider != null) {
            mBookingBonusAmountText.setText(getString(
                    R.string.post_checkout_booking_bonus_amount_formatted,
                    CurrencyUtils.formatPriceWithoutCents(
                            bonusPaymentToProvider.getAmount(),
                            currencySymbol
                    )
            ));
            mBookingBonusAmountText.setVisibility(View.VISIBLE);
        }
    }

    private void initCustomerPreferenceSection() {
        mCustomerPreferencePrompt.setText(getString(
                R.string.post_checkout_preference_prompt_formatted,
                mPostCheckoutInfo.getCustomer().getFirstName()
        ));
    }

    private void initJobsList() {
        mBookingsWrapperViewModel = new BookingsWrapperViewModel(
                mPostCheckoutInfo.getSuggestedJobs(), true);
        if (mBookingsWrapperViewModel.getBookingViewModels().isEmpty()) {
            mNoJobsText.setText(getString(
                    R.string.post_checkout_no_jobs_formatted,
                    mPostCheckoutInfo.getCustomer().getFirstName()
            ));
            mNoJobsView.setVisibility(View.VISIBLE);
        }
        else {
            mJobsContainer.removeAllViews();
            final SelectableJobsViewGroup jobsViewGroup =
                    new SelectableJobsViewGroup(getActivity());
            mJobsContainer.addView(jobsViewGroup);
            jobsViewGroup.setOnJobCheckedChangedListener(this);
            jobsViewGroup.bind(
                    mBookingsWrapperViewModel,
                    PostCheckoutRequestedBookingElementView.class
            );
            mJobsContainer.setVisibility(View.VISIBLE);
            initJobsListTexts();
        }
        onJobCheckedChanged();
    }

    private void initJobsListTexts() {
        final PaymentInfo paymentToProvider = mBooking.getPaymentToProvider();
        final String htmlString = getString(
                R.string.post_checkout_claim_prompt_formatted,
                mPostCheckoutInfo.getCustomer().getFirstName(),
                CurrencyUtils.formatPriceWithoutCents(
                        mPostCheckoutInfo.getTotalPotentialValueCents(),
                        paymentToProvider.getCurrencySymbol()
                )
        );
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mClaimPromptText.setText(Html.fromHtml(htmlString));
        }
        else {
            mClaimPromptText.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY));
        }
        mClaimPromptText.setVisibility(View.VISIBLE);
        mClaimPromptSubtext.setVisibility(View.VISIBLE);
    }

    private void initFeedbackHeader() {
        final String htmlString = getString(R.string.post_checkout_feedback_prompt);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mFeedbackPromptText.setText(Html.fromHtml(htmlString));
        }
        else {
            mFeedbackPromptText.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY));
        }
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);
        super.onDestroy();
    }

    @OnClick(R.id.submit_button)
    public void onSubmitButtonClicked() {
        if (mCustomerPreferred != null) {
            ArrayList<JobClaim> jobClaims = null;
            String feedback = null;
            if (mCustomerPreferred) {
                final List<Booking> selectedBookings = getSelectedBookings();
                if (!selectedBookings.isEmpty()) {
                    jobClaims = new ArrayList<>();
                    for (Booking booking : selectedBookings) {
                        final String jobType = booking.getType().name().toLowerCase();
                        jobClaims.add(new JobClaim(booking.getId(), jobType));
                    }
                }
            }
            else {
                feedback = mFeedbackEditText.getText().toString();
            }
            mBookingManager.sendPostCheckoutInfo(
                    mBooking.getId(), mCustomerPreferred, jobClaims, feedback
            );
            showLoadingOverlay();
        }
    }

    @OnCheckedChanged(R.id.customer_preference_no)
    public void onCustomerPreferenceNoSelected(final boolean checked) {
        if (checked) {
            if (mCustomerPreferred == null) {
                scrollToCustomerPreferenceSection();
            }
            mCustomerPreferred = false;
            mJobsSection.setVisibility(View.GONE);
            mFeedbackSection.setVisibility(View.VISIBLE);
            updateSubmitButton();
        }
    }

    @OnCheckedChanged(R.id.customer_preference_yes)
    public void onCustomerPreferenceYesSelected(final boolean checked) {
        if (checked) {
            UIUtils.dismissKeyboard(mFeedbackEditText);
            if (mCustomerPreferred == null) {
                scrollToCustomerPreferenceSection();
            }
            mCustomerPreferred = true;
            mFeedbackSection.setVisibility(View.GONE);
            mJobsSection.setVisibility(View.VISIBLE);
            updateSubmitButton();
        }
    }

    @OnFocusChange(R.id.feedback_edit_text)
    public void onFeedbackEditTextFocused(final boolean focused) {
        if (focused) {
            UIUtils.showKeyboard(mFeedbackEditText);
            mScrollView.smoothScrollTo(0, (int) mFeedbackSection.getY());
        }
    }

    private void scrollToCustomerPreferenceSection() {
        mScrollView.smoothScrollTo(0, (int) mCustomerPreferenceSection.getY());
    }

    @Subscribe
    public void onReceiveClaimJobsSuccess(final HandyEvent.ReceiveClaimJobsSuccess event) {
        hideLoadingOverlay();
        final int claimedJobsCount = event.getJobClaimResponse().getJobs().size();
        UIUtils.showToast(getActivity(),
                getResources().getQuantityString(R.plurals.claim_jobs_success_formatted,
                        claimedJobsCount));

        final List<Booking> bookings = new ArrayList<>();

        // Logging
        for (BookingClaimDetails claimDetails : event.getJobClaimResponse().getJobs()) {
            final Booking booking = claimDetails.getBooking();
            bookings.add(booking);
            mBus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.ClaimSuccess(booking)));
        }
        if (!bookings.isEmpty()) {
            mBus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.ClaimBatchSuccess(bookings)));
            final List<Date> dates = new ArrayList<>();
            for (Booking booking : bookings) {
                dates.add(booking.getStartDate());
            }
            // Trigger Schedule Refresh
            mBookingManager.requestScheduledBookings(dates, false);
        }

        dismiss();
    }

    @Subscribe
    public void onReceiveClaimJobsError(final HandyEvent.ReceiveClaimJobsError event) {
        hideLoadingOverlay();
        mBus.post(new LogEvent.AddLogEvent(new CheckOutFlowLog.ClaimBatchFailure()));
        UIUtils.showToast(getActivity(), getString(R.string.an_error_has_occurred));
    }

    @Override
    public void onJobCheckedChanged() {
        updateSubmitButton();
    }

    private void updateSubmitButton() {
        if (mCustomerPreferred == null) {
            mSubmitButton.setVisibility(View.GONE);
        }
        else {
            mSubmitButton.setVisibility(View.VISIBLE);
            if (mCustomerPreferred && !mBookingsWrapperViewModel.getBookingViewModels().isEmpty()) {
                final int selectedJobsCount = getSelectedBookings().size();
                if (selectedJobsCount > 0) {
                    mSubmitButton.setText(getResources().getQuantityString(
                            R.plurals.claim_x_jobs_formatted, selectedJobsCount, selectedJobsCount
                    ));
                }
                else {
                    mSubmitButton.setText(R.string.continue_without_claiming);
                }
            }
            else {
                mSubmitButton.setText(R.string.submit_feedback);
            }
        }
    }

    private List<Booking> getSelectedBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        for (BookingViewModel bookingView : mBookingsWrapperViewModel.getBookingViewModels()) {
            if (bookingView.isSelected()) {
                bookings.add(bookingView.getBooking());
            }
        }
        return bookings;
    }
}
