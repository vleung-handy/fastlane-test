package com.handy.portal.onboarding.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.model.Designation;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.model.claim.JobClaim;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class ScheduleConfirmationFragment extends OnboardingSubflowUIFragment {
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    BookingManager mBookingManager;

    @BindView(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @BindView(R.id.supplies_container)
    ViewGroup mSuppliesContainer;
    @BindView(R.id.supplies_header)
    ViewGroup mSuppliesHeader;
    @BindView(R.id.edit_supplies_button)
    TextView mEditSuppliesButton;
    @BindView(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @BindView(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @BindView(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;
    private ArrayList<Booking> mPendingBookings;
    private SuppliesOrderInfo mSuppliesOrderInfo;

    @OnClick(R.id.edit_schedule_button)
    public void onEditJobsButtonClicked() {
        redo(SubflowType.CLAIM, RequestCode.ONBOARDING_SUBFLOW);
    }

    @OnClick(R.id.edit_supplies_button)
    public void onEditSuppliesButtonClicked() {
        redo(SubflowType.SUPPLIES, RequestCode.ONBOARDING_SUBFLOW);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ONBOARDING_SUBFLOW) {
            if (resultCode == Activity.RESULT_OK) {
                processPendingBookingsIfAvailable(data);
                processSuppliesOrderInfoIfAvailable(data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processPendingBookingsIfAvailable(final Intent data) {
        final ArrayList<Booking> pendingBookings =
                (ArrayList<Booking>) data.getSerializableExtra(BundleKeys.BOOKINGS);
        if (pendingBookings != null && !pendingBookings.isEmpty()) {
            mPendingBookings = pendingBookings;
            initJobsView();
        }
    }

    private void processSuppliesOrderInfoIfAvailable(final Intent data) {
        final SuppliesOrderInfo suppliesOrderInfo =
                (SuppliesOrderInfo) data.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        if (suppliesOrderInfo != null) {
            mSuppliesOrderInfo = suppliesOrderInfo;
            initSuppliesView();
        }
    }

    public static ScheduleConfirmationFragment newInstance(
            final ArrayList<Booking> pendingBookings,
            final SuppliesOrderInfo suppliesOrderInfo) {
        final ScheduleConfirmationFragment fragment = new ScheduleConfirmationFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKINGS, pendingBookings);
        arguments.putSerializable(BundleKeys.SUPPLIES_ORDER_INFO, suppliesOrderInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPendingBookings = (ArrayList<Booking>) getArguments().getSerializable(BundleKeys.BOOKINGS);
        mSuppliesOrderInfo = (SuppliesOrderInfo) getArguments()
                .getSerializable(BundleKeys.SUPPLIES_ORDER_INFO);
        initJobsView();
        initSuppliesView();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog(
                NativeOnboardingLog.Types.CONFIRMATION_PAGE_SHOWN)));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    private void initJobsView() {
        mJobsContainer.removeAllViews();
        for (final Booking booking : mPendingBookings) {
            final PendingBookingElementView elementView = new PendingBookingElementView();
            elementView.initView(getActivity(), booking, null, mJobsContainer);
            mJobsContainer.addView(elementView.getAssociatedView());
        }
    }

    private void initSuppliesView() {
        if (mSuppliesOrderInfo != null) {
            final Designation designation = mSuppliesOrderInfo.getDesignation();
            mSuppliesHeader.setVisibility(View.VISIBLE);
            if (designation == Designation.YES) {
                mEditSuppliesButton.setText(R.string.edit);
                mShippingView.setContent(getString(R.string.ship_to),
                        mSuppliesOrderInfo.getShippingText());
                populatePaymentViews(mSuppliesOrderInfo.getPaymentText());
                mSuppliesContainer.setVisibility(View.VISIBLE);
            }
            else if (designation == Designation.NO) {
                mEditSuppliesButton.setText(R.string.add);
                mSuppliesContainer.setVisibility(View.GONE);
            }
            else {
                hideSuppliesSection();
            }
        }
        else {
            hideSuppliesSection();
        }
    }

    private void populatePaymentViews(final String paymentText) {
        // If there's no payment information, assume pro is being charged through a supplies fee.
        if (TextUtils.isNullOrEmpty(paymentText)) {
            mPaymentView.setVisibility(View.GONE);
            mOrderTotalView.setContent(getString(R.string.supplies_fee),
                    mSuppliesOrderInfo.getOrderTotalText());
        }
        else {
            mPaymentView.setContent(getString(R.string.payment), paymentText);
            mOrderTotalView.setContent(getString(R.string.order_total),
                    mSuppliesOrderInfo.getOrderTotalText());
        }
    }

    private void hideSuppliesSection() {
        mSuppliesHeader.setVisibility(View.GONE);
        mSuppliesContainer.setVisibility(View.GONE);
    }

    @Override
    protected int getButtonType() {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_schedule_confirmation;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.confirmation);
    }

    @Nullable
    @Override
    protected String getHeaderText() {
        return getString(R.string.ready_to_commit);
    }

    @Nullable
    @Override
    protected String getSubHeaderText() {
        return getString(R.string.double_check_everything);
    }

    @Override
    protected String getPrimaryButtonText() {
        return getString(R.string.finish_application);
    }

    @Override
    protected void onPrimaryButtonClicked() {
        showLoadingOverlay();
        final ArrayList<JobClaim> jobClaims = Lists.newArrayList(Collections2.transform(
                mPendingBookings,
                new Function<Booking, JobClaim>() {
                    @Nullable
                    @Override
                    public JobClaim apply(final Booking booking) {
                        final String bookingId = booking.getId();
                        final String bookingType = booking.getType().name().toLowerCase();
                        return new JobClaim(bookingId, bookingType);
                    }
                }));
        mBookingManager.requestClaimJobs(new JobClaimRequest(jobClaims));
        logConfirmationPageSubmitted();
        bus.post(new NativeOnboardingLog.ClaimBatchSubmitted(mPendingBookings));
    }

    private void logConfirmationPageSubmitted() {
        Boolean suppliesRequested = null;
        if (mSuppliesOrderInfo != null && mSuppliesOrderInfo.getDesignation() != null) {
            final Designation designation = mSuppliesOrderInfo.getDesignation();
            if (designation == Designation.YES) {
                suppliesRequested = true;
            }
            else if (designation == Designation.NO) {
                suppliesRequested = false;
            }
        }
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ConfirmationPageSubmitted(
                mPendingBookings.size(), suppliesRequested)));
    }

    @Subscribe
    public void onReceiveClaimJobsSuccess(HandyEvent.ReceiveClaimJobsSuccess event) {
        hideLoadingOverlay();
        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.ClaimBatchSuccess(mPendingBookings)));
        final List<Booking> claimedBookings =
                logAndGetClaimedBookings(event.getJobClaimResponse().getJobs());
        if (!claimedBookings.isEmpty()) {
            terminate(new Intent());
        }
        else {
            final String errorText = getResources()
                    .getQuantityString(R.plurals.jobs_no_longer_available, mPendingBookings.size());
            showError(errorText, getString(R.string.fix), new ErrorActionOnClickListener() {
                @Override
                public void onClick(final Snackbar snackbar) {
                    onEditJobsButtonClicked();
                }
            }, false);
        }
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error) {
        hideLoadingOverlay();
        final String errorMessage = error.error.getMessage();
        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.ClaimBatchError(mPendingBookings, errorMessage)));
        showError(errorMessage, true);
    }

    // WARNING: This has a event logging side-effect. Be mindful of its usage.
    private List<Booking> logAndGetClaimedBookings(final List<BookingClaimDetails> claimDetailsList) {
        final List<Booking> claimedBookings = new ArrayList<>();
        if (claimDetailsList != null) {
            final String providerId = getProviderId();
            for (final BookingClaimDetails claimDetails : claimDetailsList) {
                final Booking booking = claimDetails.getBooking();
                final Booking.BookingStatus bookingStatus =
                        booking.inferBookingStatus(providerId);
                if (bookingStatus == Booking.BookingStatus.CLAIMED) {
                    claimedBookings.add(booking);
                    bus.post(new LogEvent.AddLogEvent(
                            new NativeOnboardingLog.ClaimSuccess(booking)));
                }
                else {
                    bus.post(new LogEvent.AddLogEvent(
                            new NativeOnboardingLog.ClaimError(booking)));
                }
            }
        }
        return claimedBookings;
    }

    public String getProviderId() {
        return mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
    }
}
