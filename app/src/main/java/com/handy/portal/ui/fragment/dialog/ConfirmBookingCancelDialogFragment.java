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
import com.handy.portal.util.CurrencyUtils;

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
    @Bind(R.id.no_fee_notice)
    View mNoFeeNotice;
    @Bind(R.id.withholding_fee_notice)
    View mWithholdingFeeNotice;
    @Bind(R.id.withholding_fee)
    TextView mWithholdingFee;

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
        displayWithholdingNotice();
        displayKeepRate();
    }

    private void displayWithholdingNotice()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final int withholdingAmount = removeAction.getWithholdingAmount();
        if (withholdingAmount > 0)
        {
            final String currencySymbol = mBooking.getPaymentToProvider().getCurrencySymbol();
            final String fee = CurrencyUtils.formatPriceWithCents(withholdingAmount, currencySymbol);
            final String feeFormatted = getString(R.string.withholding_fee_simple_formatted, fee);
            mWithholdingFee.setText(feeFormatted);
            mNoFeeNotice.setVisibility(View.GONE);
            mWithholdingFeeNotice.setVisibility(View.VISIBLE);
        }
        else
        {
            mWithholdingFeeNotice.setVisibility(View.GONE);
            mNoFeeNotice.setVisibility(View.VISIBLE);
        }
    }

    private void displayKeepRate()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
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
