package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.core.UpdateManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PleaseUpdateFragment extends InjectedFragment
{

    @InjectView(R.id.download_button)
    Button downloadButton;
    @Inject
    UpdateManager updateManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_please_update, container);
        ButterKnife.inject(this, view);

        registerControlListeners();

        return view;
    }

    private void registerControlListeners()
    {

        downloadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doDownloadApk(updateManager.getDownloadURL());
            }
        });
    }

    public void doDownloadApk(String apkurl){
        mixpanel.track("portal app update blocking screen clicked");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(apkurl));
        startActivity(i);
    }


}
