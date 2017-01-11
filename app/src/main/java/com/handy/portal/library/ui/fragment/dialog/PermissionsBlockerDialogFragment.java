package com.handy.portal.library.ui.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.view.DialogBlockerView;
import com.handy.portal.library.util.Utils;

/**
 * a non-closeable dialog fragment with an action button
 * that directs the user to the settings page where they can set app permissions
 *
 * title and message are variable
 */
public abstract class PermissionsBlockerDialogFragment extends InjectedDialogFragment
{
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
                .setTitle(getTitleResourceId())
                .setMessage(getMessageResourceId())
                .setActionButton(R.string.permissions_dialog_action_button, new View.OnClickListener()
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

    public abstract int getTitleResourceId();
    public abstract int getMessageResourceId();
}
