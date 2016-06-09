package com.handy.portal.location.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.ui.view.DialogBlockerView;
import com.handy.portal.library.util.Utils;

public class LocationSettingsBlockerDialogFragment extends InjectedDialogFragment
{
    public static final String FRAGMENT_TAG = LocationSettingsBlockerDialogFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return new DialogBlockerView(getContext())
                .setTitle(R.string.change_location_settings_dialog_title)
                .setMessage(R.string.change_location_settings_dialog_message)
                .setActionButton(R.string.change_location_settings_dialog_action_button, new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        Utils.safeLaunchIntent(intent, getContext());
                        dismiss();
                    }
                });
    }
}
