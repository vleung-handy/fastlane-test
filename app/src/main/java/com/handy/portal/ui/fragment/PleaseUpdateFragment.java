package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.core.VersionManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PleaseUpdateFragment extends InjectedFragment
{
    @Inject
    VersionManager versionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_please_update, container);
        ButterKnife.inject(this, view);

        return view;
    }

    @OnClick(R.id.update_button)
    protected void installApk()
    {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(versionManager.getNewApkUri(), VersionManager.APK_MIME_TYPE);
        startActivity(installIntent);
    }
}
