package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.onboarding.model.claim.JobClaim;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.onboarding.ui.view.OnboardingJobsViewGroup;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostCheckoutDialogFragment extends InjectedDialogFragment
        implements OnboardingJobsViewGroup.OnJobCheckedChangedListener
{
    public static final String TAG = PostCheckoutDialogFragment.class.getSimpleName();
    private static final String KEY_POST_CHECKOUT_INFO = "post_checkout_info";
    private PostCheckoutInfo mPostCheckoutInfo;
    private BookingsWrapperViewModel mBookingsWrapperViewModel;

    @Inject
    EventBus mBus;

    @BindView(R.id.claim_prompt_text)
    TextView mClaimPromptText;
    @BindView(R.id.claim_subtitle_text)
    TextView mClaimSubtitleText;
    @BindView(R.id.jobs_container)
    ViewGroup mJobsContainer;
    @BindView(R.id.claim_button)
    Button mClaimButton;

    public static PostCheckoutDialogFragment newInstance(final PostCheckoutInfo postCheckoutInfo)
    {
        final PostCheckoutDialogFragment dialogFragment = new PostCheckoutDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_POST_CHECKOUT_INFO, postCheckoutInfo);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        mPostCheckoutInfo = (PostCheckoutInfo) getArguments()
                .getSerializable(KEY_POST_CHECKOUT_INFO);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation_slide_in_down_from_right);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState)
    {
        final View view =
                inflater.inflate(R.layout.fragment_dialog_post_checkout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        final Booking.User customer = mPostCheckoutInfo.getCustomer();
        mClaimPromptText.setText(getString(R.string.post_checkout_claim_prompt_formatted,
                customer.getFirstName()));
        mClaimSubtitleText.setText(getString(R.string.post_checkout_claim_subtitle_formatted,
                customer.getFirstName()));
        displayJobs();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause()
    {
        mBus.unregister(this);
        super.onPause();
    }

    @OnClick(R.id.claim_button)
    public void onClaimButtonClicked()
    {
        final List<Booking> selectedBookings = getSelectedBookings();
        if (!selectedBookings.isEmpty())
        {
            ArrayList<JobClaim> jobClaims = new ArrayList<>();
            for (Booking booking : selectedBookings)
            {
                final String jobType = booking.getType().name().toLowerCase();
                jobClaims.add(new JobClaim(booking.getId(), jobType));
            }
            final JobClaimRequest jobClaimRequest = new JobClaimRequest(jobClaims);
            mBus.post(new HandyEvent.RequestClaimJobs(jobClaimRequest));
        }
        else
        {
            dismiss();
        }
    }

    @OnClick(R.id.close_button)
    public void onCloseButtonClicked()
    {
        dismiss();
    }

    @Subscribe
    public void onReceiveClaimJobsSuccess(final HandyEvent.ReceiveClaimJobsSuccess event)
    {
        UIUtils.showToast(getActivity(),
                getResources().getQuantityString(R.plurals.claim_jobs_success_formatted,
                event.getJobClaimResponse().getJobs().size()));
        dismiss();
    }

    @Subscribe
    public void onReceiveClaimJobsError(final HandyEvent.ReceiveClaimJobsError event)
    {
        UIUtils.showToast(getActivity(), getString(R.string.claim_jobs_error));
    }

    private void displayJobs()
    {
        mJobsContainer.removeAllViews();
        mBookingsWrapperViewModel = new BookingsWrapperViewModel(
                mPostCheckoutInfo.getSuggestedJobs(), true);
        final OnboardingJobsViewGroup jobsViewGroup = new OnboardingJobsViewGroup(getActivity());
        mJobsContainer.addView(jobsViewGroup);
        jobsViewGroup.setOnJobCheckedChangedListener(this);
        jobsViewGroup.bind(mBookingsWrapperViewModel);
        onJobCheckedChanged();
    }

    @Override
    public void onJobCheckedChanged()
    {
        final int selectedJobsCount = getSelectedBookings().size();
        if (selectedJobsCount > 0)
        {
            mClaimButton.setText(getResources().getQuantityString(R.plurals.claim_jobs_formatted,
                    selectedJobsCount, selectedJobsCount));
        }
        else
        {
            mClaimButton.setText(R.string.continue_to_without_claiming);
        }
    }

    private List<Booking> getSelectedBookings()
    {
        ArrayList<Booking> bookings = new ArrayList<>();
        for (BookingViewModel bookingView : mBookingsWrapperViewModel.getBookingViewModels())
        {
            if (bookingView.isSelected())
            {
                bookings.add(bookingView.getBooking());
            }
        }
        return bookings;
    }
}
