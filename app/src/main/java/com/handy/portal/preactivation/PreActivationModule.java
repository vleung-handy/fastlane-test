package com.handy.portal.preactivation;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                PreActivationFlowActivity.class,
                PurchaseSuppliesFragment.class,
                PurchaseSuppliesConfirmationFragment.class,
                DeclineSuppliesDialogFragment.class,
        })
public class PreActivationModule
{
}
