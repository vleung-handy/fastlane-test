package com.handy.portal.data.callback;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * use this instead of Callback to prevent the onCallbackSuccess/onCallbackError methods from being
 * triggered when it is cancelled, or the given activity is in a bad state (finishing, destroyed)
 * <p>
 *
 * @param <T>
 */
public abstract class ActivitySafeCallback<T> extends CancellableCallback<T>
{
    private WeakReference<Activity> mActivityWeakReference;

    public ActivitySafeCallback(@NonNull Activity activity)
    {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @Override
    protected boolean areCallbacksEnabled()
    {
        return super.areCallbacksEnabled()
                && mActivityWeakReference.get() != null
                && !mActivityWeakReference.get().isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 //< api 17
                || !mActivityWeakReference.get().isDestroyed());//requires api 17+
    }
}
