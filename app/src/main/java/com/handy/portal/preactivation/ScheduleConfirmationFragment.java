package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.model.JobClaim;
import com.handy.portal.onboarding.model.JobClaimRequest;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;

public class ScheduleConfirmationFragment extends PreActivationFlowFragment
{
    @Bind(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @Bind(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @Bind(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @Bind(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;

    public static ScheduleConfirmationFragment newInstance()
    {
        return new ScheduleConfirmationFragment();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        displayPendingBookings();
        // FIXME: Pull from server
        mShippingView.setContent("Ship To", "123 Penny Lane\nBrooklyn, NY 11321");
        mPaymentView.setContent("Payment", "Card ending in 1234");
        mOrderTotalView.setContent("Order Total", "$50");
    }

    private void displayPendingBookings()
    {
        mJobsContainer.removeAllViews();
        for (final Booking booking : getPendingBookings())
        {
            final PendingBookingElementView elementView = new PendingBookingElementView();
            elementView.initView(getActivity(), booking, null, mJobsContainer);
            mJobsContainer.addView(elementView.getAssociatedView());
        }
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE;
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
        return getString(R.string.ready_to_commit);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return getString(R.string.about_to_claim_and_order);
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.finish_building_schedule);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        showLoadingOverlay();
        final ArrayList<JobClaim> jobClaims = Lists.newArrayList(Collections2.transform(
                getPendingBookings(),
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

    // A success here means the server successfully processed the request. Does not mean all the
    // jobs requested to be claimed were actually claimed (i.e. If I requested 3 jobs, the response
    // can come back: 0 out of 3 claimed).
    @Subscribe
    public void onReceiveClaimJobsSuccess(HandyEvent.ReceiveClaimJobsSuccess event)
    {
        hideLoadingOverlay();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSuccess()));
        // FIXME: Handle no claims, partial claims and full claims properly
        terminate();
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error)
    {
        hideLoadingOverlay();
        final String errorMessage = error.error.getMessage();
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchError(errorMessage)));
        showError(errorMessage);
    }
}
