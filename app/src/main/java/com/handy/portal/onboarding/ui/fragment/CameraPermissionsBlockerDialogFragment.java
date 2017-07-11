package com.handy.portal.onboarding.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.ui.view.JumioCameraDialogBlockerView;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class CameraPermissionsBlockerDialogFragment extends InjectedDialogFragment {
    @Inject
    EventBus mBus;

    public static final String FRAGMENT_TAG = CameraPermissionsBlockerDialogFragment.class.getName();

    private String mUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);

        Bundle args = getArguments();
        if (args != null && !args.isEmpty()) {
            mUrl = args.getString(BundleKeys.JUMIO_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return new JumioCameraDialogBlockerView(getContext(), mBus)
                .setUrl(mUrl)
                .setActionButton(R.string.open_settings_now, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (mBus != null) {
                            mBus.post(new NativeOnboardingLog.CameraSettingsOpenedLog());
                        }

                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        Utils.safeLaunchIntent(intent, getContext());
                        dismiss();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}
