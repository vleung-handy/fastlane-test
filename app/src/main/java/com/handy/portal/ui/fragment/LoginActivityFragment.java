package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.PhoneInputTextView;
import com.handy.portal.ui.widget.PinCodeInputTextView;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends InjectedFragment
{
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
    @InjectView(R.id.login_instructions_text_b)
    TextView secondaryInstructionsText;
    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.back_button)
    ImageButton backButton;
    @InjectView(R.id.help_cta)
    TextView helpCta;

    private static final String APPLY_NOW_URL = "https://www.handy.com/apply";
    private static final String HELP_URL = "https://www.handy.com/help";

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
                        if (phoneNumberEditText.validate())
                        {
                            sendPhoneNumber(phoneNumberEditText.getPhoneNumber());
                        }
                    }
                    break;
                    case INPUTTING_PIN:
                    {
                        if (pinCodeEditText.validate())
                        {
                            sendLoginRequest(storedPhoneNumber, pinCodeEditText.getString());
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

        helpCta.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (currentLoginState)
                {
                    case INPUTTING_PHONE_NUMBER:
                    {
                        //not yet registered, apply now
                        goToUrl(APPLY_NOW_URL);
                    }
                    break;

                    case INPUTTING_PIN:
                    {
                        //did not get a pin code sent
                        goToUrl(HELP_URL);
                    }
                    break;
                }
            }
        });

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
                if(event.pinRequestDetails.getSuccess())
                {
                    changeState(LoginState.INPUTTING_PIN);
                }
                else
                {
                    showLoginError(R.string.login_error_bad_phone);
                    changeState(LoginState.INPUTTING_PHONE_NUMBER);
                    phoneNumberEditText.highlight();
                }
            }
            else
            {
                showLoginError(R.string.login_error_connectivity);
                changeState(LoginState.INPUTTING_PHONE_NUMBER);
                phoneNumberEditText.highlight();
            }
        }
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
                    showLoginError(R.string.login_error_bad_login);
                    changeState(LoginState.INPUTTING_PIN);
                    pinCodeEditText.highlight();
                }
            } else
            {
                showLoginError(R.string.login_error_connectivity);
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
        if (loginDetails.getHandybookSessionId() != null)
        {
            CookieManager.getInstance().setCookie(dataManager.getBaseUrl(), loginDetails.getHandybookSessionIdCookie());
        }

        if (loginDetails.getUserCredentials() != null)
        {
            CookieManager.getInstance().setCookie(dataManager.getBaseUrl(), loginDetails.getUserCredentialsCookie());
        }

        //TODO: If we have API version 21 we can use a valueCallback for setting cookies instead of hacking a sleep to sync
        //Cookie syncing is not guaranteed to be instant, this is a hacky workaround
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        //transition to main activity
        startActivity(new Intent(this.getActivity(), MainActivity.class));
    }

    //View work, to separate into a view class along with the view injections
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
                helpCta.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PHONE_NUMBER:
            {
                instructionsText.setText(R.string.login_instructions_1_a);
                secondaryInstructionsText.setText(R.string.login_instructions_1_b);
                phoneInputLayout.setVisibility(View.VISIBLE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText(R.string.request_pin);
                backButton.setVisibility(View.GONE);
                helpCta.setVisibility(View.VISIBLE);
                helpCta.setText(R.string.not_registered_cta);
            }
            break;
            case WAITING_FOR_PHONE_NUMBER_RESPONSE:
            {
                instructionsText.setText(R.string.sending_pin);
                secondaryInstructionsText.setVisibility(View.GONE);
                phoneInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                helpCta.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PIN:
            {
                String instructionsFormat = getResources().getString(R.string.login_instructions_2);
                String instructions = String.format(instructionsFormat, TextUtils.formatPhone(storedPhoneNumber, ""));
                instructionsText.setText(instructions);
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText(R.string.log_in);
                backButton.setVisibility(View.VISIBLE);
                helpCta.setVisibility(View.VISIBLE);
                helpCta.setText(R.string.no_pin_cta);
            }
            break;
            case WAITING_FOR_LOGIN_RESPONSE:
            {
                instructionsText.setText(R.string.logging_in);
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                helpCta.setVisibility(View.GONE);
            }
            break;
            case COMPLETE:
            {
                instructionsText.setText(R.string.logging_in);
            }
            break;
        }
    }

    //Helpers

    private void showLoginError(int stringId)
    {
        showLoginError(getResources().getString(stringId));
    }

    private void showLoginError(String error)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }

}
