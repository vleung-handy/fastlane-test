package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.R;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.core.constant.BundleKeys;

public class EarlyAccessTrialDialogFragment extends JobAccessDialogFragment
{
    public static final String FRAGMENT_TAG = EarlyAccessTrialDialogFragment.class.getName();

    public static EarlyAccessTrialDialogFragment newInstance(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo)
    {
        EarlyAccessTrialDialogFragment fragment = new EarlyAccessTrialDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.BOOKING_PRIORITY_ACCESS, priorityAccessInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected BookingsWrapper.PriorityAccessInfo getPriorityAccessFromBundle()
    {
        return (BookingsWrapper.PriorityAccessInfo) getArguments().getSerializable(BundleKeys.BOOKING_PRIORITY_ACCESS); //should not be null
    }

    @Override
    protected int getHeaderImageResourceId()
    {
        return R.drawable.img_unlocked_trial;
    }

    @Override
    protected int getActionButtonTextResourceId()
    {
        return R.string.job_access_early_access_popup_action_button;
    }
}
