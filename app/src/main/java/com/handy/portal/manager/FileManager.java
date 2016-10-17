package com.handy.portal.manager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.core.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sng on 10/4/16.
 */

public class FileManager
{
    private static final String TAG = FileManager.class.getSimpleName();
    private static final String LOG_PATH = "handylogs";
    private static final File FILES_DIRECTORY = BaseApplication.getContext().getFilesDir();
    private final File mLogDirectory;

    public FileManager()
    {
        mLogDirectory = new File(FILES_DIRECTORY, LOG_PATH);
        if (!mLogDirectory.exists()) { mLogDirectory.mkdirs(); }
    }

    public File[] getLogFileList()
    {
        return mLogDirectory.listFiles();
    }

    /**
     *
     * @param fileName
     * @param fileContent
     * @return true if file saved, otherwise false, which means there was some ioexception
     */
    public boolean saveLogFile(@NonNull String fileName, @NonNull String fileContent)
    {
        //This was simplest way to save in sub directory
        return saveFile(new File(mLogDirectory, fileName), fileContent);
    }

    public void deleteLogFile(@NonNull String fileName)
    {
        new File(mLogDirectory, fileName).delete();
    }

    public String readFile(@NonNull File file)
    {
        StringBuffer buffer = null;
        BufferedReader input = null;
        try
        {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            buffer = new StringBuffer();
            while ((line = input.readLine()) != null)
            {
                buffer.append(line);
            }

            Log.d(TAG, buffer.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return buffer == null ? "" : buffer.toString();
    }

    /**
     * This currently works for internal file saving only. For external directory saving will need more permissions
     * @param file
     * @param fileContent
     * @return
     */
    public boolean saveFile(@NonNull File file, @NonNull String fileContent)
    {
        FileOutputStream outputStream = null;

        try
        {
            if (!file.exists())
            {
                file.createNewFile();  // if file already exists will do nothing
            }

            outputStream = new FileOutputStream(file);
            outputStream.write(fileContent.getBytes());
            return true;
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getLocalizedMessage());
            Crashlytics.log(e.getLocalizedMessage());
        }
        finally
        {
            try
            {
                if (outputStream != null)
                { outputStream.close(); }
            }
            catch (IOException e)
            {
                //ignore
            }
        }

        return false;
    }
}
