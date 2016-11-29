package com.handy.portal.data.callback;

import android.support.annotation.CallSuper;

import com.handy.portal.data.DataManager;

/**
 * use this instead of Callback to prevent the onSuccess() and onError() from being triggered if
 * this callback is cancelled
 *
 * @param <T>
 */
public abstract class CancellableCallback<T> implements DataManager.Callback<T>
{
    private boolean mIsCancelled = false;

    public abstract void onCallbackSuccess(T response);

    public abstract void onCallbackError(DataManager.DataManagerError error);

    /**
     * cancel the callback so that its onCallbackSuccess/onCallbackError methods will not be triggered
     */
    public void cancel()
    {
        mIsCancelled = true;
    }

    /**
     * @return true if the onCallbackSuccess/onCallbackError callbacks should be enabled
     */
    @CallSuper //enforce this to be called when overridden
    protected boolean areCallbacksEnabled()
    {
        return !mIsCancelled;
    }

    @Override
    public final void onSuccess(final T response)
    {
        if (areCallbacksEnabled()) { onCallbackSuccess(response); }
    }

    @Override
    public final void onError(final DataManager.DataManagerError error)
    {
        if (areCallbacksEnabled()) { onCallbackError(error); }
    }
}
