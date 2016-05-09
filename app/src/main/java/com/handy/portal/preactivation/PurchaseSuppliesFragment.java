package com.handy.portal.preactivation;

import android.os.Bundle;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.model.onboarding.OnboardingSuppliesSection;
import com.handy.portal.ui.view.SimpleContentLayout;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;

public class PurchaseSuppliesFragment extends PreActivationFlowFragment
{
    @Bind(R.id.cost_summary)
    SimpleContentLayout mCostSummary;
    @Bind(R.id.delivery_summary)
    SimpleContentLayout mDeliverySummary;
    @Bind(R.id.products_summary)
    SimpleContentLayout mProductsSummary;

    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;

    public static PurchaseSuppliesFragment newInstance(
            final OnboardingSuppliesInfo onboardingSuppliesInfo)
    {
        final PurchaseSuppliesFragment fragment = new PurchaseSuppliesFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.ONBOARDING_SUPPLIES, onboardingSuppliesInfo);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getArguments()
                .getSerializable(BundleKeys.ONBOARDING_SUPPLIES);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final OnboardingSuppliesSection costSection =
                mOnboardingSuppliesInfo.getCostSection();
        final OnboardingSuppliesSection deliverySection =
                mOnboardingSuppliesInfo.getDeliverySection();
        final OnboardingSuppliesSection productsSection =
                mOnboardingSuppliesInfo.getProductsSection();

        mCostSummary.setContent(costSection.getTitle(), costSection.getDescription());
        mDeliverySummary.setContent(deliverySection.getTitle(), deliverySection.getDescription());

        final StringBuilder productsStringBuilder = new StringBuilder();
        final List<String> products = productsSection.getList();
        for (int i = 0; i < products.size(); i++)
        {
            productsStringBuilder.append(" ⋅ ").append(products.get(i));
            if (i < products.size() - 1)
            {
                productsStringBuilder.append("\n");
            }
        }
        mProductsSummary.setContent(productsSection.getTitle(), productsStringBuilder.toString())
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
        next(PurchaseSuppliesPaymentFragment.newInstance(mOnboardingSuppliesInfo));
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
        next(null);
    }
}
