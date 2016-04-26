package com.handy.portal.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.onboarding.BookingViewModel;
import com.handy.portal.model.onboarding.BookingsWrapperViewModel;
import com.handy.portal.model.onboarding.JobClaim;
import com.handy.portal.model.onboarding.JobClaimRequest;
import com.handy.portal.model.onboarding.JobClaimResponse;
import com.handy.portal.ui.adapter.JobsRecyclerAdapter;
import com.handy.portal.ui.fragment.OnboardLoadingDialog;
import com.handy.portal.ui.view.HandyJobGroupView;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GettingStartedActivity extends AppCompatActivity implements HandyJobGroupView.OnJobChangeListener
{

    private static final String TAG = GettingStartedActivity.class.getName();

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Bind(R.id.btn_next)
    Button mBtnNext;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.loading_overlay)
    View mLoadingOverlayView;

    @Inject
    Mixpanel mixpanel;

    @Inject
    Bus bus;

    OnboardLoadingDialog mLoadingDialog;

    JobsRecyclerAdapter mAdapter;


    //    TODO: JIA: make sure to save this onSavedInstanceState during cleanup work
    BookingsListWrapper mJobs2;

    String mNoThanks;

    Drawable mGreenDrawable;
    Drawable mGrayDrawable;
    private long mRequestTime;
    private int mWaitTime;
    private boolean mDestroyed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);
        Utils.inject(this, this);

        ButterKnife.bind(this);

        mWaitTime = getResources().getInteger(R.integer.onboarding_dialog_load_min_time);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mNoThanks = getString(R.string.onboard_no_thanks);

        mGreenDrawable = ContextCompat.getDrawable(this, R.drawable.button_green);
        mGrayDrawable = ContextCompat.getDrawable(this, R.drawable.button_gray);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.onboard_getting_started));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_x_white);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        this.bus.register(this);

        if (!hasJobs(mJobs2))
        {
            loadJobs();
        }
    }

    private void loadJobs()
    {
        mJobs2 = null;
        mRequestTime = System.currentTimeMillis();
        showLoadingDialog();
        bus.post(new HandyEvent.RequestOnboardingJobs());
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
        if (wrapper != null && wrapper.getBookingsWrappers() != null && !wrapper.getBookingsWrappers().isEmpty())
        {
            //we need to check that it has actual jobs, and not just an empty list
            for (BookingsWrapper booking : wrapper.getBookingsWrappers())
            {
                if (booking.getBookings() != null && !booking.getBookings().isEmpty())
                {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        this.bus.unregister(this);
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
                safeDialogRemoval();
            }
        }, mWaitTime);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                goToAvailableJobs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        goToAvailableJobs();
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
        mJobs2 = event.bookings;
        if (!hasJobs(mJobs2))
        {
            goToAvailableJobs();
        }
        else
        {
            safeDialogRemoval();
        }
    }

    @Subscribe
    public void onJobLoadError(HandyEvent.ReceiveOnboardingJobsError error)
    {
        if (dialogDismissable())
        {
            mLoadingDialog.dismiss();
        }

        String msg;
        if (error.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            msg = getString(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            msg = getString(R.string.onboard_job_load_error);
        }

        //TODO: JIA offer them a way to try again
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * dismiss the dialog after the jobs have loaded, or 4 seconds, whichever one is slowest
     */
    private void safeDialogRemoval()
    {
//        TODO: JIA remove some of this excessive logging when feature complete
        if (mJobs2 == null)
        {
            Log.d(TAG, "safeDialogRemoval: job is null");
        }
        long elapsedTime = System.currentTimeMillis() - mRequestTime;
        if (elapsedTime < mWaitTime)
        {
            Log.d(TAG, "safeDialogRemoval: not enough time have elapsed, not closing: " + elapsedTime);
        }

        if (mLoadingDialog == null)
        {
            Log.d(TAG, "safeDialogRemoval: dialog is null, not closing");
        }
        if (mJobs2 != null && (elapsedTime > mWaitTime) && dialogDismissable())
        {
            Log.d(TAG, "safeDialogRemoval: Dismissing Dialog");
            mLoadingDialog.dismiss();
            mAdapter = new JobsRecyclerAdapter(
                    mJobs2.getBookingsWrappers(),
                    getString(R.string.onboard_getting_started_title),
                    GettingStartedActivity.this
            );
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.startLayoutAnimation();
            updateButton();
        }
    }

    /**
     * The dialog is only dismissable under these conditions.
     *
     * @return
     */
    private boolean dialogDismissable()
    {
        return mLoadingDialog != null && !this.isFinishing() && !this.mDestroyed;
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
                for (BookingViewModel bookingView : model.mBookingViewModels)
                {
                    if (bookingView.selected)
                    {
                        sum += bookingView.getBookingAmount();
                    }
                }
            }
        }

        if (sum > 0)
        {
            String symbol = mAdapter.getBookingsWrapperViewModels().get(1).mBookingViewModels.get(0).getCurrencySymbol();
            String formattedPrice = symbol + String.format("%.0f", sum);
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
        JobClaimRequest request = new JobClaimRequest();
        for (BookingsWrapperViewModel model : mAdapter.getBookingsWrapperViewModels())
        {
            //model could be null, if it's just a header
            if (model != null)
            {
                for (BookingViewModel bookingView : model.mBookingViewModels)
                {
                    if (bookingView.selected)
                    {
                        request.mJobs.add(new JobClaim(bookingView.booking.getId(), bookingView.booking.getType().name().toLowerCase()));
                    }
                }
            }
        }

        if (!request.mJobs.isEmpty())
        {
            mLoadingOverlayView.setVisibility(View.VISIBLE);
            bus.post(new HandyEvent.RequestClaimJobs(request));
        }
        else
        {
            //no jobs were selected, send the user to claim job screen.
            goToAvailableJobs();
        }
    }

    private void goToAvailableJobs()
    {
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.AVAILABLE_JOBS));
        finish();
    }


    private void goToScheduledJobs()
    {
        bus.post(new NavigationEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS));
        finish();
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

        List<Booking> bookings = jobsClaimedByMe(event.mJobClaimResponse);

        if (bookings.isEmpty())
        {
            Toast.makeText(this, getString(R.string.onboard_no_longer_available), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, event.mJobClaimResponse.getMessage(), Toast.LENGTH_SHORT).show();
            goToScheduledJobs();
        }
    }

    @NonNull
    private List<Booking> jobsClaimedByMe(JobClaimResponse jobsClaimed)
    {
        List<Booking> rval = new ArrayList<>();
        for (BookingClaimDetails bcd : jobsClaimed.getJobs())
        {
            if (bcd.getBooking().isClaimedByMe())
            {
                rval.add(bcd.getBooking());
            }
        }

        return rval;
    }

    @Subscribe
    public void onReceiveClaimJobsError(HandyEvent.ReceiveClaimJobsError error)
    {
        mLoadingOverlayView.setVisibility(View.GONE);
        String msg = "";
        if (error.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            msg = getString(R.string.error_fetching_connectivity_issue);
        }
        else
        {
            msg = getString(R.string.onboard_job_claim_error);
        }

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mDestroyed = true;
    }
}
