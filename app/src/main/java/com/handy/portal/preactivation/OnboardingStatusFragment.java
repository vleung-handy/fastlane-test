package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.library.ui.view.CollapsibleContentLayout;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;

import butterknife.Bind;

public class OnboardingStatusFragment extends PreActivationFlowFragment
{
    @Bind(R.id.jobs_collapsible)
    CollapsibleContentLayout mJobsCollapsible;
    @Bind(R.id.supplies_collapsible)
    CollapsibleContentLayout mSuppliesCollapsible;
    @Bind(R.id.shipping_view)
    SimpleContentLayout mShippingView;
    @Bind(R.id.payment_view)
    LabelAndValueView mPaymentView;
    @Bind(R.id.order_total_view)
    LabelAndValueView mOrderTotalView;

    public static OnboardingStatusFragment newInstance()
    {
        return new OnboardingStatusFragment();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        displayBookings();
        // FIXME: Parameterize
        mJobsCollapsible.setHeader(R.drawable.ic_onboarding_schedule, "3 Jobs",
                "Tuesday, May 24, 2016");
        mSuppliesCollapsible.setHeader(R.drawable.ic_onboarding_supplies,
                getString(R.string.supply_starter_kit), null);
        // FIXME: Only display if pro opted in
        // FIXME: Pull from server
        mShippingView.setContent("Ship To", "123 Penny Lane\nBrooklyn, NY 11321");
        mPaymentView.setContent("Payment", "Card ending in 1234");
        mOrderTotalView.setContent("Order Total", "$50");

    }

    private void displayBookings()
    {
        final ViewGroup container = mJobsCollapsible.getContentViewContainer();
        container.removeAllViews();
        // FIXME: Don't display pending, display claimed
        for (final Booking booking : getPendingBookings())
        {
            final PendingBookingElementView elementView = new PendingBookingElementView();
            elementView.initView(getActivity(), booking, null, container);
            container.addView(elementView.getAssociatedView());
        }
    }

    @Override
    protected int getButtonType()
    {
        // FIXME: Branch
        return ButtonTypes.NONE;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_onboarding_status;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.welcome);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        // FIXME: Pull from server
        return "Application in review";
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        // FIXME: Pull from server
        return "Take this opportunity to learn about Handy.";
    }

    @Override
    protected void onPrimaryButtonClicked()
    {

    }
}
