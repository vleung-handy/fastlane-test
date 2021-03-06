package com.handy.portal.core.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.LoginDetails;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.data.DataManager;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;
import com.handy.portal.library.ui.fragment.InjectedFragment;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.widget.PhoneInputTextView;
import com.handy.portal.library.ui.widget.PinCodeInputTextView;
import com.handy.portal.library.util.EnvironmentUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.LoginLog;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.handy.portal.logger.handylogger.model.LoginLog.TYPE_PIN;

public class LoginActivityFragment extends InjectedFragment {
    @BindView(R.id.phone_input_layout)
    RelativeLayout phoneInputLayout;
    @BindView(R.id.pin_code_input_layout)
    RelativeLayout pinCodeInputLayout;
    @BindView(R.id.phone_number_edit_text)
    PhoneInputTextView phoneNumberEditText;
    @BindView(R.id.pin_code_edit_text)
    PinCodeInputTextView pinCodeEditText;
    @BindView(R.id.login_instructions_text)
    TextView instructionsText;
    @BindView(R.id.login_button)
    Button loginButton;
    @BindView(R.id.back_button)
    ImageButton backButton;
    @BindView(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;


    @Inject
    EnvironmentModifier environmentModifier;
    @Inject
    BuildConfigWrapper buildConfigWrapper;
    @Inject
    ProviderManager mProviderManager;


    private enum LoginState {
        INIT,
        INPUTTING_PHONE_NUMBER,
        WAITING_FOR_PHONE_NUMBER_RESPONSE,
        INPUTTING_PIN,
        WAITING_FOR_LOGIN_RESPONSE,
        COMPLETE
    }


    private LoginState currentLoginState;
    private String storedPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container);

        ButterKnife.bind(this, view);

        changeState(LoginState.INIT);

        registerControlListeners();

