package com.handy.portal.library.util;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {
    public static String loadJSONFromAsset(Context context, String filename) throws IOException {
        InputStream is = context.getAssets().open(filename);
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    public static void copyFile(final InputStream from, final File to) {
        try {
            try {
                final OutputStream output = new FileOutputStream(to);
                try {
                    byte[] buffer = new byte[1024];
                    int read;

                    while ((read = from.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                }
                finally {
                    output.close();
                }
            }
            finally {
                from.close();
            }
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }

    }
}
