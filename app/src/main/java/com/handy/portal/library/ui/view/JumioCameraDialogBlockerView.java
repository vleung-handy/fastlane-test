package com.handy.portal.library.ui.view;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.IDVerificationUtils;
import com.handy.portal.logger.handylogger.model.NativeOnboardingLog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JumioCameraDialogBlockerView extends RelativeLayout {
    @BindView(R.id.dialog_blocker_title)
    TextView mTitle;
    @BindView(R.id.dialog_blocker_message)
    TextView mMessage;
    @BindView(R.id.dialog_blocker_action_button)
    Button mActionButton;
    @BindView(R.id.camera_broken_text)
    TextView mCameraBrokenText;

    private EventBus mBus;
    private String mUrl;

    public JumioCameraDialogBlockerView(final Context context, final EventBus bus) {
        super(context);
        initView(context, bus);
    }

    @SuppressWarnings("deprecation")
    private void initView(final Context context, final EventBus bus) {
        LayoutInflater.from(context).inflate(R.layout.fragment_jumio_camera_dialog_blocker, this);
        ButterKnife.bind(this);

        mBus = bus;
        mTitle.setText(R.string.allow_camera_title);
        mMessage.setText(R.string.camera_required_id_verification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCameraBrokenText.setText(
                    Html.fromHtml(getResources().getString(R.string.camera_broken_html),
                            Html.FROM_HTML_MODE_LEGACY));
        }
        else {
            mCameraBrokenText.setText(
                    Html.fromHtml(getResources().getString(R.string.camera_broken_html)));
        }
    }

    public JumioCameraDialogBlockerView setUrl(String url) {
        mUrl = url;
        return this;
    }

    public JumioCameraDialogBlockerView setActionButton(int buttonTextResourceId, OnClickListener onClickListener) {
        mActionButton.setText(buttonTextResourceId);
        mActionButton.setOnClickListener(onClickListener);
        return this;
    }

    @OnClick(R.id.camera_broken_text)
    public void clickedCameraBroken() {
        if (mBus != null) {
            mBus.post(new NativeOnboardingLog.WebIDVerificationFlowStarted());
        }
        IDVerificationUtils.initJumioWebFlow(getContext(), mUrl);
    }
}
