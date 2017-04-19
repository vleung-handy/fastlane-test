package com.handy.portal.bookings.model;


import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("email")
    private String mEmail;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("phone_str")
    private String mPhone;
    @SerializedName("id")
    private String mId;
    @SerializedName("layer_user_id")
    private String mLayerUserId;

    public String getEmail() {
        return mEmail;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getId() {
        return mId;
    }

    public String getLayerUserId() {
        return mLayerUserId;
    }

    public String getAbbreviatedName() {
        String lastInitial = TextUtils.isEmpty(mLastName) ? "" : mLastName.charAt(0) + ".";
        return mFirstName + " " + lastInitial;
    }
}
