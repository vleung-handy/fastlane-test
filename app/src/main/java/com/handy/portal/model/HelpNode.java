package com.handy.portal.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public final class HelpNode implements Parcelable
{
    @SerializedName("id") private int id;
    @SerializedName("type") private String type;
    @SerializedName("label") private String label;
    @SerializedName("content") private String content;
    @SerializedName("children") private ArrayList<HelpNode> children;
    @SerializedName("service_name") private String service;
    @SerializedName("date_start") private Date startDate;
    @SerializedName("hrs") private float hours;
    @SerializedName("slt") private String loginToken;

    public HelpNode(){}

    public final int getId() {
        return id;
    }

    @Nullable
    public final String getType() {
        return type;
    }

    public final String getLoginToken() { return loginToken;}

    public final String getLabel() {
        return label;
    }

    public final String getContent() {
        return content;
    }

    public final ArrayList<HelpNode> getChildren() {
        return children;
    }

    public final String getService() {
        return service;
    }

    public final Date getStartDate() {
        return startDate;
    }

    public final float getHours() {
        return hours;
    }

    private HelpNode(final Parcel in) {
        final int[] intData = new int[1];
        in.readIntArray(intData);
        id = intData[0];

        final String[] stringData = new String[5];
        in.readStringArray(stringData);
        type = stringData[0];
        label = stringData[1];
        content = stringData[2];
        service = stringData[3];
        loginToken = stringData[4];

        final float[] floatData = new float[1];
        in.readFloatArray(floatData);
        hours = floatData[0];

        startDate = new Date(in.readLong());

        children = new ArrayList<>();
        in.readTypedList(children, HelpNode.CREATOR);
    }

    public static HelpNode fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, HelpNode.class);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeIntArray(new int[]{id});
        out.writeStringArray(new String[]{type, label, content, service, loginToken});
        out.writeFloatArray(new float[]{hours});
        out.writeLong(startDate != null ? startDate.getTime(): 0);
        out.writeTypedList(children);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public HelpNode createFromParcel(final Parcel in) {
            return new HelpNode(in);
        }
        public HelpNode[] newArray(final int size) {
            return new HelpNode[size];
        }
    };


    public static class HelpNodeType
    {
        public static final String FAQ = "help-faq-container";
        public static final String CTA = "help-cta";
        public static final String CONTACT = "help-contact-form";
        public static final String BOOKINGS_NAV = "dynamic-bookings-navigation";
        public static final String LOG_IN_FORM = "help-log-in-form";
        public static final String ROOT = "root";
        public static final String NAVIGATION = "navigation";
        public static final String BOOKING = "booking";
        public static final String ARTICLE = "article";
    }
}
