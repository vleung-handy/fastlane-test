package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.ui.view.SimpleContentLayout;

import butterknife.Bind;

public class PurchaseSuppliesConfirmationFragment extends PreActivationSetupStepFragment
{
    @Bind(R.id.shipping_summary)
    SimpleContentLayout mShippingSummary;
    @Bind(R.id.payment_summary)
    SimpleContentLayout mPaymentSummary;
    @Bind(R.id.order_summary)
    SimpleContentLayout mOrderSummary;

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // FIXME: Pull from server
        mShippingSummary.setContent(getString(R.string.shipping_address),
                "John Doe\n123 Penny Lane\nBrooklyn, NY 12345");
        // FIXME: Pull from arguments
        mPaymentSummary.setContent(getString(R.string.payment_method),
                "Visa ending in 1234");
        // FIXME: Pull form server
        final String orderTotalFormatted = getString(R.string.order_total_formatted, "$75");
        mOrderSummary.setContent(getString(R.string.supply_starter_kit), orderTotalFormatted)
                .setImage(getResources().getDrawable(R.drawable.img_supplies));
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.payment);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.confirm_your_purchase);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return null;
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.confirm_purchase);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        goToStep(null);
    }
}
