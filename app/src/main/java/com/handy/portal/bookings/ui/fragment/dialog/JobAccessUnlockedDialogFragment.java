package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.R;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.core.constant.BundleKeys;

public class JobAccessUnlockedDialogFragment extends JobAccessDialogFragment
{
    public static final String FRAGMENT_TAG = JobAccessUnlockedDialogFragment.class.getName();

    public static JobAccessUnlockedDialogFragment newInstance(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo)
    {
        JobAccessUnlockedDialogFragment fragment = new JobAccessUnlockedDialogFragment();
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
        return R.drawable.img_unlocked;
    }

    @Override
    protected int getActionButtonTextResourceId()
    {
        return R.string.job_access_unlocked_popup_action_button;
    }
}
