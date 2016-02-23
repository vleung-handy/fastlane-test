package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;

/**
 * utility class for fragments
 */
public class FragmentUtils
{
    /**
     * wrapper method for launching dialog fragments
     * to prevent crash due to IllegalStateException
     * due to this fragment transaction being performed in an asynchronous
     * callback that might be called after onSaveInstanceState()
     * @param dialogFragment
     * @param activity
     * @param tag
     * @return true if the DialogFragment was successfully launched
     */
    public static boolean safeLaunchDialogFragment(
            @NonNull DialogFragment dialogFragment, @NonNull FragmentActivity activity, String tag)
    {
        try
        {
            dialogFragment.show(activity.getSupportFragmentManager(), tag);
            return true;
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
        return false;
    }
}
