package com.handy.portal.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.constant.RequestCode;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditPhotoFragment extends ActionBarFragment
{
    private static final String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE";
    private static final String IMAGE_DIRECTORY = "handy_images/";
    private static final String IMAGE_MIME_TYPE = "image/jpeg";
    private static final String IMAGE_SUFFIX = ".jpg";
    private Uri mImageUri;

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
//        bus.register(this);
        setBackButtonEnabled(true);
    }

    @OnClick(R.id.choose_photo_camera)
    public void onChooseCameraClicked()
    {
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
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(IMAGE_MIME_TYPE);
        final Intent chooser = Intent.createChooser(intent,
                getString(R.string.photo_chooser_title));
        startActivityForResult(chooser, RequestCode.GALLERY);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == RequestCode.CAMERA)
            {
                showToast("Camera success!");
            }
            if (requestCode == RequestCode.GALLERY)
            {
                showToast("Gallery success!");
            }
        }
    }

    @Override
    public void onPause()
    {
//        bus.unregister(this);
        super.onPause();
    }

}
