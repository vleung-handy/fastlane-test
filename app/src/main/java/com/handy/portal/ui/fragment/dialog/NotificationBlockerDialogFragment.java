package com.handy.portal.ui.fragment.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationBlockerDialogFragment extends InjectedDialogFragment
{
    @InjectView(R.id.notification_blocker_enable_button)
    protected Button updateNowButton;

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
        View view = inflater.inflate(R.layout.fragment_dialog_notification_blocker, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        updateNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse(PACKAGE_PREFIX + getContext().getPackageName()));
                startActivity(intent);
                dismiss();
            }
        });
    }
}
