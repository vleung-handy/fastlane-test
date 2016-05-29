package com.handy.portal.library.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * utils related to parcelables
 */
public class ParcelableUtils
{
    @Nullable
    private static byte[] getByteArray(@Nullable Bundle bundle, @Nullable String bundleKey)
    {
        if(bundle == null || bundleKey == null) return null;
        return bundle.getByteArray(bundleKey);
    }

    @Nullable
    public static Parcel unmarshall(@Nullable byte[] byteArray)
    {
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

    public static <T extends Parcelable> T unmarshall(@Nullable Bundle bundle, @Nullable String bundleKey, @NonNull Parcelable.Creator<T> creator)
    {
        return unmarshall(getByteArray(bundle, bundleKey), creator);
    }

    public static <T extends Parcelable> T unmarshall(byte[] bytes, @NonNull Parcelable.Creator<T> creator)
    {
        Parcel parcel = unmarshall(bytes);
        if(parcel == null) return null;
        return creator.createFromParcel(parcel);
    }
}
