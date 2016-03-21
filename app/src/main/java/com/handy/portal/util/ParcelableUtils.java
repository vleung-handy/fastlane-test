package com.handy.portal.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ParcelableUtils
{
    @Nullable
    public static Parcel unmarshall(@Nullable Bundle bundle, @Nullable String bundleKey)
    {
        if(bundle == null || bundleKey == null) return null;
        byte[] byteArray = bundle.getByteArray(bundleKey);
        if (byteArray == null) { return null; }
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(byteArray, 0, byteArray.length);
        parcel.setDataPosition(0);
        return parcel;
    }

    public static byte[] marshall(@NonNull Parcelable parcelable)
    {
        Parcel parcel = Parcel.obtain();
        parcelable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }
}
