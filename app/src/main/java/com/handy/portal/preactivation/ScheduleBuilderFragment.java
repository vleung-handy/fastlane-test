package com.handy.portal.preactivation;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.view.LabelAndValueView;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.onboarding.ui.fragment.OnboardLoadingDialog;
import com.handy.portal.onboarding.ui.view.OnboardJobGroupView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.OnClick;

public class ScheduleBuilderFragment extends PreActivationFlowFragment
        implements OnboardJobGroupView.OnJobChangeListener
{
    private static final String TAG = ScheduleBuilderFragment.class.getName();

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.start_date_view)
    LabelAndValueView mStartDateView;
    @Bind(R.id.locations_view)
    LabelAndValueView mLocationsView;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;
    @BindInt(R.integer.onboarding_dialog_load_min_time)
    int mWaitTime;

    private OnboardLoadingDialog mLoadingDialog;

    private JobsRecyclerAdapter mAdapter;
    private BookingsListWrapper mBookingsListWrapper;
    private boolean mJobLoaded;

    private long mLoadingDialogDisplayTime;
    private boolean mIsResumed;

    private Date mSelectedStartDate;
    private ArrayList<Integer> mSelectedZipclusterIds;

    public static ScheduleBuilderFragment newInstance(
            final Date selectedStartDate,
            final ArrayList<Integer> selectedZipclusterIds)
    {
        final ScheduleBuilderFragment fragment = new ScheduleBuilderFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PROVIDER_START_DATE, selectedStartDate);
        arguments.putSerializable(BundleKeys.ZIPCLUSTERS_IDS, selectedZipclusterIds);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSelectedStartDate = (Date) getArguments().getSerializable(BundleKeys.PROVIDER_START_DATE);
        mSelectedZipclusterIds = (ArrayList<Integer>) getArguments()
                .getSerializable(BundleKeys.ZIPCLUSTERS_IDS);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStartDateView.setLabel(getString(R.string.start_date));
        mLocationsView.setLabel(getString(R.string.locations));
        displaySelectedStartDate();
        if (shouldDisplaySelectedLocations())
        {
            displaySelectedLocations();
        }
        else
        {
            mLocationsView.setVisibility(View.GONE);
        }
    }

    private void displaySelectedStartDate()
    {
        mStartDateView.setValue(DateTimeUtils.formatDayOfWeekMonthDateYear(mSelectedStartDate));
    }

    private void displaySelectedLocations()
    {
        final int count = mSelectedZipclusterIds.size();
        mLocationsView.setValue(getResources().getQuantityString(
                R.plurals.locations_selected_count_formatted, count, count));
    }

    private boolean shouldDisplaySelectedLocations()
    {
        return mSelectedZipclusterIds != null && !mSelectedZipclusterIds.isEmpty();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mIsResumed = true;

        if (!getBookingsWrappers().isEmpty() && isLoadingDialogVisible())
        {
            mLoadingDialog.dismiss();
            mRecyclerView.startLayoutAnimation();
        }
        else
        {
            showLoadingDialog();
            loadJobs();
        }
    }

    private void loadJobs()
    {
        mBookingsListWrapper = null;
        mJobLoaded = false;
        mFetchErrorView.setVisibility(View.GONE);
        bus.post(new HandyEvent.RequestOnboardingJobs(null, mSelectedZipclusterIds));
    }

    @NonNull
    private List<BookingsWrapper> getBookingsWrappers()
    {
        if (mBookingsListWrapper != null && mBookingsListWrapper.hasBookings())
        {
            return mBookingsListWrapper.getBookingsWrappers();
        }
        return Collections.emptyList();
    }

    @Override
    public void onPause()
    {
        mIsResumed = false;
        super.onPause();
    }

    /**
     * The dialog will only be displayed for a certain time, then it will check whether it can be
     * dismissed.
     */
    public void showLoadingDialog()
    {
        mLoadingDialogDisplayTime = System.currentTimeMillis();
        mLoadingDialog = new OnboardLoadingDialog();
        mLoadingDialog.show(getFragmentManager(), OnboardLoadingDialog.TAG);
        mLoadingDialog.setCancelable(false);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "run: DialogRunCompleted");
                bindJobsAndRemoveLoadingDialog();
            }
        }, mWaitTime);
    }

    /**
     * When the jobs are loaded, it will check whether the dialog has been up for a specified
     * amount of time. If it has, then dismiss it.
     *
     * @param event
     */
    @Subscribe
    public void onJobLoaded(HandyEvent.ReceiveOnboardingJobsSuccess event)
    {
        Log.d(TAG, "onJobLoaded: ");
        hideLoadingOverlay();
        mJobLoaded = true;
        mBookingsListWrapper = event.getBookingsListWrapper();
        bindJobsAndRemoveLoadingDialog();
    }

    @Subscribe
    public void onJobLoadError(HandyEvent.ReceiveOnboardingJobsError event)
    {
        if (isLoadingDialogVisible())
        {
            mLoadingDialog.dismiss();
        }

        if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mErrorText.setText(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            mErrorText.setText(getString(R.string.onboard_job_load_error));
        }
        mFetchErrorView.setVisibility(View.VISIBLE);
    }

    /**
     * dismiss the dialog after the jobs have loaded, or 4 seconds, whichever one is slowest
     */
    private void bindJobsAndRemoveLoadingDialog()
    {
        long elapsedTime = System.currentTimeMillis() - mLoadingDialogDisplayTime;

        if (mJobLoaded && (elapsedTime >= mWaitTime))
        {
            Log.d(TAG, "bindJobs: ");

            final List<BookingsWrapper> bookingsWrappers = getBookingsWrappers();
            if (bookingsWrappers.isEmpty())
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NoJobsLoaded()));
            }
            else
            {
                mAdapter = new JobsRecyclerAdapter(bookingsWrappers, ScheduleBuilderFragment.this);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.startLayoutAnimation();
            }
            if (isLoadingDialogVisible())
            {
                mLoadingDialog.dismiss();
            }
            updateButton();
        }
    }

    /**
     * The dialog is only dismissable under these conditions.
     *
     * @return
     */
    private boolean isLoadingDialogVisible()
    {
        return mIsResumed && mLoadingDialog != null && mLoadingDialog.isVisible();
    }

    @Override
    public void onPriceChanged()
    {
        updateButton();
    }

    public void updateButton()
    {
        if (mAdapter != null && !mAdapter.getSelectedBookings().isEmpty())
        {
            mSingleActionButton.setAlpha(1.0f);
            mSingleActionButton.setEnabled(true);
        }
        else
        {
            mSingleActionButton.setAlpha(0.5f);
            mSingleActionButton.setEnabled(false);
        }
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        mFetchErrorView.setVisibility(View.GONE);
        showLoadingOverlay();
        loadJobs();
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
    protected String getPrimaryButtonText()
    {
        return getString(R.string.continue_to_next_step);
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
