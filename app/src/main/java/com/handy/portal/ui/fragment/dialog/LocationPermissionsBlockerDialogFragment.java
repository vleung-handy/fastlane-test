package com.handy.portal.ui.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.ui.view.DialogBlockerView;
import com.handy.portal.util.Utils;

public class LocationPermissionsBlockerDialogFragment extends InjectedDialogFragment
{
    public static final String FRAGMENT_TAG = LocationPermissionsBlockerDialogFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = new DialogBlockerView(getContext())
                .setTitle(R.string.change_location_permissions_dialog_title)
                .setMessage(R.string.change_location_permissions_dialog_message)
                .setActionButton(R.string.change_location_permissions_dialog_action_button, new View.OnClickListener()
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
        return view;
    }
}
