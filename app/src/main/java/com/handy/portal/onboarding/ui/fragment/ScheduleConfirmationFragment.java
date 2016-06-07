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
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Designation;
import com.handy.portal.onboarding.model.claim.JobClaim;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class ScheduleConfirmationFragment extends OnboardingSubflowFragment
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @Bind(R.id.supplies_container)
    ViewGroup mSuppliesContainer;
    @Bind(R.id.supplies_header)
    ViewGroup mSuppliesHeader;
    @Bind(R.id.edit_supplies_button)
    TextView mEditSuppliesButton;
    @Bind(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @Bind(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @Bind(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;
    private ArrayList<Booking> mPendingBookings;
    private SuppliesOrderInfo mSuppliesOrderInfo;

    @OnClick(R.id.edit_schedule_button)
    public void onEditJobsButtonClicked()
    {
        redo(SubflowType.CLAIM, RequestCode.ONBOARDING_SUBFLOW);
    }

    @OnClick(R.id.edit_supplies_button)
    public void onEditSuppliesButtonClicked()
    {
        redo(SubflowType.SUPPLIES, RequestCode.ONBOARDING_SUBFLOW);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.ONBOARDING_SUBFLOW)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                processPendingBookingsIfAvailable(data);
                processSuppliesOrderInfoIfAvailable(data);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processPendingBookingsIfAvailable(final Intent data)
    {
        final ArrayList<Booking> pendingBookings =
                (ArrayList<Booking>) data.getSerializableExtra(BundleKeys.BOOKINGS);
        if (pendingBookings != null && !pendingBookings.isEmpty())
        {
            mPendingBookings = pendingBookings;
            initJobsView();
        }
    }

    private void processSuppliesOrderInfoIfAvailable(final Intent data)
    {
        final SuppliesOrderInfo suppliesOrderInfo =
                (SuppliesOrderInfo) data.getSerializableExtra(BundleKeys.SUPPLIES_ORDER_INFO);
        if (suppliesOrderInfo != null)
        {
            mSuppliesOrderInfo = suppliesOrderInfo;
            initSuppliesView();
        }
    }

    public static ScheduleConfirmationFragment newInstance(
            final ArrayList<Booking> pendingBookings,
            final SuppliesOrderInfo suppliesOrderInfo)
    {
        final ScheduleConfirmationFragment fragment = new ScheduleConfirmationFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKINGS, pendingBookings);
        arguments.putSerializable(BundleKeys.SUPPLIES_ORDER_INFO, suppliesOrderInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mPendingBookings = (ArrayList<Booking>) getArguments().getSerializable(BundleKeys.BOOKINGS);
        mSuppliesOrderInfo = (SuppliesOrderInfo) getArguments()
                .getSerializable(BundleKeys.SUPPLIES_ORDER_INFO);
        initJobsView();
        initSuppliesView();
    }

    private void initJobsView()
    {
        mJobsContainer.removeAllViews();
        for (final Booking booking : mPendingBookings)
        {
            final PendingBookingElementView elementView = new PendingBookingElementView();
            elementView.initView(getActivity(), booking, null, mJobsContainer);
            mJobsContainer.addView(elementView.getAssociatedView());
        }
    }

    private void initSuppliesView()
    {
        if (mSuppliesOrderInfo != null)
        {
            final Designation designation = mSuppliesOrderInfo.getDesignation();
            mSuppliesHeader.setVisibility(View.VISIBLE);
            if (designation == Designation.YES)
            {
                mEditSuppliesButton.setText(R.string.edit);
                mShippingView.setContent(getString(R.string.ship_to),
                        mSuppliesOrderInfo.getShippingText());
                mPaymentView.setContent(getString(R.string.payment),
                        mSuppliesOrderInfo.getPaymentText());
                mOrderTotalView.setContent(getString(R.string.order_total),
                        mSuppliesOrderInfo.getOrderTotalText());
                mSuppliesContainer.setVisibility(View.VISIBLE);
            }
            else if (designation == Designation.NO)
            {
                mEditSuppliesButton.setText(R.string.add);
                mSuppliesContainer.setVisibility(View.GONE);
            }
            else
            {
                hideSuppliesSection();
            }
        }
        else
        {
            hideSuppliesSection();
        }
    }

    private void hideSuppliesSection()
    {
        mSuppliesHeader.setVisibility(View.GONE);
        mSuppliesContainer.setVisibility(View.GONE);
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.confirmation);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return mSubflowData.getHeader().getTitle();
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return mSubflowData.getHeader().getDescription();
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.finish_application);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        showLoadingOverlay();
        final ArrayList<JobClaim> jobClaims = Lists.newArrayList(Collections2.transform(
                mPendingBookings,
                new Function<Booking, JobClaim>()
                {
                    @Nullable
                    @Override
                    public JobClaim apply(final Booking booking)
                    {
                        final String bookingId = booking.getId();
                        final String bookingType = booking.getType().name().toLowerCase();
                        return new JobClaim(bookingId, bookingType);
                    }
                }));
        bus.post(new HandyEvent.RequestClaimJobs(new JobClaimRequest(jobClaims)));
    }

    @Subscribe
    public void onReceiveClaimJobsSuccess(HandyEvent.ReceiveClaimJobsSuccess event)
    {
        hideLoadingOverlay();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSuccess()));
        if (hasClaims(event.getJobClaimResponse().getJobs()))
        {
            terminate(new Intent());
        }
        else
        {
            final String errorText = getResources()
                    .getQuantityString(R.plurals.jobs_no_longer_available, mPendingBookings.size());
            showError(errorText, getString(R.string.fix), new ErrorActionOnClickListener()
            {
                @Override
                public void onClick(final Snackbar snackbar)
                {
                    onEditJobsButtonClicked();
                }
            }, false);
        }
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error)
    {
        hideLoadingOverlay();
        final String errorMessage = error.error.getMessage();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchError(errorMessage)));
        showError(errorMessage, true);
    }

    private boolean hasClaims(final List<BookingClaimDetails> claimDetailsList)
    {
        if (claimDetailsList != null)
        {
            final String providerId = getProviderId();
            for (final BookingClaimDetails claimDetails : claimDetailsList)
            {
                final Booking.BookingStatus bookingStatus =
                        claimDetails.getBooking().inferBookingStatus(providerId);
                if (bookingStatus == Booking.BookingStatus.CLAIMED)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String getProviderId()
    {
        return mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }
}
