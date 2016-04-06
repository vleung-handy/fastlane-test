package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;

import butterknife.Bind;

public class ConfirmBookingCancelDialogFragment extends ConfirmBookingActionDialogFragment
{
    public static final String FRAGMENT_TAG = ConfirmBookingCancelDialogFragment.class.getSimpleName();

    @Bind(R.id.old_keep_rate)
    TextView mOldKeepRate;
    @Bind(R.id.new_keep_rate)
    TextView mNewKeepRate;
    @Bind(R.id.no_keep_rate)
    TextView mNoKeepRate;

    public static ConfirmBookingCancelDialogFragment newInstance(final Booking booking)
    {
        ConfirmBookingCancelDialogFragment fragment = new ConfirmBookingCancelDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        final Booking.Action action = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final Booking.Action.Extras.KeepRate keepRate = action.getKeepRate();
        final Float oldKeepRate = keepRate.getActual();
        final Float newKeepRate = keepRate.getOnNextUnassign();
        if (oldKeepRate != null && newKeepRate != null)
        {
            final String oldKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(oldKeepRate * 100));
            final String newKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(newKeepRate * 100));
            mOldKeepRate.setText(oldKeepRateFormatted);
            mNewKeepRate.setText(newKeepRateFormatted);
            mNoKeepRate.setVisibility(View.GONE);
        }
        else
        {
            mOldKeepRate.setText(R.string.empty_keep_rate_percent);
            mNoKeepRate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected View getBookingActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_cancel, container, false);
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        Toast.makeText(getActivity(), "", Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.color.error_red;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.cancel_job);
    }
}
