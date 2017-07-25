package com.handy.portal.clients.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.model.Address;

import java.io.Serializable;

/**
 * Created by sng on 7/18/17
 */

public class Client implements Serializable {
    @SerializedName("id")
    private int mId;

    @SerializedName("first_name")
    @NonNull
    private String mFirstName;

    @SerializedName("last_name")
    @NonNull
    private String mLastName;

    @SerializedName("profile_image_url")
    @Nullable
    private String mProfileImageUrl;

    @SerializedName("layer_user_id")
    @NonNull
    private String mLayerUserId;

    @SerializedName("address")
    @Nullable
    private Address mAddress;

    @SerializedName("context")
    @Nullable
    private Context mContext;

    public int getId() {
        return mId;
    }

    @NonNull
    public String getFirstName() {
        return mFirstName;
    }

    @NonNull
    public String getLastName() {
        return mLastName;
    }

    @Nullable
    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    @NonNull
    public String getLayerUserId() {
        return mLayerUserId;
    }

    @Nullable
    public Address getAddress() {
        return mAddress;
    }

    @Nullable
    public Context getContext() {
        return mContext;
    }

    public class Context implements Serializable {

        @SerializedName("description")
        private String mDescription;

        @SerializedName("type")
        private String mType;

        private ContextType mContextType;

        @NonNull
        public String getDescription() {
            return mDescription;
        }

        @NonNull
        public ContextType getContextType() {
            if(mContextType == null) {
                mContextType = ContextType.fromString(mType);
            }
            return mContextType;
        }
    }

    public enum ContextType implements Serializable {
        UpcomingBooking("upcoming_booking"), LastBooking("last_booking");

        private String mType;

        ContextType(String type) {
            mType = type;
        }

        public static ContextType fromString(String text) {
            for (ContextType b : ContextType.values()) {
                if (b.mType.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return LastBooking;
        }
    }
}
