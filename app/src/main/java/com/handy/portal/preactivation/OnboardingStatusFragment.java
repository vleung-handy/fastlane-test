package com.handy.portal.preactivation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.PendingBookingElementView;
import com.handy.portal.library.ui.view.CollapsibleContentLayout;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.Utils;

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
    @Bind(R.id.links_container)
    ViewGroup mLinksContainer;

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
        // FIXME: Use help center links
        addLinkTextView("How Handy Works", "http://facebook.com");
        addLinkTextView("Payments", "http://google.com");
        addLinkTextView("Getting Help", "http://twitter.com");
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
        return ButtonTypes.SINGLE_FIXED;
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

    @Override
    protected int getHeaderImageType()
    {
        return HeaderImageTypes.WELCOME;
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
        next(SchedulePreferencesFragment.newInstance());
    }

    private void addLinkTextView(final String text, @NonNull final String url)
    {
        final TextView view = (TextView) LayoutInflater.from(getActivity())
                .inflate(R.layout.view_link_text, mLinksContainer, false);
        view.setText(text);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.safeLaunchIntent(intent, getActivity());
            }
        });
        mLinksContainer.addView(view);
    }
}
