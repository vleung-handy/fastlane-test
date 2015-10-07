package com.handy.portal.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class IOUtils
{
    public static String loadJSONFromAsset(Context context, String filename) throws IOException
    {
        InputStream is = context.getAssets().open(filename);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }
}
