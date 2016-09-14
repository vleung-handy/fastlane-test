package com.handy.portal.onboarding.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.ui.view.PermissionsBlockerDialogView;
import com.handy.portal.library.util.Utils;

public class PermissionsBlockerDialogFragment extends InjectedDialogFragment
{
    public static final String FRAGMENT_TAG =
            PermissionsBlockerDialogFragment.class.getName();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new PermissionsBlockerDialogView(getContext())
                .setText(R.string.permissions_needed,
                        R.string.permissions_needed_description)
                .setActionButton(R.string.open_settings_now, new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        Utils.safeLaunchIntent(intent, getContext());
                        dismiss();
                    }
                });
    }
}
