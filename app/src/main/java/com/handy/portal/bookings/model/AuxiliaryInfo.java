package com.handy.portal.bookings.model;

import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;

import java.io.Serializable;

/**
 * This is initially used for the holding "metadata" information about a booking, whether it's
 * a booking referred by someone that favorited this pro
 */
public class AuxiliaryInfo implements Serializable {

    public enum AuxType {
        @SerializedName("favorite")FAVORITE,
        @SerializedName("referral")REFERRAL
    }

    @SerializedName("text")
    private String mText;

    @SerializedName("type")
    private AuxType mType;

    public String getText() {
        return mText;
    }

    public AuxType getType() {
        return mType;
    }

    @DrawableRes
    public int getIconDrawableRes() {
        if (mType == null) {
            return 0;
        }
        switch (mType) {
            case FAVORITE:
                return R.drawable.ic_aux_info_fav;
            case REFERRAL:
                return R.drawable.ic_aux_info_ref;
            default:
                return 0;
        }
    }
}
