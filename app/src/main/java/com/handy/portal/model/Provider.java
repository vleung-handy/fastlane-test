package com.handy.portal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Provider implements Parcelable
{
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;

//TODO: add more fields
    public String getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getAbbreviatedName()
    {
        return firstName + (lastName.isEmpty() ? "" : " " + lastName.charAt(0) + ".");
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{id, email, firstName, lastName});
    }
}
