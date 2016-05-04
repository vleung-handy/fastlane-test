package com.handy.portal.preactivation;

import com.handy.portal.R;

public class PurchaseSuppliesFragment extends PreActivationSetupStepFragment
{
    @Override
    protected int getLayoutResId()
    {
        return R.layout.fragment_pre_activation_purchase_supplies;
    }

    @Override
    protected int getTitleResId()
    {
        return R.string.purchase_supplies;
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        // FIXME: Implement correctly
        goToStep(PreActivationSetupStep.PURCHASE_SUPPLIES);
    }

    @Override
    protected void onSecondaryButtonClicked()
    {
        goToStep(null);
    }
}
