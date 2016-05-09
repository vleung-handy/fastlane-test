package com.handy.portal.preactivation;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                PreActivationFlowActivity.class,
                PurchaseSuppliesFragment.class,
                PurchaseSuppliesPaymentFragment.class,
                PurchaseSuppliesConfirmationFragment.class,
        })
public class PreActivationModule
{
}
