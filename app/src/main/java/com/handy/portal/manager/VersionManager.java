package com.handy.portal.manager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.google.common.annotations.VisibleForTesting;
import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.util.CheckApplicationCapabilitiesUtils;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

public class VersionManager
{
    //TODO: parameterize these strings
    public static final String APK_MIME_TYPE = "application/vnd.android.package-archive";
    private static final String APK_FILE_NAME = "handy-pro-latest.apk";

    // This backoff duration is used to prevent the app from executing download repeatedly when
    // download fails. It is used to check whether there was a download attempt recently and if so,
    // doesn't execute the update process.
    private static final int UPDATE_CHECK_BACKOFF_DURATION_MILLIS = DateTimeUtils.MILLISECONDS_IN_MINUTE*5; // 5 minutes
    private long lastUpdateCheckTimeMillis = 0;

    private final Context context;
    private final Bus bus;
    private final BuildConfigWrapper buildConfigWrapper;
    private DataManager dataManager;
    private PrefsManager prefsManager;
    private DownloadManager downloadManager;
    private String mDownloadUrl;

    private long downloadReferenceId;

    @Inject
    public VersionManager(final Context context, final Bus bus, final DataManager dataManager, final PrefsManager prefsManager, final BuildConfigWrapper buildConfigWrapper)
    {
        this.context = context;
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        this.buildConfigWrapper = buildConfigWrapper;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public Uri getNewApkUri()
    {
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDirectory, APK_FILE_NAME);
        return Uri.fromFile(file);
    }

    @Produce
    public HandyEvent.DownloadUpdateSuccessful produceUpdateDownloadSuccessful()
    {
        if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL)
        {
            return new HandyEvent.DownloadUpdateSuccessful();
        }
        return null;
    }

    @Produce
    public HandyEvent.DownloadUpdateFailed produceUpdateDownloadFailed()
    {
        if (getDownloadStatus() == DownloadManager.STATUS_FAILED)
        {
            return new HandyEvent.DownloadUpdateFailed();
        }
        return null;
    }

    @Subscribe
    public void onUpdateCheckRequest(HandyEvent.RequestUpdateCheck event)
    {
        if (CheckApplicationCapabilitiesUtils.isDownloadManagerEnabled(context))
        {
            // prevent download prompts from showing continuously if download fails if download manager is already enabled
            if (CheckApplicationCapabilitiesUtils.isExternalStorageWritable())
            {
                // TODO: Make request back-offs better
                long now = new Date().getTime();
                if (lastUpdateCheckTimeMillis > 0 && now - lastUpdateCheckTimeMillis < UPDATE_CHECK_BACKOFF_DURATION_MILLIS)
                {
                    return;
                }

                lastUpdateCheckTimeMillis = now;

                PackageInfo pkgInfo = getPackageInfoFromActivity(event.sender);
                dataManager.checkForUpdates(buildConfigWrapper.getFlavor(), pkgInfo.versionCode, new DataManager.Callback<UpdateDetails>()
                        {
                            @Override
                            public void onSuccess(final UpdateDetails updateDetails)
                            {
                                if (updateDetails.getShouldUpdate())
                                {
                                    mDownloadUrl = updateDetails.getDownloadUrl();
                                    bus.post(new HandyEvent.ReceiveUpdateAvailableSuccess(updateDetails));
                                }
                            }

                            @Override
                            public void onError(final DataManager.DataManagerError error)
                            {
                                bus.post(new HandyEvent.ReceiveUpdateAvailableError(error));
                            }
                        }
                );
            }
            else
            {
                bus.post(new HandyEvent.ReceiveUpdateAvailableError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.OTHER,
                        context.getString(R.string.error_update_failed_unwritable))));
            }

        }
        else
        {
            bus.post(new HandyEvent.RequestEnableApplication(CheckApplicationCapabilitiesUtils.DOWNLOAD_MANAGER_PACKAGE_NAME, context.getString(R.string.error_update_failed_download_manager_disabled)));
        }


    }

    @Subscribe
    public void onApplicationResumed(HandyEvent.ApplicationResumed event)
    {
        dataManager.sendVersionInformation(getVersionInfo());
    }

    @Subscribe
    public void onReceiveLoginSuccess(HandyEvent.ReceiveLoginSuccess event)
    {
        dataManager.sendVersionInformation(getVersionInfo());
    }

    public void downloadApk()
    {
        String apkUrl = getDownloadUrl();
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        downloadsDirectory.mkdirs();

        File oldApkFile = new File(downloadsDirectory, APK_FILE_NAME);
        if (oldApkFile.exists())
        {
            oldApkFile.delete();
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setMimeType(APK_MIME_TYPE)
                .setAllowedOverRoaming(false)
                .setVisibleInDownloadsUi(false)
                .setTitle(context.getString(R.string.app_update))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILE_NAME);

        downloadReferenceId = downloadManager.enqueue(request);

        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @VisibleForTesting
    protected BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReferenceId == referenceId)
            {
                if (getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL)
                {
                    bus.post(new HandyEvent.DownloadUpdateSuccessful());
                }
                else
                {
                    bus.post(new HandyEvent.DownloadUpdateFailed());
                }
            }
        }
    };

    private int getDownloadStatus()
    {
        if (CheckApplicationCapabilitiesUtils.isDownloadManagerEnabled(context))
        {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadReferenceId);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst())
            {
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        }

        return -1;
    }

    private PackageInfo getPackageInfoFromActivity(Context context)
    {
        PackageInfo pInfo;
        try
        {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }
        return pInfo;
    }

    private HashMap<String, String> getVersionInfo()
    {
        PackageInfo pInfo = getPackageInfoFromActivity(context);
        HashMap<String, String> info = new HashMap<>();
        info.put("user_id", prefsManager.getString(PrefsKey.LAST_PROVIDER_ID));
        info.put("platform", "android");
        info.put("app_identifier", context.getPackageName());
        info.put("app_version", pInfo.versionName);
        info.put("app_version_code", Integer.toString(pInfo.versionCode));
        info.put("app_flavor", buildConfigWrapper.getFlavor());
        info.put("os_version", android.os.Build.VERSION.RELEASE);
        info.put("os_version_code", Integer.toString(android.os.Build.VERSION.SDK_INT));
        return info;
    }

    public String getDownloadUrl()
    {
        return mDownloadUrl;
    }

    public boolean hasRequestedDownload()
    {
        return downloadReferenceId != 0;
    }
}
