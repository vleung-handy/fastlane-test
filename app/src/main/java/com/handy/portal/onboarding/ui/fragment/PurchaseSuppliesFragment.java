package com.handy.portal.onboarding.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.view.SimpleContentLayout;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.OnboardingSuppliesLog;
import com.handy.portal.model.Designation;
import com.handy.portal.model.onboarding.OnboardingSuppliesSection;
import com.handy.portal.model.onboarding.SuppliesInfo;
import com.handy.portal.onboarding.model.supplies.SuppliesOrderInfo;

import java.util.List;

import butterknife.Bind;

public class PurchaseSuppliesFragment extends OnboardingSubflowFragment
{
    @Bind(R.id.cost_summary)
    SimpleContentLayout mCostSummary;
    @Bind(R.id.delivery_summary)
    SimpleContentLayout mDeliverySummary;
    @Bind(R.id.products_summary)
    SimpleContentLayout mProductsSummary;

    private SuppliesInfo mSuppliesInfo;

    public static PurchaseSuppliesFragment newInstance()
    {
        final PurchaseSuppliesFragment fragment = new PurchaseSuppliesFragment();
        final Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSuppliesInfo = mSubflowData.getSuppliesInfo();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final OnboardingSuppliesSection costSection =
                mSuppliesInfo.getCostSection();
        final OnboardingSuppliesSection deliverySection =
                mSuppliesInfo.getDeliverySection();
        final OnboardingSuppliesSection productsSection =
                mSuppliesInfo.getProductsSection();

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
                .collapse(getString(R.string.see_products), new Runnable()
                {

                    @Override
                    public void run()
                    {
                        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                                OnboardingSuppliesLog.Types.PRODUCTS_LIST_SHOWN)));
                    }
                });

        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.LANDING_SCREEN_SHOWN)));
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.DOUBLE;
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
        return getString(R.string.do_you_want_supplies);
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
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.PURCHASE_SUPPLIES_SELECTED)));
        next(PurchaseSuppliesConfirmationFragment.newInstance());
    }

    @Override
    protected void onSecondaryButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.DECLINE_SUPPLIES_SELECTED)));
        final DeclineSuppliesDialogFragment fragment = DeclineSuppliesDialogFragment.newInstance();
        fragment.setTargetFragment(this, RequestCode.DECLINE_SUPPLIES);
        FragmentUtils.safeLaunchDialogFragment(fragment, getActivity(), null);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case RequestCode.DECLINE_SUPPLIES:
                    declineSupplies();
                    break;
            }
        }
    }

    private void declineSupplies()
    {
        bus.post(new LogEvent.AddLogEvent(new OnboardingSuppliesLog(
                OnboardingSuppliesLog.Types.DECLINE_SUPPLIES_CONFIRMED)));
        bus.post(new HandyEvent.RequestOnboardingSupplies(false));
        // no need to wait for response
        bus.post(new LogEvent.AddLogEvent(
                new OnboardingSuppliesLog.RequestSupplies.Submitted(false)));

        final Intent data = new Intent();
        final SuppliesOrderInfo suppliesOrderInfo = new SuppliesOrderInfo();
        suppliesOrderInfo.setDesignation(Designation.NO);
        data.putExtra(BundleKeys.SUPPLIES_ORDER_INFO, suppliesOrderInfo);
        terminate(data);
    }
}
