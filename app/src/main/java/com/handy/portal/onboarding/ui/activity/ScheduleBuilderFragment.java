package com.handy.portal.onboarding.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.onboarding.OnboardingSuppliesInfo;
import com.handy.portal.onboarding.model.BookingViewModel;
import com.handy.portal.onboarding.model.BookingsWrapperViewModel;
import com.handy.portal.onboarding.model.JobClaim;
import com.handy.portal.onboarding.model.JobClaimRequest;
import com.handy.portal.onboarding.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.onboarding.ui.fragment.OnboardLoadingDialog;
import com.handy.portal.onboarding.ui.view.OnboardJobGroupView;
import com.handy.portal.preactivation.PreActivationFlowFragment;
import com.handy.portal.preactivation.PurchaseSuppliesFragment;
import com.handy.portal.ui.fragment.dialog.OnboardingJobClaimConfirmDialog;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.FragmentUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.OnClick;

public class ScheduleBuilderFragment extends PreActivationFlowFragment
        implements OnboardJobGroupView.OnJobChangeListener,
        DialogInterface.OnCancelListener
{
    private static final String TAG = ScheduleBuilderFragment.class.getName();

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.start_date_view)
    TextView mStartDateView;
    @Bind(R.id.locations_view)
    TextView mLocationsView;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;
    @BindInt(R.integer.onboarding_dialog_load_min_time)
    int mWaitTime;

    @Inject
    Bus mBus;

    @Inject
    PrefsManager mPrefsManager;

    private OnboardLoadingDialog mLoadingDialog;

    private JobsRecyclerAdapter mAdapter;
    private BookingsListWrapper mJobs;
    private boolean mJobLoaded;

    private long mLoadingDialogDisplayTime;
    private boolean mIsResumed;

    @NonNull
    private String mProviderId;

    /**
     * mainly used for logging error of the booking ids that weren't claimed properly
     */
    private ArrayList<String> mBookingIdsToClaim;

    private Date mSelectedStartDate;
    private ArrayList<Integer> mSelectedZipclusterIds;
    private JobClaimRequest mJobClaimRequest;
    private OnboardingSuppliesInfo mOnboardingSuppliesInfo;

    public static ScheduleBuilderFragment newInstance(
            final OnboardingSuppliesInfo onboardingSuppliesInfo,
            final Date selectedStartDate,
            final ArrayList<Integer> selectedZipclusterIds)
    {
        final ScheduleBuilderFragment fragment = new ScheduleBuilderFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.ONBOARDING_SUPPLIES, onboardingSuppliesInfo);
        arguments.putSerializable(BundleKeys.START_DATE, selectedStartDate);
        arguments.putSerializable(BundleKeys.ZIPCLUSTERS_IDS, selectedZipclusterIds);
        fragment.setArguments(arguments);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingSuppliesInfo = (OnboardingSuppliesInfo) getArguments()
                .getSerializable(BundleKeys.ONBOARDING_SUPPLIES);
        mSelectedStartDate = (Date) getArguments().getSerializable(BundleKeys.START_DATE);
        mSelectedZipclusterIds = (ArrayList<Integer>) getArguments()
                .getSerializable(BundleKeys.ZIPCLUSTERS_IDS);
        mProviderId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        displaySelectedStartDate();
        displaySelectedLocations();
    }

    private void displaySelectedStartDate()
    {
        mStartDateView.setText(DateTimeUtils.DAY_OF_WEEK_MONTH_DATE_YEAR_FORMATTER
                .format(mSelectedStartDate.getTime()));
    }

    private void displaySelectedLocations()
    {
        final int count = mSelectedZipclusterIds.size();
        mLocationsView.setText(getResources().getQuantityString(
                R.plurals.locations_selected_count_formatted, count, count));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mIsResumed = true;

        if (!hasJobs(mJobs))
        {
            showLoadingDialog();
            loadJobs();
        }
        else if (isLoadingDialogVisible())
        {
            mLoadingDialog.dismiss();
            mRecyclerView.startLayoutAnimation();
        }
    }

    private void loadJobs()
    {
        mJobs = null;
        mJobLoaded = false;
        mFetchErrorView.setVisibility(View.GONE);
        mBus.post(new HandyEvent.RequestOnboardingJobs(mSelectedStartDate, mSelectedZipclusterIds));
    }

    /**
     * Checks to see if there is at least one job. It's tricky, because there could be elements
     * without jobs, so we need to check specifically for the existence of a job
     *
     * @param wrapper
     * @return
     */
    private boolean hasJobs(BookingsListWrapper wrapper)
    {
        return wrapper != null && wrapper.hasJobs();
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
        mJobs = event.bookings;
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

            if (!hasJobs(mJobs))
            {
                if (isLoadingDialogVisible())
                {
                    mLoadingDialog.dismiss();
                    mSingleActionButton.setVisibility(View.GONE);
                }
                mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NoJobsLoaded()));
                // FIXME: Do not terminate, do something else
                terminate();
            }
            else
            {
                mAdapter = new JobsRecyclerAdapter(
                        mJobs.getBookingsWrappers(),
                        getString(R.string.onboard_getting_started_title),
                        ScheduleBuilderFragment.this,
                        getResources().getString(R.string.onboard_no_time_available)
                );
                mRecyclerView.setAdapter(mAdapter);
                updateButton();
                if (isLoadingDialogVisible())
                {
                    mLoadingDialog.dismiss();
                    mRecyclerView.startLayoutAnimation();
                }
            }
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

    /**
     * Calculates the job prices, and updates that information on the button.
     */
    public void updateButton()
    {
        //one of the jobs changed price, re-calculate
        float sum = 0;
        for (BookingsWrapperViewModel model : mAdapter.getBookingsWrapperViewModels())
        {
            for (BookingViewModel bookingView : model.getBookingViewModels())
            {
                if (bookingView.isSelected())
                {
                    sum += bookingView.getBookingAmount();
                }
            }
        }

        if (sum > 0)
        {
            String symbol = mAdapter.getBookingsWrapperViewModels().get(0).getBookingViewModels().get(0).getCurrencySymbol();
            String formattedPrice = String.format(Locale.getDefault(), "%.0f", sum);
            if (symbol != null)
            {
                formattedPrice = symbol + formattedPrice;
            }
            String text = String.format(getString(R.string.onboard_claim_and_earn_formatted), formattedPrice);
            mSingleActionButton.setText(text);
            mSingleActionButton.setAlpha(1.0f);
            mSingleActionButton.setEnabled(true);
        }
        else
        {
            mSingleActionButton.setText(R.string.continue_to_next_step);
            mSingleActionButton.setAlpha(0.5f);
            mSingleActionButton.setEnabled(false);
        }
    }

    /**
     * Post an analytics event that the user decided to not select a job. Navigate to the
     * Available Jobs section
     */
    private void skipJobSelection()
    {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.Skipped()));
        // FIXME: Do not terminate, do something else
        terminate();
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        mFetchErrorView.setVisibility(View.GONE);
        showLoadingOverlay();
        loadJobs();
    }

    /**
     * A success here means the server successfully processed the request. Does not mean all the
     * jobs requested to be claimed were actually claimed. ie..., if I requested 3 jobs, the response
     * can come back: 0 out of 3 claimed.
     *
     * @param event
     */
    @Subscribe
    public void onReceiveClaimJobsSuccess(HandyEvent.ReceiveClaimJobsSuccess event)
    {
        hideLoadingOverlay();
        mFetchErrorView.setVisibility(View.GONE);

        mBus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.ClaimBatchSuccess(mBookingIdsToClaim)));

        String message = event.getJobClaimResponse().getMessage();

        List<Booking> bookings = new ArrayList<>();
        for (BookingClaimDetails bcd : event.getJobClaimResponse().getJobs())
        {
            if (bcd.getBooking().isClaimedByMe()
                    || mProviderId.equals(bcd.getBooking().getProviderId()))
            {
                bookings.add(bcd.getBooking());
                mBus.post(new LogEvent.AddLogEvent(
                        new NativeOnboardingLog.ClaimSuccess(bcd.getBooking())));
            }
            else
            {
                mBus.post(new LogEvent.AddLogEvent(
                        new NativeOnboardingLog.ClaimError(bcd.getBooking(), message)));
            }
        }

        if (bookings.isEmpty())
        {
            //nothing was claimed.
            // FIXME: Do not terminate, do something else
            terminate();
        }
        else
        {
            if (bookings.size() == event.getJobClaimResponse().getJobs().size())
            {
                //I was able to claim 100% of the jobs I wanted.
                next();
            }
            else
            {
                next();
            }
        }
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error)
    {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchError(
                mBookingIdsToClaim, error.error.getMessage())));

        hideLoadingOverlay();
        mFetchErrorView.setVisibility(View.VISIBLE);
        if (error.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            mErrorText.setText(getString(R.string.error_fetching_connectivity_issue));
        }
        else
        {
            mErrorText.setText(getString(R.string.onboard_job_claim_error));
        }
    }

    @Override
    public void onCancel(final DialogInterface dialog)
    {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.Skipped()));
        // FIXME: Do not terminate, do something else
        terminate();
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case RequestCode.CONFIRM_REQUEST:
                    confirmJobClaims();
                    break;
            }
        }
    }

    private void confirmJobClaims()
    {
        showLoadingOverlay();
        mBus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.ClaimBatchSubmitted(mBookingIdsToClaim)));
        mBus.post(new HandyEvent.RequestClaimJobs(mJobClaimRequest));
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
        final ArrayList<JobClaim> jobs = new ArrayList<>();
        mBookingIdsToClaim = new ArrayList<>();
        for (BookingsWrapperViewModel model : mAdapter.getBookingsWrapperViewModels())
        {
            for (BookingViewModel bookingView : model.getBookingViewModels())
            {
                if (bookingView.isSelected())
                {
                    jobs.add(new JobClaim(
                            bookingView.getBooking().getId(),
                            bookingView.getBooking().getType().name().toLowerCase())
                    );
                    mBookingIdsToClaim.add(bookingView.getBooking().getId());
                }
            }
        }
        mJobClaimRequest = new JobClaimRequest(jobs);

        if (!jobs.isEmpty())
        {
            //show confirmation dialog to confirm the selected jobs.
            final OnboardingJobClaimConfirmDialog fragment =
                    OnboardingJobClaimConfirmDialog.newInstance();
            fragment.setTargetFragment(this, RequestCode.CONFIRM_REQUEST);
            FragmentUtils.safeLaunchDialogFragment(
                    fragment,
                    getActivity(),
                    null
            );
        }
        else
        {
            //no jobs were selected, send the user to claim job screen.
            skipJobSelection();
        }
    }

    private void next()
    {
        next(PurchaseSuppliesFragment.newInstance(mOnboardingSuppliesInfo));
    }
}
