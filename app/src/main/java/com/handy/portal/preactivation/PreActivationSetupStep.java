package com.handy.portal.preactivation;

public enum PreActivationSetupStep
{
    PURCHASE_SUPPLIES(PurchaseSuppliesFragment.class),
    PURCHASE_SUPPLIES_PAYMENT(PurchaseSuppliesPaymentFragment.class),
    PURCHASE_SUPPLIES_CONFIRMATION(PurchaseSuppliesConfirmationFragment.class),
    ;

    private Class<? extends PreActivationSetupStepFragment> mFragmentClass;

    PreActivationSetupStep(final Class<? extends PreActivationSetupStepFragment> fragmentClass)
    {
        mFragmentClass = fragmentClass;
    }

    public static PreActivationSetupStep first()
    {
        return PURCHASE_SUPPLIES;
    }

    public Class<? extends PreActivationSetupStepFragment> getFragmentClass()
    {
        return mFragmentClass;
    }
}
