package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderSettings implements Serializable, Cloneable {
    @SerializedName("late_dispatch_opt_in")
    private boolean mLateDispatchOptIn;

    public boolean hasOptedInToLateDispatchNotifications() {
        return mLateDispatchOptIn;
    }

    public void setLateDispatchOptIn(boolean lateDispatchOptIn) {
        mLateDispatchOptIn = lateDispatchOptIn;
    }

    @Override
    public ProviderSettings clone() {
        try {
            // Note: if any of the serialized attributes of this object are objects themselves,
            // you must clone them within here as well.
            return (ProviderSettings) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
