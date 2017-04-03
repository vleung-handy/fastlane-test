package com.handy.portal.announcements.model;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Announcement implements Serializable {
    @SerializedName("id")
    private String mId;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("subtitle")
    private String mSubtitle;

    /**
     * @see TriggerContext
     */
    @SerializedName("trigger_context")
    private String mTriggerContext;

    public String getId() {
        return mId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    /**
     * represents the context in which this announcement should be displayed
     */
    @Nullable
    public TriggerContext getTriggerContext() {
        try {
            return TriggerContext.fromValue(mTriggerContext);
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
        return null;
    }

    public enum TriggerContext {
        /**
         * this is triggered specifically when the main flow (with the navigation tabs)
         * is opened, and not when on login or onboarding
         */
        MAIN_FLOW_OPEN("app_open"),
        CHECK_IN_NON_REPEAT_CUSTOMER("check_in_non_repeat_customer"),
        ON_MY_WAY("on_my_way"),
        CHECK_IN_REPEAT_CUSTOMER("check_in_repeat_customer");

        private final String mValue;

        TriggerContext(String value) {
            mValue = value;
        }

        static TriggerContext fromValue(@Nullable String value) {
            if (value == null) { return null; }
            for (TriggerContext triggerContext : values()) {
                if (triggerContext.getValue().equalsIgnoreCase(value)) {
                    return triggerContext;
                }
            }
            throw new IllegalArgumentException("No enum found for value " + value);
        }

        private String getValue() {
            return mValue;
        }

        @Override
        public String toString() {
            return mValue;
        }
    }
}
