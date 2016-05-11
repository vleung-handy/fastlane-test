package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.crashlytics.android.Crashlytics;

/**
 * utility class for fragments
 */
public class FragmentUtils
{
    /**
     * launches a dialog fragment using the source activity's support fragment manager
     * @param dialogFragment
     * @param sourceActivity
     * @param dialogFragmentTag
     * @return
     */
    public static boolean safeLaunchDialogFragment(
            @NonNull DialogFragment dialogFragment, @NonNull FragmentActivity sourceActivity, String dialogFragmentTag)
    {
        return safeLaunchDialogFragment(dialogFragment, sourceActivity.getSupportFragmentManager(), dialogFragmentTag);
    }

    /**
     * TODO find out when to use getChildFragmentManager vs getSupportFragmentManager
     * for dialog fragments
     *
     * may have to use this to prevent an IllegalStateException
     *
     * launches a dialog fragment using the source fragment's child fragment manager
     * @param dialogFragment
     * @param sourceFragment
     * @param dialogFragmentTag
     * @return
     */
    public static boolean safeLaunchDialogFragment(
            @NonNull DialogFragment dialogFragment, @NonNull Fragment sourceFragment, String dialogFragmentTag)
    {
        return safeLaunchDialogFragment(dialogFragment, sourceFragment.getChildFragmentManager(), dialogFragmentTag);
    }

    /**
     * wrapper method for launching dialog fragments
     * to prevent crash due to IllegalStateException
     * due to this fragment transaction being performed in an asynchronous
     * callback that might be called after onSaveInstanceState()
     *
     * @return true if the DialogFragment was successfully launched
     */
    private static boolean safeLaunchDialogFragment(@NonNull DialogFragment dialogFragment,
                                                    @NonNull FragmentManager fragmentManager,
                                                    String dialogFragmentTag)
    {
        try
        {
            dialogFragment.show(fragmentManager, dialogFragmentTag);
            return true;
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
        return false;
    }
}