        bus.post(new LoginLog.Shown(LoginLog.TYPE_PHONE));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    private void registerControlListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentLoginState) {
                    case INPUTTING_PHONE_NUMBER: {
                        bus.post(new LoginLog.LoginSubmitted(LoginLog.TYPE_PHONE));
                        if (phoneNumberEditText.validate()) {
                            sendPhoneNumber(phoneNumberEditText.getPhoneNumber());
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(phoneNumberEditText.getWindowToken(), 0);
                        }
                    }
                    break;
                    case INPUTTING_PIN: {
                        bus.post(new LoginLog.LoginSubmitted(TYPE_PIN));
                        if (pinCodeEditText.validate()) {
                            sendLoginRequest(storedPhoneNumber, pinCodeEditText.getString());
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(pinCodeEditText.getWindowToken(), 0);
                        }
                    }
                    break;
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentLoginState) {
                    case INPUTTING_PIN: {
                        changeState(LoginState.INPUTTING_PHONE_NUMBER);
                    }
                    break;
                }
            }
        });
    }

    @OnClick(R.id.logo)
    protected void selectEnvironment() {
        if (!buildConfigWrapper.isDebug()) { return; }

        EnvironmentUtils.showEnvironmentModifierDialog(environmentModifier, getActivity(), null);
    }

    @OnClick(R.id.login_help_button)
    protected void showLoginInstructions() {
        if (getView() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }

        View instructionView =
                LayoutInflater.from(getActivity()).inflate(R.layout.element_login_instructions, null);
        instructionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                goToUrl(HelpCenterConstants.LOGIN_INFO_URL);
            }
        });
        mSlideUpPanelLayout.showPanel(R.string.instructions, instructionView);
    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        Utils.safeLaunchIntent(launchBrowser, this.getActivity());
    }

    //Event Sending

    //Send a request for a pin code based on a phone number
    private void sendPhoneNumber(String phoneNumber) {
        storedPhoneNumber = phoneNumber; //remember so they don't have to reinput once they receive their pin
        changeState(LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE);

        if (buildConfigWrapper.isDebug() && !environmentModifier.isPinRequestEnabled()) {
            // if pin request is disabled, jump to pin input state; this is used for test automation
            // purposes where the seeded value of the pin associated with the provider will be
            // preserved on the server side and used on the client side
            changeState(LoginState.INPUTTING_PIN);
        }
        else {
            bus.post(new HandyEvent.RequestPinCode(phoneNumber));
        }
    }

    //send a login request to the server with our phoneNumber and pin
    private void sendLoginRequest(String phoneNumber, String pinCode) {
        changeState(LoginState.WAITING_FOR_LOGIN_RESPONSE);
        bus.post(new HandyEvent.RequestLogin(phoneNumber, pinCode));
    }

    //Event Listening

    @Subscribe
    public void onPinCodeRequestReceived(HandyEvent.ReceivePinCodeSuccess event) {
        if (currentLoginState == LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE) {
            if (event.pinRequestDetails.getSuccess()) {
                bus.post(new LoginLog.Success(LoginLog.TYPE_PHONE));
                changeState(LoginState.INPUTTING_PIN);
            }
            else {
                bus.post(new LoginLog.Error(LoginLog.TYPE_PHONE));
                postLoginErrorEvent("phone number");
                showToast(R.string.login_error_bad_phone);
                changeState(LoginState.INPUTTING_PHONE_NUMBER);
                phoneNumberEditText.highlight();
            }
        }
    }

    @Subscribe
    public void onPinCodeRequestError(HandyEvent.ReceivePinCodeError event) {
        if (currentLoginState == LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE) {
            bus.post(new LoginLog.Error(LoginLog.TYPE_PHONE));
            postLoginErrorEvent("server");
            if (event.error != null && !TextUtils.isNullOrEmpty(event.error.getMessage())) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(event.error.getMessage())
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok, null)
                        .create()
                        .show();
            }
            else {
                showToast(R.string.login_error_connectivity);
            }
            changeState(LoginState.INPUTTING_PHONE_NUMBER);
            phoneNumberEditText.highlight();
        }
    }

    private void postLoginErrorEvent(String source) {
        bus.post(new HandyEvent.LoginError(source));
    }

    @Subscribe
    public void onLoginRequestSuccess(HandyEvent.ReceiveLoginSuccess event) {
        if (currentLoginState == LoginState.WAITING_FOR_LOGIN_RESPONSE) {
            if (event.loginDetails.getSuccess()) {
                bus.post(new LoginLog.Success(LoginLog.TYPE_PIN));
                beginLogin(event.loginDetails);
            }
            else {
                //this should never happen anymore since we changed the HTTP response code for login failure. logging for now just in case
                Crashlytics.logException(new Exception("Login request success event fired but login details success parameter is false"));
                bus.post(new LoginLog.Error(LoginLog.TYPE_PIN));
                showToast(R.string.login_error_bad_login);
                changeState(LoginState.INPUTTING_PIN);
                pinCodeEditText.highlight();
            }
        }
    }

    @Subscribe
    public void onLoginRequestError(HandyEvent.ReceiveLoginError event) {
        if (currentLoginState == LoginState.WAITING_FOR_LOGIN_RESPONSE) {
            bus.post(new LoginLog.Error(LoginLog.TYPE_PIN));
            DataManager.DataManagerError.Type errorType = event.error == null ? null : event.error.getType();
            if (errorType != null) {
                if (errorType.equals(DataManager.DataManagerError.Type.NETWORK)) {
                    showToast(R.string.error_connectivity);
                }
                else if (errorType.equals(DataManager.DataManagerError.Type.CLIENT)) {
                    showToast(R.string.login_error_bad_login);
                }
                else //server error
                {
                    showToast(R.string.login_error_connectivity);
                }
            }
            else {
                //should never happen
                showToast(R.string.login_error_connectivity);
                Crashlytics.logException(new Exception("Login request error type is null"));
            }
            pinCodeEditText.highlight();
            changeState(LoginState.INPUTTING_PIN);
        }
    }

    //Controller

    private void changeState(LoginState phase) {
        if (currentLoginState == phase) {
            return;
        }

        currentLoginState = phase;

        updateDisplay(currentLoginState);

        //TODO: Do we need to test connectivity or anything else before proceeding?
        if (phase == LoginState.INIT) {
            changeState(LoginState.INPUTTING_PHONE_NUMBER);
        }
    }

    @SuppressWarnings("deprecation")
    private void beginLogin(LoginDetails loginDetails) {
        changeState(LoginState.COMPLETE);

        //Set cookies to enable seamless access in our webview
        if (loginDetails.getAuthToken() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setCookie(dataManager.getBaseUrl(),
                        loginDetails.getUserCredentialsCookie());
                CookieManager.getInstance().flush();
            }
            else {
                CookieSyncManager.createInstance(getActivity());
                CookieManager.getInstance().setCookie(dataManager.getBaseUrl(), loginDetails.getUserCredentialsCookie());
                CookieSyncManager.getInstance().sync();
            }
        }

        String providerId = loginDetails.getProviderId();
        mProviderManager.setProviderId(providerId);

        startActivity(new Intent(this.getActivity(), SplashActivity.class));
    }

    //TODO: View work, to separate into a view class along with the view injections
    private void updateDisplay(LoginState phase) {
        switch (phase) {
            case INIT: {
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PHONE_NUMBER: {
                instructionsText.setText(R.string.login_instructions_1);
                phoneInputLayout.setVisibility(View.VISIBLE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText(R.string.request_pin);
                backButton.setVisibility(View.GONE);
            }
            break;
            case WAITING_FOR_PHONE_NUMBER_RESPONSE: {
                instructionsText.setText(R.string.sending_pin);
                phoneInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PIN: {
                String instructionsFormat = getResources().getString(R.string.login_instructions_2);
                String instructions = String.format(instructionsFormat, storedPhoneNumber);
                instructionsText.setText(instructions);
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText(R.string.log_in);
                backButton.setVisibility(View.VISIBLE);
            }
            break;
            case WAITING_FOR_LOGIN_RESPONSE: {
                instructionsText.setText(R.string.logging_in);
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case COMPLETE: {
                instructionsText.setText(R.string.logging_in);
            }
            break;
        }
    }

    @Override
    public void startActivity(final Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }

}
