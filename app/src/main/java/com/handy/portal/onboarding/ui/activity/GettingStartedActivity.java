package com.handy.portal.onboarding.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.onboarding.model.BookingViewModel;
import com.handy.portal.onboarding.model.BookingsWrapperViewModel;
import com.handy.portal.onboarding.model.JobClaim;
import com.handy.portal.onboarding.model.JobClaimRequest;
import com.handy.portal.onboarding.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.onboarding.ui.fragment.OnboardLoadingDialog;
import com.handy.portal.onboarding.ui.view.OnboardJobGroupView;
import com.handy.portal.ui.fragment.dialog.OnboardJobClaimConfirmDialog;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GettingStartedActivity extends AppCompatActivity
        implements OnboardJobGroupView.OnJobChangeListener,
        DialogInterface.OnCancelListener,
        OnboardJobClaimConfirmDialog.ConfirmationDialogListener
{

    private static final String TAG = GettingStartedActivity.class.getName();

    /**
     * This is used for logging analytics
     */
    private static final String SOURCE = "onboarding";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.loading_overlay)
    View mLoadingOverlayView;
    @Bind(R.id.fetch_error_view)
    View mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mErrorText;

    @Inject
    Bus mBus;

    @Inject
    PrefsManager mPrefsManager;

    private OnboardLoadingDialog mLoadingDialog;
    private OnboardJobClaimConfirmDialog mOnboardJobClaimConfirmDialog;

    private JobsRecyclerAdapter mAdapter;
    private BookingsListWrapper mJobs2;
    private boolean mJobLoaded;
    private String mNoThanks;

    private Drawable mGreenDrawable;
    private Drawable mGrayDrawable;
    private long mRequestTime;
    private int mWaitTime;
    private boolean mDestroyed;

    @NonNull
    private String mProviderId;

    /**
     * mainly used for logging error of the booking ids that weren't claimed properly
     */
    private ArrayList<String> mBookingIdsToClaim;

    private JobClaimRequest mJobClaimRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);
        Utils.inject(this, this);

        ButterKnife.bind(this);

        mWaitTime = getResources().getInteger(R.integer.onboarding_dialog_load_min_time);

        mProviderId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mNoThanks = getString(R.string.onboard_no_thanks);

        mGreenDrawable = ContextCompat.getDrawable(this, R.drawable.button_green);
        mGrayDrawable = ContextCompat.getDrawable(this, R.drawable.button_gray);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.onboard_getting_started));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x_white);
        mFetchErrorView.setBackgroundColor(ContextCompat.getColor(this, R.color.handy_bg));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBus.register(this);

        if (!hasJobs(mJobs2))
        {
            showLoadingDialog();
            loadJobs();
        }
    }

    private void loadJobs()
    {
        mJobs2 = null;
        mJobLoaded = false;
        mRequestTime = System.currentTimeMillis();
        mFetchErrorView.setVisibility(View.GONE);
        mBus.post(new HandyEvent.RequestOnboardingJobs());

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
        if (wrapper == null)
        {
            return false;
        }
        else
        {
            return wrapper.hasJobs();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBus.unregister(this);
    }

    /**
     * The dialog will only be displayed for a certain time, then it will check whether it can be
     * dismissed.
     */
    public void showLoadingDialog()
    {
        mLoadingDialog = new OnboardLoadingDialog();
        mLoadingDialog.show(getFragmentManager(), OnboardLoadingDialog.TAG);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(TAG, "run: DialogRunCompleted");
                bindJobsAndRemoveDialog();
            }
        }, mWaitTime);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                skipJobSelection();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        skipJobSelection();
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
        mLoadingOverlayView.setVisibility(View.GONE);
        mJobLoaded = true;
        mJobs2 = event.bookings;
        bindJobsAndRemoveDialog();
    }

    @Subscribe
    public void onJobLoadError(HandyEvent.ReceiveOnboardingJobsError event)
    {
        if (dialogVisible())
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
    private void bindJobsAndRemoveDialog()
    {
        long elapsedTime = System.currentTimeMillis() - mRequestTime;

        if (mJobLoaded && (elapsedTime >= mWaitTime))
        {
            Log.d(TAG, "bindJobs: ");

            if (!hasJobs(mJobs2))
            {
                //there are no jobs, so..., go to the available jobs fragment
                if (dialogVisible())
                {
                    mLoadingDialog.dismiss();
                    mBtnNext.setVisibility(View.GONE);
                }
                mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NoJobsLoaded()));
                goToAvailableJobs(getBundle(getString(R.string.onboard_claim_no_job), R.drawable.snack_bar_schedule));
            }
            else
            {
                mAdapter = new JobsRecyclerAdapter(
                        mJobs2.getBookingsWrappers(),
                        getString(R.string.onboard_getting_started_title),
                        GettingStartedActivity.this,
                        getResources().getString(R.string.onboard_no_time_available)
                );
                mRecyclerView.setAdapter(mAdapter);
                updateButton();
                if (dialogVisible())
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
    private boolean dialogVisible()
    {
        return mLoadingDialog != null && !isFinishing() && !mDestroyed && mLoadingDialog.isVisible();
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
            //model could be null, if it's just a header
            if (model != null)
            {
                for (BookingViewModel bookingView : model.getBookingViewModels())
                {
                    if (bookingView.isSelected())
                    {
                        sum += bookingView.getBookingAmount();
                    }
                }
            }
        }

        if (sum > 0)
        {
            String symbol = mAdapter.getBookingsWrapperViewModels().get(1).getBookingViewModels().get(0).getCurrencySymbol();
            String formattedPrice = String.format("%.0f", sum);
            if (symbol != null)
            {
                formattedPrice = symbol + formattedPrice;
            }
            String text = String.format(getString(R.string.onboard_claim_and_earn_formatted), formattedPrice);
            mBtnNext.setText(text);
            mBtnNext.setBackground(mGreenDrawable);
        }
        else
        {
            mBtnNext.setText(mNoThanks);
            mBtnNext.setBackground(mGrayDrawable);
        }
    }

    @OnClick(R.id.btn_next)
    public void buttonClicked()
    {
        mJobClaimRequest = new JobClaimRequest();
        mBookingIdsToClaim = new ArrayList<>();
        for (BookingsWrapperViewModel model : mAdapter.getBookingsWrapperViewModels())
        {
            //model could be null, if it's just a header
            if (model != null)
            {
                for (BookingViewModel bookingView : model.getBookingViewModels())
                {
                    if (bookingView.isSelected())
                    {
                        mJobClaimRequest.mJobs.add(new JobClaim(
                                bookingView.getBooking().getId(),
                                bookingView.getBooking().getType().name().toLowerCase())
                        );
                        mBookingIdsToClaim.add(bookingView.getBooking().getId());
                    }
                }
            }
        }

        if (!mJobClaimRequest.mJobs.isEmpty())
        {
            //show confirmation dialog to confirm the selected jobs.
            mOnboardJobClaimConfirmDialog = new OnboardJobClaimConfirmDialog();
            mOnboardJobClaimConfirmDialog.show(
                    getSupportFragmentManager(),
                    OnboardJobClaimConfirmDialog.class.getSimpleName()
            );
        }
        else
        {
            //no jobs were selected, send the user to claim job screen.
            skipJobSelection();
        }
    }

    /**
     * Post an analytics event that the user decided to not select a job. Navigate to the
     * Available Jobs section
     */
    private void skipJobSelection() {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.Skipped()));
        goToAvailableJobs(getBundle(getString(R.string.onboard_claim_no_job), R.drawable.snack_bar_schedule));
    }

    private void goToAvailableJobs(Bundle bundle)
    {
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS, bundle));
        finish();
    }


    private void goToScheduledJobs(Bundle bundle)
    {
        mBus.post(new NavigationEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, bundle));
        finish();
    }

    private Bundle getBundle(String message, @DrawableRes int imageRes)
    {
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.MESSAGE, message);
        bundle.putInt(BundleKeys.MESSAGE_ICON, imageRes);

        return bundle;
    }

    @OnClick(R.id.try_again_button)
    public void doRequestBookingsAgain()
    {
        mFetchErrorView.setVisibility(View.GONE);
        mLoadingOverlayView.setVisibility(View.VISIBLE);
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
        mLoadingOverlayView.setVisibility(View.GONE);
        mFetchErrorView.setVisibility(View.GONE);

        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSuccess(mBookingIdsToClaim)));

        String message = event.mJobClaimResponse.getMessage();
        if (event.mJobClaimResponse == null || event.mJobClaimResponse.getJobs() == null)
        {
            //this should never happen, but just in case.
            goToAvailableJobs(getBundle(message, R.drawable.snack_bar_error));
        }

        List<Booking> bookings = new ArrayList<>();
        for (BookingClaimDetails bcd : event.mJobClaimResponse.getJobs())
        {
            if (bcd.getBooking().isClaimedByMe() || mProviderId.equals(bcd.getBooking().getProviderId()))
            {
                bookings.add(bcd.getBooking());
                mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimSuccess(
                        bcd.getBooking()
                )));
            }
            else
            {
                mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimError(
                        bcd.getBooking(),
                        message
                )));
            }
        }

        if (bookings.isEmpty())
        {
            //nothing was claimed.
            goToAvailableJobs(getBundle(message, R.drawable.snack_bar_error));
        }
        else
        {
            if (bookings.size() == event.mJobClaimResponse.getJobs().size())
            {
                //I was able to claim 100% of the jobs I wanted.
                goToScheduledJobs(getBundle(message, R.drawable.snack_bar_check));
            }
            else
            {
                goToScheduledJobs(getBundle(message, R.drawable.snack_bar_schedule));
                //I was only able to claim partially what I wanted.
            }
        }
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error)
    {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchError(mBookingIdsToClaim, error.error.getMessage())));

        mLoadingOverlayView.setVisibility(View.GONE);
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
    protected void onDestroy()
    {
        super.onDestroy();
        mDestroyed = true;
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCancel(final DialogInterface dialog)
    {
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.Skipped()));
        goToAvailableJobs(getBundle(getString(R.string.onboard_claim_no_job), R.drawable.snack_bar_schedule));
    }

    /**
     * This happens if the user confirms the job claims via the confirmation dialog
     */
    @Override
    public void confirmJobClaims()
    {
        if (mOnboardJobClaimConfirmDialog != null) {
            mOnboardJobClaimConfirmDialog.dismiss();
        }

        mLoadingOverlayView.setVisibility(View.VISIBLE);
        mBus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.ClaimBatchSubmitted(mBookingIdsToClaim)));
        mBus.post(new HandyEvent.RequestClaimJobs(mJobClaimRequest));
    }
}
