package com.handy.portal.bookings.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingClaimDialogFragment;
import com.handy.portal.bookings.ui.fragment.dialog.SwapBookingClaimDialogFragment;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.library.util.FragmentUtils;

public class ClaimUtils
{
    /**
     * shows the confirm booking claim dialog if the cancellation policy data is there, based on the given booking
     *
     * @return true if the confirm dialog is shown/is showing, false otherwise
     */
    public static boolean showConfirmBookingClaimDialogIfNecessary(
            final Booking booking,
            final Fragment fragment,
            final FragmentManager fragmentManager)
    {
        final Booking.Action claimAction = booking.getAction(Booking.Action.ACTION_CLAIM);
        if (booking.canSwap())
        {
            if (fragmentManager
                    .findFragmentByTag(SwapBookingClaimDialogFragment.FRAGMENT_TAG) == null)
            {
                final SwapBookingClaimDialogFragment dialogFragment =
                        SwapBookingClaimDialogFragment.newInstance(booking);
                dialogFragment.setTargetFragment(fragment, RequestCode.CONFIRM_SWAP);
                FragmentUtils.safeLaunchDialogFragment(dialogFragment, fragment,
                        SwapBookingClaimDialogFragment.FRAGMENT_TAG);
            }
            return true;
        }
        else if (claimAction != null && claimAction.getExtras() != null)
        {
            final Booking.Action.Extras.CancellationPolicy cancellationPolicy =
                    claimAction.getExtras().getCancellationPolicy();
            if (cancellationPolicy != null)
            {
                // Cancellation policy is accessed within ConfirmBookingCancellationPolicyDialogFragment
                if (fragmentManager
                        .findFragmentByTag(ConfirmBookingClaimDialogFragment.FRAGMENT_TAG) == null)
                {
                    ConfirmBookingActionDialogFragment confirmBookingDialogFragment =
                            ConfirmBookingClaimDialogFragment.newInstance(booking);
                    confirmBookingDialogFragment
                            .setTargetFragment(fragment, RequestCode.CONFIRM_REQUEST);
                    FragmentUtils.safeLaunchDialogFragment(confirmBookingDialogFragment, fragment,
                            ConfirmBookingClaimDialogFragment.FRAGMENT_TAG);
                }
                return true;
            }
        }
        return false;
    }
}
