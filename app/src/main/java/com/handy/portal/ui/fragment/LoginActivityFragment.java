package com.handy.portal.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.annotations.VisibleForTesting;
import com.handy.portal.R;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.ui.activity.SplashActivity;
import com.handy.portal.ui.layout.SlideUpPanelContainer;
import com.handy.portal.ui.widget.PhoneInputTextView;
import com.handy.portal.ui.widget.PinCodeInputTextView;
import com.handy.portal.util.UIUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivityFragment extends InjectedFragment
{
    @VisibleForTesting
    protected static final String HELP_CENTER_URL = "https://www.handy.com/help#/6311ae/e15ed1/76a73e";

    @InjectView(R.id.phone_input_layout)
    RelativeLayout phoneInputLayout;
    @InjectView(R.id.pin_code_input_layout)
    RelativeLayout pinCodeInputLayout;
    @InjectView(R.id.phone_number_edit_text)
    PhoneInputTextView phoneNumberEditText;
    @InjectView(R.id.pin_code_edit_text)
    PinCodeInputTextView pinCodeEditText;
    @InjectView(R.id.login_instructions_text)
    TextView instructionsText;
    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.back_button)
    ImageButton backButton;

    @Inject
    EnvironmentModifier environmentModifier;
    @Inject
    BuildConfigWrapper buildConfigWrapper;
    @Inject
    Mixpanel mixpanel;
    @Inject
    PrefsManager prefsManager;
    @InjectView(R.id.slide_up_panel_container)
    protected SlideUpPanelContainer slideUpPanelContainer;

    private enum LoginState
    {
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
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container);

        ButterKnife.inject(this, view);

        changeState(LoginState.INIT);

        registerControlListeners();

        return view;
    }

    private void registerControlListeners()
    {
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (currentLoginState)
                {
                    case INPUTTING_PHONE_NUMBER:
                    {
                        sendPhoneNumber(phoneNumberEditText.getPhoneNumber());
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(phoneNumberEditText.getWindowToken(), 0);
                    }
                    break;
                    case INPUTTING_PIN:
                    {
                        if (pinCodeEditText.validate())
                        {
                            sendLoginRequest(storedPhoneNumber, pinCodeEditText.getString());
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(pinCodeEditText.getWindowToken(), 0);
                        }
                    }
                    break;
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (currentLoginState)
                {
                    case INPUTTING_PIN:
                    {
                        changeState(LoginState.INPUTTING_PHONE_NUMBER);
                    }
                    break;
                }
            }
        });
    }

    @OnClick(R.id.logo)
    protected void selectEnvironment()
    {
        if (!buildConfigWrapper.isDebug()) return;

        UIUtils.createEnvironmentModifierDialog(environmentModifier, getActivity(), null).show();
    }

    @OnClick(R.id.login_help_button)
    protected void showLoginInstructions()
    {
        slideUpPanelContainer.showPanel(R.string.instructions, new SlideUpPanelContainer.ContentInitializer()
        {
            @Override
            public void initialize(ViewGroup panel)
            {
                LayoutInflater.from(getActivity()).inflate(R.layout.element_login_instructions, panel);

                panel.findViewById(R.id.login_help).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        goToUrl(HELP_CENTER_URL);
                    }
                });
            }
        });
    }

    private void goToUrl(String url)
    {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        Utils.safeLaunchIntent(launchBrowser, this.getActivity());
    }


    //Event Sending

    //Send a request for a pin code based on a phone number
    private void sendPhoneNumber(String phoneNumber)
    {
        storedPhoneNumber = phoneNumber; //remember so they don't have to reinput once they receive their pin
        changeState(LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE);

        if (buildConfigWrapper.isDebug() && !environmentModifier.pinRequestEnabled())
        {
            // if pin request is disabled, jump to pin input state; this is used for test automation
            // purposes where the seeded value of the pin associated with the provider will be
            // preserved on the server side and used on the client side
            changeState(LoginState.INPUTTING_PIN);
        } else
        {
            bus.post(new HandyEvent.RequestPinCode(phoneNumber));
        }
    }

    //send a login request to the server with our phoneNumber and pin
    private void sendLoginRequest(String phoneNumber, String pinCode)
    {
        changeState(LoginState.WAITING_FOR_LOGIN_RESPONSE);
        bus.post(new HandyEvent.RequestLogin(phoneNumber, pinCode));
    }

    //Event Listening

    @Subscribe
    public void onPinCodeRequestReceived(HandyEvent.ReceivePinCodeSuccess event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE)
        {
            if (event.pinRequestDetails.getSuccess())
            {
                changeState(LoginState.INPUTTING_PIN);
            } else
            {
                postLoginErrorEvent("phone number");
                showToast(R.string.login_error_bad_phone);
                changeState(LoginState.INPUTTING_PHONE_NUMBER);
                phoneNumberEditText.highlight();
            }
        }
    }

    @Subscribe
    public void onPinCodeRequestError(HandyEvent.ReceivePinCodeError event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE)
        {
            postLoginErrorEvent("server");
            showToast(R.string.login_error_connectivity);
            changeState(LoginState.INPUTTING_PHONE_NUMBER);
            phoneNumberEditText.highlight();
        }
    }

    private void postLoginErrorEvent(String source)
    {
        bus.post(new HandyEvent.LoginError(source));
    }

    @Subscribe
    public void onLoginRequestSuccess(HandyEvent.ReceiveLoginSuccess event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_LOGIN_RESPONSE)
        {
            if (event.loginDetails.getSuccess())
            {
                beginLogin(event.loginDetails);
            } else
            {
                postLoginErrorEvent("pin code");
                showToast(R.string.login_error_bad_login);
                changeState(LoginState.INPUTTING_PIN);
                pinCodeEditText.highlight();
            }
        }
    }

    @Subscribe
    public void onLoginRequestError(HandyEvent.ReceiveLoginError event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_LOGIN_RESPONSE)
        {
            postLoginErrorEvent("server");
            showToast(R.string.login_error_connectivity);
            changeState(LoginState.INPUTTING_PIN);
        }
    }

    //Controller

    private void changeState(LoginState phase)
    {
        if (currentLoginState == phase)
        {
            return;
        }

        currentLoginState = phase;

        updateDisplay(currentLoginState);

        //TODO: Do we need to test connectivity or anything else before proceeding?
        if (phase == LoginState.INIT)
        {
            changeState(LoginState.INPUTTING_PHONE_NUMBER);
        }
    }

    private void beginLogin(LoginDetails loginDetails)
    {
        changeState(LoginState.COMPLETE);

        String providerId = loginDetails.getProviderId();
        prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, providerId);//TODO: we need to move away from using PrefsKey.LAST_PROVIDER_ID

        prefsManager.setBoolean(PrefsKey.ONBOARDING_NEEDED, true);
        startActivity(new Intent(this.getActivity(), SplashActivity.class));
    }

    //TODO: View work, to separate into a view class along with the view injections
    private void updateDisplay(LoginState phase)
    {
        switch (phase)
        {
            case INIT:
            {
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PHONE_NUMBER:
            {
                mixpanel.track("portal login shown - phone");
                instructionsText.setText(R.string.login_instructions_1_a);
                phoneInputLayout.setVisibility(View.VISIBLE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText(R.string.request_pin);
                backButton.setVisibility(View.GONE);
            }
            break;
            case WAITING_FOR_PHONE_NUMBER_RESPONSE:
            {
                instructionsText.setText(R.string.sending_pin);
                phoneInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PIN:
            {
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
            case WAITING_FOR_LOGIN_RESPONSE:
            {
                instructionsText.setText(R.string.logging_in);
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case COMPLETE:
            {
                instructionsText.setText(R.string.logging_in);
            }
            break;
        }
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }

}
