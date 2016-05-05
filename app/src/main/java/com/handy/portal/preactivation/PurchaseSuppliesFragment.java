package com.handy.portal.preactivation;

import com.handy.portal.R;

public class PurchaseSuppliesFragment extends PreActivationSetupStepFragment
{
    @Override
    protected int getLayoutResId()
    {
        return 0;
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
        goToStep(PreActivationSetupStep.PURCHASE_SUPPLIES_PAYMENT);
    }

    @Override
    protected void onSecondaryButtonClicked()
    {
        goToStep(null);
    }
}
