package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
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
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.onboarding.ui.view.OnboardingJobsViewGroup;
import com.handy.portal.onboarding.viewmodel.BookingViewModel;
import com.handy.portal.onboarding.viewmodel.BookingsWrapperViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostCheckoutDialogFragment extends InjectedDialogFragment
        implements OnboardingJobsViewGroup.OnJobCheckedChangedListener
{
    public static final String TAG = PostCheckoutDialogFragment.class.getSimpleName();
    private static final String KEY_POST_CHECKOUT_INFO = "post_checkout_info";
    private PostCheckoutInfo mPostCheckoutInfo;
    private BookingsWrapperViewModel mBookingsWrapperViewModel;

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

    private void displayJobs()
    {
        mJobsContainer.removeAllViews();
        mBookingsWrapperViewModel = new BookingsWrapperViewModel(
                mPostCheckoutInfo.getSuggestedJobs(), true);
        final OnboardingJobsViewGroup jobsViewGroup = new OnboardingJobsViewGroup(getActivity());
        mJobsContainer.addView(jobsViewGroup);
        jobsViewGroup.setOnJobCheckedChangedListener(this);
        jobsViewGroup.bind(mBookingsWrapperViewModel);
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
