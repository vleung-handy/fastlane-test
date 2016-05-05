package com.handy.portal.preactivation;

import android.support.annotation.Nullable;

import com.handy.portal.R;

public class PurchaseSuppliesPaymentFragment extends PreActivationSetupStepFragment
{
    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_purchase_supplies_payment;
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
        return getString(R.string.enter_payment_information);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return getString(R.string.wont_charge_until_two_weeks);
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_confirmation);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        goToStep(null);
    }
}
