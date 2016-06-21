package com.handy.portal.updater.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppUpdateLog;
import com.handy.portal.updater.AppUpdateEvent;
import com.handy.portal.updater.VersionManager;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PleaseUpdateFragment extends InjectedFragment
{
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final String ANDROID_PACKAGE_INSTALLER_PACKAGE_NAME = "com.android.packageinstaller";
    private static final String ANDROID_PACKAGE_INSTALLER_ACTIVITY_NAME = "com.android.packageinstaller.PackageInstallerActivity";
    @Inject
    VersionManager mVersionManager;

    @Bind(R.id.update_image)
    ImageView mUpdateImage;
    @Bind(R.id.update_button)
    View mUpdateButton;
    @Bind(R.id.update_text)
    TextView mUpdateText;
    @Bind(R.id.grant_access_button)
    Button mGrantAccessButton;
    @Bind(R.id.grant_permissions_section)
    LinearLayout mGrantPermissionsSection;
    @Bind(R.id.install_update_section)
    LinearLayout mInstallUpdateSection;
    @Bind(R.id.manual_download_text)
    TextView manualDownloadText;
    @Bind(R.id.app_update_fragment_update_later_button)
    Button mUpdateLaterButton;

    private boolean mAlreadyAskedPermissions = false;
    private Uri mApkUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_please_update, container);
        ButterKnife.bind(this, view);

        ((AnimationDrawable) mUpdateImage.getBackground()).start();
        checkPermissions();

        mGrantAccessButton.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                requestPermissions();
            }
        });
        Crashlytics.log("Starting the update process");

        // Make link in text clickable
        manualDownloadText.setMovementMethod(LinkMovementMethod.getInstance());

        bus.post(new LogEvent.AddLogEvent(new AppUpdateLog.Shown(getApkDownloadUrl())));

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        if (!mVersionManager.hasRequestedDownload() && !shouldRequestPermissions())
        {
            mGrantPermissionsSection.setVisibility(View.GONE);
            mInstallUpdateSection.setVisibility(View.VISIBLE);
            downloadApk();
        }
        else if (mAlreadyAskedPermissions && mApkUri == null)
        {
            mInstallUpdateSection.setVisibility(View.GONE);
            mGrantPermissionsSection.setVisibility(View.VISIBLE);
        }
        showUpdateLaterButtonForUpdateDetails();
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onReceiveUpdateAvailableSuccess(AppUpdateEvent.ReceiveUpdateAvailableSuccess event)
    {
        showUpdateLaterButtonForUpdateDetails();
        downloadApk();
    }

    @Subscribe
    public void onDownloadUpdateSuccessful(AppUpdateEvent.DownloadUpdateSuccessful event)
    {
        mUpdateImage.setBackgroundResource(R.drawable.img_update_success);
        mUpdateButton.setEnabled(true);
        mUpdateText.setText(R.string.update_copy);
        mUpdateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                installApk();
            }
        });
        mApkUri = mVersionManager.getNewApkUri();
    }

    @Subscribe
    public void onDownloadUpdateFailed(AppUpdateEvent.DownloadUpdateFailed event)
    {
        bus.post(new LogEvent.AddLogEvent(new AppUpdateLog.Failed(getApkDownloadUrl())));
        showToast(R.string.update_failed);
        finishActivity();
    }

    @OnClick(R.id.update_button)
    protected void installApk()
    {
        bus.post(new LogEvent.AddLogEvent(new AppUpdateLog.Started(getApkDownloadUrl())));

        final Intent installIntent = new Intent(Intent.ACTION_VIEW);
        setPackageInstallerComponent(installIntent);

        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(mVersionManager.getNewApkUri(), VersionManager.APK_MIME_TYPE);
        boolean successfullyLaunchedInstallIntent = Utils.safeLaunchIntent(installIntent, getActivity());
        if (!successfullyLaunchedInstallIntent)
        {
            bus.post(new LogEvent.AddLogEvent(new AppUpdateLog.Failed(getApkDownloadUrl())));
        }
    }

    @OnClick(R.id.app_update_fragment_update_later_button)
    protected void onUpdateLaterButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new AppUpdateLog.Skipped(getApkDownloadUrl())));
        finishActivity();
    }

    /**
     * convenience method to get apk download url for the logger events created in this fragment,
     * handling the case when the update details object is null (shouldn't be here, but just to be safe)
     *
     * @return
     */
    private String getApkDownloadUrl()
    {
        return mVersionManager.getUpdateDetails() == null ? null : mVersionManager.getUpdateDetails().getDownloadUrl();
    }

    private void finishActivity()
    {
        getActivity().finish();
    }

    private void setPackageInstallerComponent(final Intent installIntent)
    {
        final ComponentName packageInstallerComponentName =
                new ComponentName(ANDROID_PACKAGE_INSTALLER_PACKAGE_NAME,
                        ANDROID_PACKAGE_INSTALLER_ACTIVITY_NAME);
        try
        {
            final PackageManager packageManager = getActivity().getPackageManager();
            final ActivityInfo activityInfo =
                    packageManager.getActivityInfo(packageInstallerComponentName, 0);
            if (activityInfo != null)
            {
                installIntent.setComponent(packageInstallerComponentName);
            }
            else
            {
                Crashlytics.logException(
                        new RuntimeException(
                                "Unable to use " + ANDROID_PACKAGE_INSTALLER_PACKAGE_NAME));
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Crashlytics.logException(e);
        }
    }

    private void checkPermissions()
    {
        if (shouldRequestPermissions())
        {
            showExternalStorageRequestPermissionAlert();
        }
        else
        {
            downloadApk();
        }
    }

    private void showExternalStorageRequestPermissionAlert()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder
                .setTitle(R.string.allow_external_storage_write_title)
                .setMessage(R.string.allow_external_storage_write_message)
                .setPositiveButton(R.string.allow_external_storage_write_button, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                mAlreadyAskedPermissions = true;
                                requestPermissions();
                            }
                        }
                )
                .create()
                .show();
    }

    private boolean shouldRequestPermissions()
    {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void showUpdateLaterButtonForUpdateDetails()
    {
        if (mVersionManager.getUpdateDetails() == null || mVersionManager.getUpdateDetails().isUpdateBlocking())
        {
            mUpdateLaterButton.setVisibility(View.GONE);
        }
        else
        {
            mUpdateLaterButton.setVisibility(View.VISIBLE);
        }
    }

    /*
    TODO don't like this and the assumptions being made
     */
    private void downloadApk()
    {
        if (mVersionManager.getUpdateDetails() == null)
        {
            bus.post(new AppUpdateEvent.RequestUpdateCheck(getActivity()));
        }
        else
        {
            mVersionManager.downloadApk();
        }
    }
}
