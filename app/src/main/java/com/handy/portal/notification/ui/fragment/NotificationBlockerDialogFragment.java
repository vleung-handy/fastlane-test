package com.handy.portal.notification.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.ui.view.DialogBlockerView;

import butterknife.ButterKnife;

public class NotificationBlockerDialogFragment extends InjectedDialogFragment
{
    private static final String PACKAGE_PREFIX = "package:";

    public static final String FRAGMENT_TAG = "fragment_dialog_notification_blocker";

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
                .setTitle(R.string.notification_blocker_title)
                .setMessage(R.string.notification_blocker_body)
                .setActionButton(R.string.enable_now, new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse(PACKAGE_PREFIX + getContext().getPackageName()));
                        startActivity(intent);
                        dismiss();
                    }
                });
        ButterKnife.bind(this, view);
        return view;
    }
}
