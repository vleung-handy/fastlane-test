package com.handy.portal.preactivation;

import android.support.annotation.Nullable;

import com.handy.portal.R;

public class ScheduleConfirmationFragment extends PreActivationFlowFragment
{
    public static ScheduleConfirmationFragment newInstance()
    {
        return new ScheduleConfirmationFragment();
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_confirmation;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.confirmation);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.ready_to_commit);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return getString(R.string.about_to_claim_and_order);
    }

    @Override
    protected String getPrimaryButtonText()
    {
        return getString(R.string.finish_building_schedule);
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        // FIXME: Claim jobs here
        getActivity().finish();
    }
}
