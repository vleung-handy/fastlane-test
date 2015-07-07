package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PleaseUpdateFragment extends InjectedFragment
{
    @Inject
    VersionManager versionManager;

    @InjectView(R.id.update_image)
    ImageView updateImage;
    @InjectView(R.id.update_button)
    View updateButton;
    @InjectView(R.id.update_text)
    TextView updateText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_please_update, container);
        ButterKnife.inject(this, view);

        ((AnimationDrawable) updateImage.getBackground()).start();

        return view;
    }

    @Subscribe
    public void onDownloadUpdateSuccessful(HandyEvent.DownloadUpdateSuccessful event)
    {
        updateImage.setBackgroundResource(R.drawable.img_update_success);
        updateButton.setEnabled(true);
        updateText.setText(R.string.update_copy);
    }

    @Subscribe
    public void onDownloadUpdateFailed(HandyEvent.DownloadUpdateFailed event)
    {
        showToast(R.string.update_failed);
        getActivity().finish();
    }

    @OnClick(R.id.update_button)
    protected void installApk()
    {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(versionManager.getNewApkUri(), VersionManager.APK_MIME_TYPE);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(installIntent);
    }
}
