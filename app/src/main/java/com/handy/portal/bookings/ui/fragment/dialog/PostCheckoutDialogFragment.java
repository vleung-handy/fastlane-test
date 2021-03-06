package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import com.handy.portal.bookings.model.PostCheckoutResponse;
import com.handy.portal.bookings.model.PostCheckoutSubmission;
import com.handy.portal.bookings.ui.element.PostCheckoutRequestedBookingElementView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.CheckOutFlowLog;
import com.handy.portal.logger.handylogger.model.EventContext;
import com.handy.portal.logger.handylogger.model.EventType;
import com.handy.portal.logger.handylogger.model.JobsLog;
import com.handy.portal.onboarding.model.claim.JobClaim;
import com.handy.portal.onboarding.ui.view.SelectableJobsViewGroup;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;
import com.handy.portal.payments.model.PaymentInfo;

import org.greenrobot.eventbus.EventBus;

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
    @Inject
    DataManager mDataManager;
    @Inject
    ProviderManager mProviderManager;

    @BindView(R.id.post_checkout_scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.post_checkout_scroll_view_content)
    ViewGroup mScrollViewContent;
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
        mBus.post(
                new CheckOutFlowLog(EventType.CUSTOMER_PREFERENCE_SHOWN, mBooking)
        );
    }

    private void initHeader() {
        final PaymentInfo paymentToProvider = mBooking.getPaymentToProvider();
        final PaymentInfo bonusPaymentToProvider = mBooking.getBonusPaymentToProvider();
        final String currencySymbol = paymentToProvider.getCurrencySymbol();
        mBookingAmountText.setText(getString(
                R.string.post_checkout_booking_amount_formatted,
                CurrencyUtils.formatPriceWithoutCents(paymentToProvider.getAmount(), currencySymbol)
        ));
        if (bonusPaymentToProvider != null && bonusPaymentToProvider.getAmount() > 0) {
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
        final int totalPotentialCents = mPostCheckoutInfo.getTotalPotentialCents();
        if (totalPotentialCents > 0) {
            final PaymentInfo paymentToProvider = mBooking.getPaymentToProvider();
            final String htmlString = getString(
                    R.string.post_checkout_claim_prompt_formatted,
                    mPostCheckoutInfo.getCustomer().getFirstName(),
                    CurrencyUtils.formatPriceWithoutCents(
                            totalPotentialCents,
                            paymentToProvider.getCurrencySymbol()
                    )
            );
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                mClaimPromptText.setText(Html.fromHtml(htmlString));
            }
            else {
                mClaimPromptText.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY));
            }
        }
        else {
            mClaimPromptText.setText(getResources().getQuantityString(
                    R.plurals.post_checkout_claim_prompt_default_formatted,
                    mPostCheckoutInfo.getSuggestedJobs().size(),
                    mPostCheckoutInfo.getCustomer().getFirstName()
            ));
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

    @OnClick(R.id.submit_button)
    public void onSubmitButtonClicked() {
        if (mCustomerPreferred != null) {
            final ArrayList<JobClaim> jobClaims = new ArrayList<>();
            final String feedback = mFeedbackEditText.getText().toString();
            if (mCustomerPreferred) {
                final List<Booking> selectedBookings = getSelectedBookings();
                if (!selectedBookings.isEmpty()) {
                    for (Booking booking : selectedBookings) {
                        final String jobType = booking.getType().name().toLowerCase();
                        jobClaims.add(new JobClaim(booking.getId(), jobType));
                    }
                }
            }
            showLoadingOverlay();
            mDataManager.submitPostCheckoutInfo(
                    mBooking.getId(),
                    new PostCheckoutSubmission(mCustomerPreferred, jobClaims, feedback),
                    new FragmentSafeCallback<PostCheckoutResponse>(this) {
                        @Override
                        public void onCallbackSuccess(final PostCheckoutResponse response) {
                            mBus.post(new CheckOutFlowLog.PostCheckoutLog(
                                    EventType.POST_CHECKOUT_SUCCESS,
                                    mBooking,
                                    mCustomerPreferred,
                                    feedback,
                                    jobClaims.size()
                            ));
                            logClaims(response.getClaims());
                            onSubmitPostCheckoutInfoSuccess(response);
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            mBus.post(new CheckOutFlowLog.PostCheckoutLog(
                                    EventType.POST_CHECKOUT_ERROR,
                                    mBooking,
                                    mCustomerPreferred,
                                    feedback,
                                    jobClaims.size()
                            ));
                            onSubmitPostCheckoutInfoError(error);
                        }
                    }
            );
            mBus.post(new CheckOutFlowLog.PostCheckoutLog(
                    EventType.POST_CHECKOUT_SUBMITTED,
                    mBooking,
                    mCustomerPreferred,
                    feedback,
                    jobClaims.size()
            ));
        }
    }

    private void logClaims(final List<BookingClaimDetails> claims) {
        if (claims != null) {
            final String providerId = mProviderManager.getLastProviderId();
            for (final BookingClaimDetails claimDetails : claims) {
                final Booking booking = claimDetails.getBooking();
                if (booking.inferBookingStatus(providerId)
                        == Booking.BookingStatus.CLAIMED) {
                    mBus.post(new JobsLog(EventType.CLAIM_SUCCESS,
                            EventContext.CHECKOUT_FLOW, booking));
                }
                else {
                    mBus.post(new JobsLog(EventType.CLAIM_ERROR,
                            EventContext.CHECKOUT_FLOW, booking));
                }
            }
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
            mBus.post(
                    new CheckOutFlowLog.CustomerPreferenceSelected(mBooking, false)
            );
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
            mBus.post(
                    new CheckOutFlowLog.CustomerPreferenceSelected(mBooking, true)
            );
            mBus.post(new CheckOutFlowLog.UpcomingJobsShown(
                    mBooking,
                    mPostCheckoutInfo.getSuggestedJobs().size(),
                    mPostCheckoutInfo.getTotalPotentialCents() / 100)
            );
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
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScrollViewContent.setMinimumHeight(mHeader.getHeight() + displayMetrics.heightPixels);
        // This delay allows the view height adjustment to take effect before the scrolling
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.smoothScrollTo(0, (int) mCustomerPreferenceSection.getY());
            }
        }, 100);
    }

    private void onSubmitPostCheckoutInfoSuccess(final PostCheckoutResponse response) {
        hideLoadingOverlay();
        UIUtils.showToast(getActivity(), response.getMessage());
        refreshScheduleDates(response);
        dismiss();
    }

    private void refreshScheduleDates(final PostCheckoutResponse response) {
        final List<Date> datesToRefresh = new ArrayList<>();
        if (response.getClaims() != null) {
            for (BookingClaimDetails claimDetails : response.getClaims()) {
                final Booking booking = claimDetails.getBooking();
                datesToRefresh.add(booking.getStartDate());
            }
        }
        if (!datesToRefresh.isEmpty()) {
            mBookingManager.requestScheduledBookings(datesToRefresh, false);
        }
    }

    private void onSubmitPostCheckoutInfoError(final DataManager.DataManagerError error) {
        hideLoadingOverlay();
        final String errorMessage = TextUtils.isEmpty(error.getMessage()) ?
                getString(R.string.an_error_has_occurred) : error.getMessage();
        UIUtils.showToast(getActivity(), errorMessage);
        dismiss();
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
