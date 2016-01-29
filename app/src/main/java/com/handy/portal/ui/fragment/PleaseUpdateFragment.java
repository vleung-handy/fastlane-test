package com.handy.portal.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PleaseUpdateFragment extends InjectedFragment
{
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    @Inject
    VersionManager mVersionManager;

    @Bind(R.id.update_image)
    ImageView mUpdateImage;
    @Bind(R.id.update_button)
    Button mUpdateButton;
    @Bind(R.id.update_text)
    TextView mUpdateText;
    @Bind(R.id.grant_access_button)
    Button mGrantAccessButton;
    @Bind(R.id.grant_permissions_section)
    LinearLayout mGrantPermissionsSection;
    @Bind(R.id.install_update_section)
    LinearLayout mInstallUpdateSection;

    private boolean mAlreadyAskedPermissions = false;
    private Uri mApkUri;

    private static final String MANUAL_DOWNLOAD_URL = "https://www.handy.com/p";

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

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
    }

    @Subscribe
    public void onReceiveUpdateAvailableSuccess(HandyEvent.ReceiveUpdateAvailableSuccess event)
    {
        downloadApk();
    }

    @Subscribe
    public void onDownloadUpdateSuccessful(HandyEvent.DownloadUpdateSuccessful event)
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
    public void onDownloadUpdateFailed(HandyEvent.DownloadUpdateFailed event)
    {
        // Fallback to opening link and downloading manually
        mUpdateText.setText(R.string.download_update_manually);
        mUpdateButton.setText(getResources().getString(R.string.download_update));
        mUpdateButton.setEnabled(true);
        mUpdateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                downloadApkManually();
            }
        });

        // Button shake/wiggle to grab attention
        Animation shakeAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        mUpdateButton.startAnimation(shakeAnimation);
    }

    protected void installApk()
    {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            ComponentName comp = new ComponentName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
            installIntent.setComponent(comp);
        }

        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setDataAndType(mVersionManager.getNewApkUri(), VersionManager.APK_MIME_TYPE);
        Utils.safeLaunchIntent(installIntent, getActivity());
    }

    protected void downloadApkManually()
    {
        Uri uri = Uri.parse(MANUAL_DOWNLOAD_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        getActivity().finish();
        startActivity(intent);
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

    private void downloadApk()
    {
        if (mVersionManager.getDownloadUrl() == null)
        {
            bus.post(new HandyEvent.RequestUpdateCheck(getActivity()));
        }
        else
        {
            mVersionManager.downloadApk();
        }
    }
}
