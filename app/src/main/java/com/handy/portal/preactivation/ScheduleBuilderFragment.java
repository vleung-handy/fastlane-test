package com.handy.portal.preactivation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.onboarding.ui.view.OnboardingJobsViewGroup;

import java.util.List;

import butterknife.Bind;

public class ScheduleBuilderFragment extends PreActivationFlowFragment
        implements OnboardingJobsViewGroup.OnJobCheckedChangedListener
{
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private JobsRecyclerAdapter mAdapter;
    private BookingsListWrapper mBookingsListWrapper;

    public static ScheduleBuilderFragment newInstance(final BookingsListWrapper bookingsListWrapper)
    {
        return new ScheduleBuilderFragment();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        disableButtons();
        mAdapter = new JobsRecyclerAdapter(mBookingsListWrapper.getBookingsWrappers(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.startLayoutAnimation();
        updateButton();
    }

    @Override
    public void onJobCheckedChanged()
    {
        updateButton();
    }

    public void updateButton()
    {
        if (mAdapter != null && !mAdapter.getSelectedBookings().isEmpty())
        {
            enableButtons();
        }
        else
        {
            disableButtons();
        }
    }

    @Override
    protected int getButtonType()
    {
        return ButtonTypes.SINGLE_FIXED;
    }

    @Override
    protected int getLayoutResId()
    {
        return R.layout.view_schedule_builder;
    }

    @Override
    protected String getTitle()
    {
        return getString(R.string.claim_your_first_jobs);
    }

    @Nullable
    @Override
    protected String getHeaderText()
    {
        return getString(R.string.onboard_getting_started_title);
    }

    @Nullable
    @Override
    protected String getSubHeaderText()
    {
        return null;
    }

    @Override
    protected void onPrimaryButtonClicked()
    {
        if (mAdapter != null)
        {
            final List<Booking> selectedBookings = mAdapter.getSelectedBookings();
            if (!selectedBookings.isEmpty())
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSubmitted()));
                setPendingBookings(selectedBookings);
                next(ScheduleConfirmationFragment.newInstance());
            }
        }
    }
}
