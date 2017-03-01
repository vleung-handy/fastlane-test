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
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.ProviderSettingsEvent;
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

public class IDVerificationFragment extends OnboardingSubflowFragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 42;

    private boolean requestedCameraPermissions;
    private boolean showCameraBlocker;
    private OnboardingDetails mOnboardingDetails;
    private NetverifySDK mNetverifySDK;
    private String mAfterIdVerificationFinishUrl;


    public static IDVerificationFragment newInstance() {
        return new IDVerificationFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnboardingDetails = getOnboardingDetails();
        initializeJumioIDVerification();
    }

    private void initializeJumioIDVerification() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (!requestedCameraPermissions) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
                requestedCameraPermissions = true;
            }
            else {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.CameraPermissionDeniedLog()));
                initJumioPermissionsBlocker();
            }
        }
        else {
            //has camera permissions
            if (mOnboardingDetails != null) {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.CameraPermissionGrantedLog()));
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationStartedLog()));

                SubflowData subflowData =
                        mOnboardingDetails.getSubflowDataByType(SubflowType.ID_VERIFICATION);

                if (subflowData != null) {
                    if (!Strings.isNullOrEmpty(subflowData.getBeforeIdVerificationStartUrl())) {
                        bus.post(new ProviderSettingsEvent.RequestIdVerificationStart(
                                subflowData.getBeforeIdVerificationStartUrl()
                        ));
                    }

                    if (isNativeJumioFlowSupported(subflowData)) {
                        startNativeJumioFlow(subflowData);
                    }
                    else {
                        //jumio native flow not supported
                        startJumioWebFallbackFlow(subflowData);
                    }
                }
                else {
                    //subflow data null
                    showToast(R.string.error_missing_server_data);
                    Crashlytics.logException(new Exception("id verification subflow data is null"));
                }
            }
            else {
                //onboarding details null
                showToast(R.string.error_missing_server_data);
                Crashlytics.logException(new Exception("onboarding details is null in id verification fragment"));
            }
        }
    }

    private void startJumioWebFallbackFlow(@NonNull SubflowData subflowData) {
        if (!Strings.isNullOrEmpty(subflowData.getAfterIdVerificationFinishUrl())) //check if fallback flow available
        {
            /*
            note: leaving this outside of the if statement check below to maintain previous logic
            because i don't know exactly how this is used
             */
            mAfterIdVerificationFinishUrl = subflowData.getAfterIdVerificationFinishUrl();
            jumioAfterFinishCallback("", IDVerificationUtils.ID_VERIFICATION_INIT_ERROR);
            if (!Strings.isNullOrEmpty(subflowData.getJumioURL())) {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.WebIDVerificationFlowStarted()));
                IDVerificationUtils.initJumioWebFlow(getContext(), subflowData.getJumioURL());
            }
            else {
                showToast(R.string.error_missing_server_data);
                Crashlytics.logException(new Exception("unable to start jumio web fallback flow because jumio url is null or empty"));
            }
        }
        else {
            showToast(R.string.error_missing_server_data);
            Crashlytics.logException(new Exception("unable to start jumio web fallback flow because after id verification finish url is null or empty"));
        }
    }

    private void startNativeJumioFlow(@NonNull SubflowData subflowData) {
        try {
            mAfterIdVerificationFinishUrl = subflowData.getAfterIdVerificationFinishUrl();

            mNetverifySDK = NetverifySDK.create(getActivity(), subflowData.getJumioToken(),
                    subflowData.getJumioSecret(), JumioDataCenter.US);
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
        catch (PlatformNotSupportedException e) {
            //this never happens but AS complains if we don't catch it
            Crashlytics.logException(e);
            startJumioWebFallbackFlow(subflowData);
        }
        catch (MissingPermissionException e) {
            initJumioPermissionsBlocker();
        }
    }

    /**
     * @param subflowData
     * @return true if jumio native flow is determined as supported given non-null SubflowData
     */
    private boolean isNativeJumioFlowSupported(@NonNull SubflowData subflowData) {
        boolean isSupportedPlatform = NetverifySDK.isSupportedPlatform();

        bus.post(new LogEvent.AddLogEvent(
                new NativeOnboardingLog.NativeIDVerificationPlatformSupportStatusLog(isSupportedPlatform)));
        return isSupportedPlatform &&
                !Strings.isNullOrEmpty(subflowData.getJumioSecret())
                && !Strings.isNullOrEmpty(subflowData.getJumioToken()) &&
                !Strings.isNullOrEmpty(subflowData.getFullName()) &&
                !Strings.isNullOrEmpty(subflowData.getCandidateId())
                && !Strings.isNullOrEmpty(subflowData.getAfterIdVerificationFinishUrl());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showCameraBlocker && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            showCameraBlocker = false;
            Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
            if (fragmentByTag != null && fragmentByTag instanceof CameraPermissionsBlockerDialogFragment) {
                ((CameraPermissionsBlockerDialogFragment) fragmentByTag).dismiss();
            }
            initializeJumioIDVerification();
        }
        if (showCameraBlocker) {
            initJumioPermissionsBlocker();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeJumioIDVerification();
            }
            else {
                // Camera permission is required else blocked
                showCameraBlocker = true;
            }
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NetverifySDK.REQUEST_CODE) {
            final String scanReference = (data == null) ? "" : data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE);

            if (Strings.isNullOrEmpty(scanReference)) {
                cancel(new Intent());
            }

            if (resultCode == Activity.RESULT_OK) {
                bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationCompletedLog()));
                jumioAfterFinishCallback(scanReference, IDVerificationUtils.ID_VERIFICATION_SUCCESS);
                terminate(new Intent());
            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    int errorCode =
                            data.getIntExtra(NetverifySDK.EXTRA_ERROR_CODE, 0);
                    String errorMessage =
                            data.getStringExtra(NetverifySDK.EXTRA_ERROR_MESSAGE);

                    if (errorCode != 0 && !Strings.isNullOrEmpty(errorMessage)) {
                        Crashlytics.log(errorMessage);
                    }

                    // Cancelled by user
                    if (errorCode == 250) {
                        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationCancelledLog()));
                    }
                    else {
                        bus.post(new LogEvent.AddLogEvent(new NativeOnboardingLog.NativeIDVerificationFailedLog()));
                    }
                }

                jumioAfterFinishCallback(scanReference, IDVerificationUtils.ID_VERIFICATION_CANCELLATION);
                cancel(new Intent());
            }

            if (mNetverifySDK != null) { mNetverifySDK.destroy(); }
        }
    }

    private void jumioAfterFinishCallback(final String scanReference,
                                          @IDVerificationUtils.IdVerificationStatus final String status) {
        // Send scan reference to the server after id verification completed
        if (!Strings.isNullOrEmpty(mAfterIdVerificationFinishUrl)) {
            bus.post(new ProviderSettingsEvent.RequestIdVerificationFinish(
                    mAfterIdVerificationFinishUrl, scanReference, status));
        }
    }

    private void initJumioPermissionsBlocker() {
        Crashlytics.log("init jumio permissions blocker");
        Fragment fragmentByTag = getChildFragmentManager().
                findFragmentByTag(CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
        if (fragmentByTag == null) {
            if (mOnboardingDetails != null) {
                CameraPermissionsBlockerDialogFragment fragment = new CameraPermissionsBlockerDialogFragment();
                SubflowData subflowData =
                        mOnboardingDetails.getSubflowDataByType(SubflowType.ID_VERIFICATION);
                if (subflowData != null && !Strings.isNullOrEmpty(subflowData.getJumioURL())) {
                    Bundle args = new Bundle();
                    args.putString(BundleKeys.JUMIO_URL, subflowData.getJumioURL());
                    fragment.setArguments(args);
                }

                FragmentUtils.safeLaunchDialogFragment(fragment, this,
                        CameraPermissionsBlockerDialogFragment.FRAGMENT_TAG);
            }
            else {
                showToast(R.string.error_missing_server_data);
                Crashlytics.logException(new Exception("unable to start jumio permissions blocker because onboarding details null"));
            }
        }
    }
}
