package com.handy.portal.preactivation;

import com.handy.portal.onboarding.ui.activity.ScheduleBuilderFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                PreActivationFlowActivity.class,
                PurchaseSuppliesFragment.class,
                PurchaseSuppliesPaymentFragment.class,
                PurchaseSuppliesConfirmationFragment.class,
                DeclineSuppliesDialogFragment.class,
                ScheduleBuilderFragment.class,
                SchedulePreferencesFragment.class,
        })
public class PreActivationModule
{
}
