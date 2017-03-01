package com.handy.portal.core.util;

import android.content.Context;
import android.support.annotation.NonNull;

public class FileProviderUtils {
    /**
     * returns the file provider authority for this app specifically
     * <p>
     * TODO don't know how to get the file provider authority independently of the application,
     * so leaving this utility outside of the library package (which should only contain non-application specific stuff)
     */
    @NonNull
    public static String getApplicationFileProviderAuthority(@NonNull Context context) {
        //this should match the authority defined in AndroidManifest.xml
        return context.getApplicationContext().getPackageName() + ".provider";
    }
}
