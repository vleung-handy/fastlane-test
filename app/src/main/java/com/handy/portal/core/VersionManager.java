package com.handy.portal.core;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

import com.google.common.annotations.VisibleForTesting;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.HashMap;

import javax.inject.Inject;

public class VersionManager
{
    public static final String APK_MIME_TYPE = "application/vnd.android.package-archive";
    public static final String APK_FILE_NAME = "handy-pro-latest.apk";

    private final Context context;
    private final Bus bus;
    private final BuildConfigWrapper buildConfigWrapper;
    private DataManager dataManager;

    @Inject
    VersionManager(final Context context, final Bus bus, final DataManager dataManager, final BuildConfigWrapper buildConfigWrapper)
    {
        this.context = context;
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
        this.buildConfigWrapper = buildConfigWrapper;
    }

    private long downloadReference;
    private DownloadManager downloadManager;
    private boolean downloadComplete;

    public Uri getNewApkUri()
    {
        return downloadManager.getUriForDownloadedFile(downloadReference);
    }

    @Produce
    public Event.UpdateReady produceUpdateReady()
    {
        if (downloadComplete)
        {
            return new Event.UpdateReady();
        }
        return null;
    }

    @Subscribe
    public void onUpdateCheckRequest(Event.UpdateCheckEvent event)
    {

        PackageInfo pkgInfo = getPackageInfoFromActivity(event.sender);

        dataManager.checkForUpdates(buildConfigWrapper.getFlavor(), pkgInfo.versionCode, new DataManager.Callback<UpdateDetails>()
                {
                    @Override
                    public void onSuccess(final UpdateDetails updateDetails)
                    {
                        if (updateDetails.getShouldUpdate())
                        {
                            downloadApk(updateDetails.getDownloadUrl());
                            bus.post(new Event.UpdateAvailable());
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        //TODO: ERROR MESSAGE
                    }
                }
        );
    }

    @Subscribe
    public void onApplicationResumed(Event.ApplicationResumed event)
    {

        PackageInfo pInfo = getPackageInfoFromActivity(event.sender);

        HashMap<String, String> info = new HashMap<>();
        info.put("user_id", dataManager.getProviderId());
        info.put("platform", "android");
        info.put("app_identifier", event.sender.getPackageName());
        info.put("app_version", pInfo.versionName);
        info.put("app_version_code", Integer.toString(pInfo.versionCode));
        info.put("app_flavor", buildConfigWrapper.getFlavor());
        info.put("os_version", android.os.Build.VERSION.RELEASE);
        info.put("os_version_code", Integer.toString(android.os.Build.VERSION.SDK_INT));

        dataManager.sendVersionInformation(info, new DataManager.Callback<SimpleResponse>()
                {
                    @Override
                    public void onSuccess(final SimpleResponse updateDetails)
                    {
                        //Do nothing
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        //Do nothing
                    }
                }
        );
    }

    public void downloadApk(String apkUrl)
    {
        downloadComplete = false;

        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        downloadsDirectory.mkdirs();

        File oldApkFile = new File(downloadsDirectory, APK_FILE_NAME);
        if (oldApkFile.exists())
        {
            oldApkFile.delete();
        }

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl))
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setMimeType(APK_MIME_TYPE)
                .setAllowedOverRoaming(false)
                .setVisibleInDownloadsUi(false)
                .setTitle("Portal Update")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, APK_FILE_NAME);

        downloadReference = downloadManager.enqueue(request);

        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @VisibleForTesting
    protected BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId)
            {
                downloadComplete = true;
                bus.post(new Event.UpdateReady());
            }
        }
    };

    private PackageInfo getPackageInfoFromActivity(Activity sender)
    {
        PackageInfo pInfo;
        try
        {
            pInfo = sender.getPackageManager().getPackageInfo(sender.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }
        return pInfo;
    }
}
