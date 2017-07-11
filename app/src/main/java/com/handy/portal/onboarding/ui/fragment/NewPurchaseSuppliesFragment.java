package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.handy.portal.R;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;


public class NewPurchaseSuppliesFragment extends OnboardingSubflowUIFragment {

    public static NewPurchaseSuppliesFragment newInstance() {
        return new NewPurchaseSuppliesFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.post(new NativeOnboardingLog(
                NativeOnboardingLog.Types.SUPPLIES_SUGGESTION_SHOWN));

    }

    @Override
    protected int getButtonType() {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.view_new_purchase_supplies;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.supplies);
    }

    @Nullable
    @Override
    protected String getHeaderText() {
        return getString(R.string.do_you_have_supplies);
    }

    @Nullable
    @Override
    protected String getSubHeaderText() {
        return getString(R.string.supplies_description_standard_kit);
    }

    @Override
    protected void onPrimaryButtonClicked() {
        bus.post(new NativeOnboardingLog(
                NativeOnboardingLog.Types.SUPPLIES_SUGGESTION_TAPPED));
        terminate(new Intent());
    }

    @Override
    protected String getPrimaryButtonText() {
        return getString(R.string.continue_single_word);
    }
}
