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

import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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

    public Uri getNewApkUri()
    {
        return downloadManager.getUriForDownloadedFile(downloadReference);
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
                    if (updateDetails.getShouldUpdate()) {
                        downloadApk(updateDetails.getDownloadUrl());
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
    public void onApplicationResumed(Event.ApplicationResumed event) {

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
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setMimeType(APK_MIME_TYPE);
        request.setAllowedOverRoaming(false);
        request.setTitle("Portal Update");
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_FILE_NAME);

        downloadReference = downloadManager.enqueue(request);

        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId)
            {
                bus.post(new Event.UpdateAvailable());
            }
        }
    };

    private PackageInfo getPackageInfoFromActivity(Activity sender) {
        PackageInfo pInfo = null;
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
