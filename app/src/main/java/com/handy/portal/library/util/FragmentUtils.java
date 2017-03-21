package com.handy.portal.library.util;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;

/**
 * utility class for fragments
 */
public class FragmentUtils {
    /**
     * launches a dialog fragment using the source activity's support fragment manager
     *
     * @param dialogFragment
     * @param sourceActivity
     * @param dialogFragmentTag
     * @return
     */
    public static boolean safeLaunchDialogFragment(
            @NonNull DialogFragment dialogFragment, @NonNull FragmentActivity sourceActivity, String dialogFragmentTag) {
        return safeLaunchDialogFragment(dialogFragment, sourceActivity.getSupportFragmentManager(), dialogFragmentTag);
    }

    /**
     * TODO find out when to use getChildFragmentManager vs getSupportFragmentManager
     * for dialog fragments
     * <p>
     * may have to use this to prevent an IllegalStateException
     * <p>
     * launches a dialog fragment using the source fragment's child fragment manager
     *
     * @param dialogFragment
     * @param sourceFragment
     * @param dialogFragmentTag
     * @return
     */
    public static boolean safeLaunchDialogFragment(
            @NonNull DialogFragment dialogFragment, @NonNull Fragment sourceFragment, String dialogFragmentTag) {
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
                                                    String dialogFragmentTag) {
        try {
            dialogFragment.show(fragmentManager, dialogFragmentTag);
            return true;
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
        return false;
    }

    public static void switchToFragment(
            Fragment currentFragment, Fragment newFragment, boolean addToBackStack) {
        switchToFragment(currentFragment.getFragmentManager(), newFragment, addToBackStack);
    }

    public static void switchToFragment(
            FragmentManager fragmentManager, Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (addToBackStack) {
            transaction.replace(R.id.main_container, newFragment).addToBackStack(null).commit();
        }
        else {
            transaction.replace(R.id.main_container, newFragment).commit();
        }
    }
}
