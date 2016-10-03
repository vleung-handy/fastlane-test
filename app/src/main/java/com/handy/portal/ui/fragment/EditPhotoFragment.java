package com.handy.portal.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.library.util.TextUtils;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.mime.TypedFile;

public class EditPhotoFragment extends ActionBarFragment
{
    private static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    private static final String IMAGE_DIRECTORY = "handy_images/";
    private static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final String IMAGE_SUFFIX = ".jpg";
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 4001;
    private static final int REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE = 4002;
    private Uri mImageUri;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.PROFILE_PICTURE;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.edit_photo, false);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        setBackButtonEnabled(true);
    }

    @OnClick(R.id.choose_photo_camera)
    public void onChooseCameraClicked()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_PERMISSION_CAMERA);
            return;
        }

        final Intent cameraImageIntent = new Intent(ACTION_IMAGE_CAPTURE);
        final File cameraFolder;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            cameraFolder = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY);
        }
        else
        {
            cameraFolder = getActivity().getCacheDir(); // FIXME: Test this scenario
        }
        if (!cameraFolder.exists())
        {
            cameraFolder.mkdirs();
        }
        final String imageFileName = System.currentTimeMillis() + IMAGE_SUFFIX;
        final File imageFile = new File(Environment.getExternalStorageDirectory(),
                IMAGE_DIRECTORY + imageFileName);
        mImageUri = Uri.fromFile(imageFile);
        cameraImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(cameraImageIntent, RequestCode.CAMERA);
    }

    @OnClick(R.id.choose_photo_gallery)
    public void onChooseGalleryClicked()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE);
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_MIME_TYPE);
        final Intent chooser = Intent.createChooser(intent,
                getString(R.string.photo_chooser_title));
        startActivityForResult(chooser, RequestCode.GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    onChooseCameraClicked();
                }
                break;
            case REQUEST_CODE_PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    onChooseGalleryClicked();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == RequestCode.GALLERY)
            {
                mImageUri = data.getData();
            }
            bus.post(new ProfileEvent.RequestPhotoUploadUrl(IMAGE_MIME_TYPE));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        }
    }

    @Subscribe
    public void onReceivePhotoUploadUrlError(
            final ProfileEvent.ReceivePhotoUploadUrlError event)
    {
        showError(event.error);
    }

    @Subscribe
    public void onReceivePhotoUploadUrlSuccess(
            final ProfileEvent.ReceivePhotoUploadUrlSuccess event)
    {
        final TypedFile file = new TypedFile(IMAGE_MIME_TYPE, new File(mImageUri.getPath()));
        dataManager.uploadPhoto(event.getUploadUrl(), file,
                new DataManager.Callback<HashMap<String, String>>()
                {
                    @Override
                    public void onSuccess(final HashMap<String, String> response)
                    {
                        bus.post(new ProfileEvent.RequestProviderProfile(false));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        showError(error);
                    }
                });

    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(
            final ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @Subscribe
    public void onReceiveProviderProfileError(
            final ProfileEvent.ReceiveProviderProfileError event)
    {
        showError(event.error);
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    private void showError(final DataManager.DataManagerError error)
    {
        String message = error.getMessage();
        if (TextUtils.isNullOrEmpty(message))
        {
            message = getString(R.string.an_error_has_occurred);
        }
        showToast(message);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }
}
