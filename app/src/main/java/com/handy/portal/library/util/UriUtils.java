package com.handy.portal.library.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * utility methods relating to Uri
 */

public class UriUtils
{
    /**
     * gets Uri from the given file, taking into account heightened file security in API 24+
     * @param fileProviderAuthority this should match the authority defined in the app's AndroidManifest.xml
     *                                  currently we don't know how to get this independent of app
     *                                  so we are passing this as a parameter so that this function
     *                                  can be in the library package, which should be independent of app
     * @return
     */
    public static Uri getUriFromFile(@NonNull Context context, @NonNull File file,
                                     @NonNull String fileProviderAuthority)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            //for devices with api <24
            return Uri.fromFile(file);
        }
        else
        {
            /*
            devices with api 24 need to generate the uri this way
            or a FileUriExposedException will be thrown

            URI generated from the file provider not compatible with devices with api <24
            nothing happens when we try to launch the install intent
            and getting the error "Unsupported scheme content" from package installer
             */

            return FileProvider.getUriForFile(context, fileProviderAuthority, file);
        }
    }
}
