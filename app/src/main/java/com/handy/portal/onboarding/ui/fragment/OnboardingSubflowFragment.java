package com.handy.portal.onboarding.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.handy.portal.onboarding.ui.activity.OnboardingSubflowActivity;

public abstract class OnboardingSubflowFragment extends ActionBarFragment {
    protected SubflowData mSubflowData;
    protected boolean mIsSingleStepMode;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubflowData = (SubflowData) getArguments().getSerializable(BundleKeys.SUBFLOW_DATA);
        mIsSingleStepMode = getArguments().getBoolean(BundleKeys.IS_SINGLE_STEP_MODE, false);
    }

    protected void next(@NonNull final OnboardingSubflowFragment fragment) {
        ((OnboardingSubflowActivity) getActivity()).next(fragment, true);
    }

    protected void terminate(@NonNull final Intent data) {
        ((OnboardingSubflowActivity) getActivity()).terminate(data);
    }

    protected void redo(final SubflowType subflowType, final int requestCode) {
        final Intent intent = new Intent(getActivity(), OnboardingSubflowActivity.class);
        intent.putExtra(BundleKeys.ONBOARDING_DETAILS, getOnboardingDetails());
        intent.putExtra(BundleKeys.SUBFLOW_TYPE, subflowType);
        startActivityForResult(intent, requestCode);
    }

    protected void cancel(@NonNull final Intent data) {
        ((OnboardingSubflowActivity) getActivity()).cancel(data);
    }

    protected OnboardingDetails getOnboardingDetails() {
        return ((OnboardingSubflowActivity) getActivity()).getOnboardingDetails();
    }

}
