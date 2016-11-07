package com.handy.portal.onboarding;

import com.handy.portal.onboarding.ui.activity.OnboardingFlowActivity;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;
import com.handy.portal.onboarding.ui.fragment.BackgroundCheckFeePaymentFragment;
import com.handy.portal.onboarding.ui.fragment.DeclineSuppliesDialogFragment;
import com.handy.portal.onboarding.ui.fragment.NewPurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.OnboardingStatusFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.PurchaseSuppliesFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleBuilderFragment;
import com.handy.portal.onboarding.ui.fragment.ScheduleConfirmationFragment;
import com.handy.portal.onboarding.ui.fragment.SchedulePreferencesFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                OnboardingFlowActivity.class,
                OnboardingSubflowActivity.class,
                PurchaseSuppliesFragment.class,
                PurchaseSuppliesConfirmationFragment.class,
                DeclineSuppliesDialogFragment.class,
                ScheduleBuilderFragment.class,
                SchedulePreferencesFragment.class,
                ScheduleConfirmationFragment.class,
                OnboardingStatusFragment.class,
                NewPurchaseSuppliesFragment.class,
                BackgroundCheckFeePaymentFragment.class
        })
public class OnboardingModule
{
}
