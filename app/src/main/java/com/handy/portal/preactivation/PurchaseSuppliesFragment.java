package com.handy.portal.preactivation;

import android.os.Bundle;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.view.SimpleContentLayout;
import com.squareup.otto.Subscribe;

import butterknife.Bind;

public class PurchaseSuppliesFragment extends PreActivationFlowFragment
{
    @Bind(R.id.cost_summary)
    SimpleContentLayout mCostSummary;
    @Bind(R.id.delivery_summary)
    SimpleContentLayout mDeliverySummary;
    @Bind(R.id.products_summary)
    SimpleContentLayout mProductsSummary;

    public static PurchaseSuppliesFragment newInstance()
    {
        return new PurchaseSuppliesFragment();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // FIXME: Pull from server
        mCostSummary.setContent("What does the kit cost?",
                "It costs $75, and it\u2019ll be billed to your card.");
        mDeliverySummary.setContent("When will I get it?",
                "It should arrive 1 to 3 business days after you\u2019ve been activated.");
        mProductsSummary.setContent("What does it include?",
                " ⋅ 2 All purpose cleaner\n ⋅ 2 Tub and tile cleaner\n" +
                        " ⋅ Handy apron")
                .collapse(getString(R.string.see_products));
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.purchase_supplies);
    }

    @Override
    protected String getHeaderText()
    {
        return getString(R.string.supply_kit_purchase_inquiry);
    }

    @Override
    protected String getSubHeaderText()
    {
        return null;
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.yes_supply_kit);
    }

    @Override
    protected String getSecondaryButtonText()
    {
        return getString(R.string.no_supply_kit);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        next(PurchaseSuppliesPaymentFragment.newInstance());
    }

    @Override
    protected void onSecondaryButtonClicked()
    {
        // FIXME: Show confirmation slide-up
        showLoadingOverlay();
        bus.post(new HandyEvent.RequestOnboardingSupplies(false));
    }

    @Subscribe
    void onReceiveOnboardingSuppliesSuccess(final HandyEvent.ReceiveOnboardingSuppliesSuccess event)
    {
        hideLoadingOverlay();
        next(null);
    }

    @Subscribe
    void onReceiveOnboardingSuppliesError(final HandyEvent.ReceiveOnboardingSuppliesError event)
    {
        hideLoadingOverlay();
        // FIXME: Show toast
    }
}
