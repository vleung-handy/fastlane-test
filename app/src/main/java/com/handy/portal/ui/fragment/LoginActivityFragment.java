package com.handy.portal.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.common.annotations.VisibleForTesting;
import com.handy.portal.R;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.EnvironmentManager;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.PhoneInputTextView;
import com.handy.portal.ui.widget.PinCodeInputTextView;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.handy.portal.data.EnvironmentManager.Environment;

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
    @InjectView(R.id.login_help)
    TextView loginHelpText;

    @Inject
    EnvironmentManager environmentManager;
    @Inject
    BuildConfigWrapper buildConfigWrapper;

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

        //TODO: Prepopulate phone number with device's number? User could still edit if it fails

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

        loginHelpText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToUrl(HELP_CENTER_URL);
            }
        });
    }


    @OnClick(R.id.logo)
    protected void onSelectEnvironment()
    {
        if (!buildConfigWrapper.isDebug()) return;

        final Environment[] environments = Environment.values();
        String[] environmentNames = new String[environments.length];
        Environment currentEnvironment = environmentManager.getEnvironment();
        for (int i = 0; i < environments.length; i++)
        {
            Environment environment = environments[i];
            environmentNames[i] = environment.getName();
            if (currentEnvironment == environment)
            {
                environmentNames[i] += " (selected)";
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick an environment")
                .setItems(environmentNames, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        environmentManager.setEnvironment(environments[which]);
                    }
                });
        builder.create().show();
    }

    private void goToUrl(String url)
    {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


    //Event Sending

    //Send a request for a pin code based on a phone number
    private void sendPhoneNumber(String phoneNumber)
    {
        storedPhoneNumber = phoneNumber; //remember so they don't have to reinput once they receive their pin
        changeState(LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE);
        bus.post(new Event.RequestPinCodeEvent(phoneNumber));
    }

    //send a login request to the server with our phoneNumber and pin
    private void sendLoginRequest(String phoneNumber, String pinCode)
    {
        changeState(LoginState.WAITING_FOR_LOGIN_RESPONSE);
        bus.post(new Event.RequestLoginEvent(phoneNumber, pinCode));
    }

    //Event Listening

    @Subscribe
    public void onPinCodeRequestReceived(Event.PinCodeRequestReceivedEvent event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_PHONE_NUMBER_RESPONSE)
        {
            if (event.success)
            {
                if (event.pinRequestDetails.getSuccess())
                {
                    changeState(LoginState.INPUTTING_PIN);
                }
                else
                {
                    postLoginErrorEvent("phone number");
                    showErrorToast(R.string.login_error_bad_phone);
                    changeState(LoginState.INPUTTING_PHONE_NUMBER);
                    phoneNumberEditText.highlight();
                }
            }
            else
            {
                postLoginErrorEvent("server");
                showErrorToast(R.string.login_error_connectivity);
                changeState(LoginState.INPUTTING_PHONE_NUMBER);
                phoneNumberEditText.highlight();
            }
        }
    }

    private void postLoginErrorEvent(String source)
    {
        bus.post(new Event.LoginError(source));
    }

    @Subscribe
    public void onLoginRequestReceived(Event.LoginRequestReceivedEvent event)
    {
        if (currentLoginState == LoginState.WAITING_FOR_LOGIN_RESPONSE)
        {
            if (event.success)
            {
                if (event.loginDetails.getSuccess())
                {
                    beginLogin(event.loginDetails);
                }
                else
                {
                    postLoginErrorEvent("pin code");
                    showErrorToast(R.string.login_error_bad_login);
                    changeState(LoginState.INPUTTING_PIN);
                    pinCodeEditText.highlight();
                }
            }
            else
            {
                postLoginErrorEvent("server");
                showErrorToast(R.string.login_error_connectivity);
                changeState(LoginState.INPUTTING_PIN);
            }
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

        //Set cookies to enable seamless access in our webview
        if (loginDetails.getUserCredentials() != null)
        {
            CookieSyncManager.createInstance(getActivity());
            CookieManager.getInstance().setCookie(dataManager.getBaseUrl(), loginDetails.getUserCredentialsCookie());
            CookieSyncManager.getInstance().sync();
        }

        //transition to main activity
        startActivity(new Intent(this.getActivity(), MainActivity.class));
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
