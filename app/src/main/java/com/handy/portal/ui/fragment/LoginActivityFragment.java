package com.handy.portal.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.data.EnvironmentSwitcher;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.PhoneInputTextView;
import com.handy.portal.ui.widget.PinCodeInputTextView;
import com.handy.portal.util.FlavorUtils;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.handy.portal.data.EnvironmentSwitcher.Environment;


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
    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.back_button)
    ImageButton backButton;
    @InjectView(R.id.help_cta)
    TextView helpCta;
    @InjectView(R.id.login_help)
    TextView loginHelpText;

    @Inject
    EnvironmentSwitcher environmentSwitcher;

    private static final boolean DEBUG_SKIP_LOGIN = false; //bypass the native login and use the old web login

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

        if (DEBUG_SKIP_LOGIN)
        {
            startActivity(new Intent(this.getActivity(), MainActivity.class));
        }

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
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(phoneNumberEditText.getWindowToken(), 0);
                        }
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

        loginHelpText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getResources().getString(R.string.login_help_email_address), null));
                intent.putExtra(Intent.EXTRA_SUBJECT, R.string.login_help_email_subject);
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.login_help_choose_email_client)));
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
                if (event.pinRequestDetails.getSuccess())
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
            }
            else
            {
                showLoginError(R.string.login_error_connectivity);
                changeState(LoginState.INPUTTING_PIN);
            }
        }
    }

    @OnClick(R.id.logo)
    protected void selectEnvironment()
    {
        if (!FlavorUtils.isStageFlavor()) return;

        final Environment[] environments = Environment.values();
        String[] environmentNames = new String[environments.length];
        Environment currentEnvironment = environmentSwitcher.getEnvironment();
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
                        environmentSwitcher.setEnvironment(environments[which]);
                    }
                });
        builder.create().show();
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
                phoneInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
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
                helpCta.setVisibility(View.INVISIBLE);
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
