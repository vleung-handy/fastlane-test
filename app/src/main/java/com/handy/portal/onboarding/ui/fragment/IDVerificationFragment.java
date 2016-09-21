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
    private String mAfterIdVerificationFinishUrl;


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
            if (mOnboardingDetails != null)
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.CameraPermissionGrantedLog()));
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationStartedLog()));

                SubflowData subflowData =
                        mOnboardingDetails.getSubflowDataByType(SubflowType.ID_VERIFICATION);

                // ID verification start whether or not jumio sdk init works
                if (subflowData != null && !Strings.isNullOrEmpty(subflowData.getBeforeIdVerificationStartUrl()))
                {
                    bus.post(new ProviderSettingsEvent.RequestIdVerificationStart(
                            subflowData.getBeforeIdVerificationStartUrl()
                    ));
                }

                if (NetverifySDK.isSupportedPlatform() && subflowData != null &&
                        !Strings.isNullOrEmpty(subflowData.getJumioSecret())
                        && !Strings.isNullOrEmpty(subflowData.getJumioToken()) &&
                        !Strings.isNullOrEmpty(subflowData.getFullName()) &&
                        !Strings.isNullOrEmpty(subflowData.getCandidateId())
                        && !Strings.isNullOrEmpty(subflowData.getAfterIdVerificationFinishUrl()))
                {
                    try
                    {
                        mAfterIdVerificationFinishUrl = subflowData.getAfterIdVerificationFinishUrl();

                        mNetverifySDK = NetverifySDK.create(getActivity(), subflowData.getJumioToken(),
                                subflowData.getJumioSecret(), JumioDataCenter.US);
                        mNetverifySDK.setName(subflowData.getFullName());
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
                else if (subflowData != null &&
                        !Strings.isNullOrEmpty(subflowData.getAfterIdVerificationFinishUrl()))
                {
                    // Platform not supported or subflow data not valid
                    mAfterIdVerificationFinishUrl = subflowData.getAfterIdVerificationFinishUrl();
                    jumioAfterFinishCallback("", IDVerificationUtils.ID_VERIFICATION_INIT_ERROR);
                    initJumioWebFlow(subflowData);
                }
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
            final String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);

            if (Strings.isNullOrEmpty(scanReference))
            {
                cancel(new Intent());
            }

            if (resultCode == Activity.RESULT_OK)
            {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationCompletedLog()));
                jumioAfterFinishCallback(scanReference, IDVerificationUtils.ID_VERIFICATION_SUCCESS);
                terminate(new Intent());
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                if (data != null)
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
                }

                jumioAfterFinishCallback(scanReference, IDVerificationUtils.ID_VERIFICATION_CANCELLATION);
                cancel(new Intent());
            }

            if (mNetverifySDK != null)
            { mNetverifySDK.destroy(); }
        }
    }

    private void jumioAfterFinishCallback(final String scanReference,
                                          @IDVerificationUtils.IdVerificationStatus final String status)
    {
        // Send scan reference to the server after id verification completed
        if (!Strings.isNullOrEmpty(mAfterIdVerificationFinishUrl))
        {
            bus.post(new ProviderSettingsEvent.RequestIdVerificationFinish(
                    mAfterIdVerificationFinishUrl, scanReference, status));
        }
    }

    private void initJumioBlocker()
    {
        Fragment fragmentByTag = getChildFragmentManager().
                findFragmentByTag(CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
        if (fragmentByTag == null)
        {
            if (mOnboardingDetails != null)
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
