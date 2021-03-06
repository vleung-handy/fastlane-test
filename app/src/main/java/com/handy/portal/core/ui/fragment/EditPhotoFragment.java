package com.handy.portal.core.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.event.ProfileEvent;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.util.FileProviderUtils;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UriUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.ImageUploadLog;
import com.handy.portal.logger.handylogger.model.ProfilePhotoLog;
import com.handy.portal.logger.handylogger.model.ProfilePhotoUploadLog;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.mime.TypedFile;

public class EditPhotoFragment extends ActionBarFragment {
    @Inject
    PrefsManager mPrefsManager;


    public enum Source {
        ONBOARDING, PROFILE, APP
    }


    private static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    private static final String EXTRA_CAMERA_FACING = "android.intent.extras.CAMERA_FACING";
    private static final String IMAGE_DIRECTORY = "handy_images/";
    private static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final String IMAGE_FILE_NAME = "temp.jpg";
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 4001;
    private static final int MAX_IMAGE_SIZE_MB = 1024 * 1024 * 3; // 3 MB
    private boolean mShowLoadingOverlayOnResume;
    private Source mSource;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.PROFILE_PICTURE;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSource = (Source) getArguments().getSerializable(BundleKeys.NAVIGATION_SOURCE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.edit_photo, false);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        bus.post(new NavigationEvent.SetNavigationTabVisibility(false));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        if (mShowLoadingOverlayOnResume) {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
        else {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        }
    }

    @OnClick(R.id.take_photo_button)
    public void onTakePhotoButtonClicked() {
        if (!Utils.areAllPermissionsGranted(getActivity(), new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }
        bus.post(new ProfilePhotoLog.CameraTapped());

        final Intent cameraImageIntent = new Intent(ACTION_IMAGE_CAPTURE);
        /*
        seems like API 24+ devices don't need the below flag but <API 24 devices do
         */
        cameraImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Uri imageFileUri = UriUtils.getUriFromFile(getContext(), getImageFile(),
                FileProviderUtils.getApplicationFileProviderAuthority(getContext()));
        cameraImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        final int frontFacingCameraId = getFrontFacingCameraId();
        if (frontFacingCameraId != -1) {
            cameraImageIntent.putExtra(EXTRA_CAMERA_FACING, frontFacingCameraId);
        }
        startActivityForResult(cameraImageIntent, RequestCode.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onTakePhotoButtonClicked();
        }
    }

    private int getFrontFacingCameraId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final CameraManager cameraManager = (CameraManager) getActivity()
                    .getSystemService(Context.CAMERA_SERVICE);
            try {
                for (final String cameraId : cameraManager.getCameraIdList()) {
                    final CameraCharacteristics cameraCharacteristics =
                            cameraManager.getCameraCharacteristics(cameraId);
                    final Integer lensFacing =
                            cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                    if (lensFacing != null && lensFacing == CameraMetadata.LENS_FACING_FRONT) {
                        return Integer.parseInt(cameraId);
                    }
                }
            }
            catch (Exception e) {
                Crashlytics.logException(e);
            }
        }
        else {
            for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    return cameraId;
                }
            }
        }
        return -1;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                bus.post(new ProfileEvent.RequestPhotoUploadUrl(IMAGE_MIME_TYPE));
                bus.post(new ImageUploadLog.MetadataRequestSubmitted());
                bus.post(
                        new ProfilePhotoUploadLog.ProfilePhotoUploadSubmitted(mSource));
            }
            else {
                bus.post(new ProfilePhotoLog.ImageChosen());
                mShowLoadingOverlayOnResume = true;
                cropImage();
            }
        }
        else {
            bus.post(new ProfilePhotoLog.ImagePickerDismissed());
            mShowLoadingOverlayOnResume = false;
        }
    }

    private void cropImage() {
        final Uri imageUri = Uri.fromFile(getImageFile());
        final UCrop.Options options = new UCrop.Options();
        final int blue = ContextCompat.getColor(getActivity(), R.color.handy_blue);
        final int darkBlue = ContextCompat.getColor(getActivity(), R.color.handy_blue_pressed);
        options.setActiveWidgetColor(blue);
        options.setToolbarColor(blue);
        options.setStatusBarColor(darkBlue);
        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        UCrop.of(imageUri, imageUri)
                .withOptions(options)
                .withAspectRatio(1, 1)
                .start(getActivity(), this);
    }

    private File getImageFile() {
        final File directory = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return new File(Environment.getExternalStorageDirectory(),
                IMAGE_DIRECTORY + IMAGE_FILE_NAME);
    }

    @Subscribe
    public void onReceivePhotoUploadUrlError(
            final ProfileEvent.ReceivePhotoUploadUrlError event) {
        bus.post(new ImageUploadLog.MetadataRequestError());
        bus.post(
                new ProfilePhotoUploadLog.ProfilePhotoUploadError(mSource));
        showError(event.error);
    }

    @Subscribe
    public void onReceivePhotoUploadUrlSuccess(
            final ProfileEvent.ReceivePhotoUploadUrlSuccess event) {
        bus.post(new ImageUploadLog.MetadataRequestSuccess());
        final File imageFile = getImageFile();
        if (!imageFile.exists()) {
            showToast(R.string.an_error_has_occurred);
            return;
        }
        fixImageRotation(imageFile);
        compressImage(imageFile);
        uploadImage(event.getUploadUrl(), imageFile);
    }

    private void compressImage(final File imageFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            while (imageFile.length() > MAX_IMAGE_SIZE_MB) {
                final FileOutputStream outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();
                bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            }
        }
        catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    private void fixImageRotation(final File imageFile) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
            final ExifInterface ei = new ExifInterface(imageFile.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
            final FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
        }
        catch (IOException e) {
            Crashlytics.logException(e);
        }
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    private void uploadImage(final String uploadUrl, final File imageFile) {
        final TypedFile file = new TypedFile(IMAGE_MIME_TYPE, imageFile);
        dataManager.uploadPhoto(uploadUrl, file, new FragmentSafeCallback<Void>(this) {
            @Override
            public void onCallbackSuccess(final Void response) {
                bus.post(new ImageUploadLog.ImageRequestSuccess());
                bus.post(
                        new ProfilePhotoUploadLog.ProfilePhotoUploadSuccess(mSource));
                bus.post(new ProfileEvent.RequestProviderProfile(false));
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                bus.post(new ImageUploadLog.ImageRequestError());
                bus.post(
                        new ProfilePhotoUploadLog.ProfilePhotoUploadError(mSource));
                showError(error);
            }
        });
        bus.post(new ImageUploadLog.ImageRequestSubmitted());
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(
            final ProfileEvent.ReceiveProviderProfileSuccess event) {
        bus.post(new ProfileEvent.ProfilePhotoUpdated());
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.upload_photo_success);
        getActivity().onBackPressed();
    }

    @Subscribe
    public void onReceiveProviderProfileError(final ProfileEvent.ReceiveProviderProfileError event) {
        showError(event.error);
        getActivity().onBackPressed();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        bus.post(new NavigationEvent.SetNavigationTabVisibility(true));
        super.onDestroyView();
    }

    private void showError(final DataManager.DataManagerError error) {
        String message = error.getMessage();
        if (TextUtils.isNullOrEmpty(message)) {
            message = getString(R.string.an_error_has_occurred);
        }
        showToast(message);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mShowLoadingOverlayOnResume = false;
    }
}
