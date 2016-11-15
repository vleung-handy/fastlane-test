package com.handy.portal.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.data.DataManager;
import com.handy.portal.helpcenter.constants.HelpCenterUrl;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.widget.PhoneInputTextView;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.LoginLog;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.model.SuccessWrapper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginSltFragment extends InjectedFragment
{
    @Inject
    LoginManager mLoginManager;

    @BindView(R.id.phone_input_layout)
    RelativeLayout mPhoneInputLayout;
    @BindView(R.id.phone_number_edit_text)
    PhoneInputTextView mPhoneNumberEditText;
    @BindView(R.id.login_instructions_text)
    TextView mInstructionsText;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login_slt, container);

        ButterKnife.bind(this, view);

        bus.post(new LogEvent.AddLogEvent(new LoginLog.Shown(LoginLog.TYPE_PHONE_TOKEN)));

        return view;
    }

    @OnClick(R.id.login_button)
    public void login()
    {
        if (!mPhoneNumberEditText.validate()) { return; }

        bus.post(new LogEvent.AddLogEvent(new LoginLog.login_submitted(LoginLog.TYPE_PHONE_TOKEN)));
        mLoginManager.requestSlt(mPhoneNumberEditText.getPhoneNumber(), new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                onRequestSltSuccess(response);
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                onRequestSltError(error);
            }
        });
    }

    private void onRequestSltSuccess(SuccessWrapper response)
    {
        if (response.getSuccess())
        {
            bus.post(new LogEvent.AddLogEvent(new LoginLog.Success(LoginLog.TYPE_PHONE_TOKEN)));

            mInstructionsText.setText(getString(R.string.login_instructions_slt2, mPhoneNumberEditText.getPhoneNumber()));
            mLoginButton.setText(R.string.request_slt_again);
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new LoginLog.Error(LoginLog.TYPE_PHONE_TOKEN)));
            showToast(R.string.login_error_bad_phone);
            mPhoneNumberEditText.highlight();
        }
    }

    private void onRequestSltError(DataManager.DataManagerError error)
    {
        bus.post(new LogEvent.AddLogEvent(new LoginLog.Error(LoginLog.TYPE_PHONE_TOKEN)));

        if (error != null && !TextUtils.isNullOrEmpty(error.getMessage()))
        {
            new AlertDialog.Builder(getActivity())
                    .setMessage(error.getMessage())
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
        else
        {
            showToast(R.string.login_error_connectivity);
        }
        mPhoneNumberEditText.highlight();
    }

    @OnClick(R.id.login_help_button)
    public void showLoginInstructions()
    {
        if (getView() != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        View instructionView =
                LayoutInflater.from(getActivity()).inflate(R.layout.element_login_instructions, null);
        instructionView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {

                goToUrl(HelpCenterUrl.LOGIN_HELP_ABSOLUTE_URL);
            }
        });
        mSlideUpPanelLayout.showPanel(R.string.instructions, instructionView);
    }

    private void goToUrl(String url)
    {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        Utils.safeLaunchIntent(launchBrowser, this.getActivity());
    }
}
