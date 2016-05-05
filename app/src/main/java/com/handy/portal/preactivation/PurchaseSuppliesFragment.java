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
    protected String getTitle()
    {
        return getString(R.string.purchase_supplies);
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
        // FIXME: Implement correctly
        goToStep(PreActivationSetupStep.PURCHASE_SUPPLIES);
    }

    @Override
    protected void onSecondaryButtonClicked()
    {
        goToStep(null);
    }
}
