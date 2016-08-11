package com.handy.portal.onboarding.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Strings;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.ProviderSettingsEvent;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.IDVerificationUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowType;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.data.document.NVDocumentType;

import java.util.ArrayList;

public class IDVerificationFragment extends OnboardingSubflowFragment
{
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 42;

    private boolean requestedCameraPermissions;
    private boolean showCameraBlocker;
    private OnboardingDetails mOnboardingDetails;
    private NetverifySDK mNetverifySDK;
    private String mAfterFinishUrl;


    public static IDVerificationFragment newInstance()
    {
        return new IDVerificationFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mOnboardingDetails = getOnboardingDetails();
        initializeJumioIDVerification();
    }

    private void initializeJumioIDVerification()
    {
        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationStartedLog()));

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (!requestedCameraPermissions)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
                requestedCameraPermissions = true;
            }
            else
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.CameraPermissionDeniedLog()));
                initJumioBlocker();
            }
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.CameraPermissionGrantedLog()));

            SubflowData subflowData =
                    mOnboardingDetails.getSubflowDataByType(SubflowType.ID_VERIFICATION);

            if (NetverifySDK.isSupportedPlatform() && subflowData != null &&
                    !Strings.isNullOrEmpty(subflowData.getJumioSecret())
                    && !Strings.isNullOrEmpty(subflowData.getJumioToken()) &&
                    !Strings.isNullOrEmpty(subflowData.getFullName())
                    && !Strings.isNullOrEmpty(subflowData.getScanReference()) &&
                    !Strings.isNullOrEmpty(subflowData.getCandidateId()) &&
                    !Strings.isNullOrEmpty(subflowData.getAfterFinishUrl()))
            {
                try
                {
                    mAfterFinishUrl = subflowData.getAfterFinishUrl();

                    mNetverifySDK = NetverifySDK.create(getActivity(), subflowData.getJumioToken(),
                            subflowData.getJumioSecret(), JumioDataCenter.US);
                    mNetverifySDK.setName(subflowData.getFullName());
                    mNetverifySDK.setMerchantScanReference(subflowData.getScanReference());
                    mNetverifySDK.setCustomerId(subflowData.getCandidateId());

                    mNetverifySDK.setEnableEpassport(true);
                    ArrayList<NVDocumentType> documentTypes = new ArrayList<>();
                    documentTypes.add(NVDocumentType.PASSPORT);
                    documentTypes.add(NVDocumentType.DRIVER_LICENSE);
                    documentTypes.add(NVDocumentType.IDENTITY_CARD);
                    mNetverifySDK.setPreselectedDocumentTypes(documentTypes);

                    mNetverifySDK.setRequireFaceMatch(true);
                    mNetverifySDK.setRequireVerification(true);

                    startActivityForResult(mNetverifySDK.getIntent(), NetverifySDK.REQUEST_CODE);
                }
                catch (PlatformNotSupportedException e)
                {
                    Crashlytics.logException(e);
                    IDVerificationUtils.initJumioWebFlow(getContext(), subflowData.getJumioURL());
                }
                catch (MissingPermissionException e)
                {
                    initJumioBlocker();
                }
            }
            else if (subflowData != null && !Strings.isNullOrEmpty(subflowData.getJumioURL()))
            {
                // Platform not supported or subflow data not valid
                initJumioWebFlow(subflowData);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (showCameraBlocker && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            showCameraBlocker = false;
            Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
            if (fragmentByTag != null && fragmentByTag instanceof CameraPermissionsBlockerDialogFragment)
            {
                ((CameraPermissionsBlockerDialogFragment) fragmentByTag).dismiss();
            }
            initializeJumioIDVerification();
        }
        if (showCameraBlocker)
        {
            initJumioBlocker();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                initializeJumioIDVerification();
            }
            else
            {
                // Camera permission is required else blocked
                showCameraBlocker = true;
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NetverifySDK.REQUEST_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);
                if (Strings.isNullOrEmpty(scanReference))
                {
                    cancel(new Intent());
                }
                else
                {
                    // Send scan reference to the server after id verification completed
                    bus.post(new ProviderSettingsEvent.RequestIdVerificationFinish(
                            mSubflowData.getAfterFinishUrl(), scanReference));

                    bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationCompletedLog()));
                    terminate(new Intent());
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                int errorCode =
                        data.getIntExtra(NetverifySDK.EXTRA_ERROR_CODE, 0);
                String errorMessage =
                        data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);

                if (errorCode != 0 && !Strings.isNullOrEmpty(errorMessage))
                { Crashlytics.log(errorMessage); }

                // Cancelled by user
                if (errorCode == 250)
                {
                    bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationCancelledLog()));
                }
                else
                {
                    bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationFailedLog()));
                }

//                if ((errorCode >= 100 && errorCode <= 160) || errorCode == 230)
//                {
//                    // Retry possible
//                    initializeJumioIDVerification();
//                }
//                else
//                {
//                    // Retry impossible
//                    initJumioBlocker();
//                }

                cancel(new Intent());
            }
            mNetverifySDK.destroy();
        }
    }

    private void initJumioBlocker()
    {
        Fragment fragmentByTag = getChildFragmentManager().
                findFragmentByTag(CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
        if (fragmentByTag == null)
        {
            CameraPermissionsBlockerDialogFragment fragment = new CameraPermissionsBlockerDialogFragment();

            SubflowData subflowData =
                    mOnboardingDetails.getSubflowDataByType(SubflowType.ID_VERIFICATION);
            if (subflowData != null && !Strings.isNullOrEmpty(subflowData.getJumioURL()))
            {
                Bundle args = new Bundle();
                args.putString(BundleKeys.JUMIO_URL, subflowData.getJumioURL());
                fragment.setArguments(args);
            }

            FragmentUtils.safeLaunchDialogFragment(fragment, this,
                    CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    private void initJumioWebFlow(SubflowData subflowData)
    {
        if (subflowData != null && !Strings.isNullOrEmpty(subflowData.getJumioURL()))
        {
            bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.WebIDVerificationFlowStarted()));
            IDVerificationUtils.initJumioWebFlow(getContext(), subflowData.getJumioURL());
        }
    }
}
