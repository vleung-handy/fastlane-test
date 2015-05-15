package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.PhoneInputTextView;
import com.handy.portal.ui.widget.PinCodeInputTextView;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends InjectedFragment
{
    private static final boolean DEBUG_FAKE_RESPONSES = true;
    private static final boolean DEBUG_SKIP_LOGIN = false;

    private String storedPhoneNumber;

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
    @InjectView(R.id.logo)
    ImageView logo;

    private enum LoginPhase
    {
        INIT,
        INPUTTING_PHONE_NUMBER,
        WAITING_FOR_PHONE_NUMBER_RESPONSE,
        INPUTTING_PIN,
        WAITING_FOR_LOGIN_RESPONSE,
        COMPLETE
    }

    private LoginPhase currentLoginPhase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container);

        ButterKnife.inject(this, view);

        changeState(LoginPhase.INIT);

        //fancy spinning logo
        logo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                AnimationDrawable logoSpin = (AnimationDrawable) logo.getBackground();
                logoSpin.stop();
                logoSpin.start();
            }
        });

        registerControlListeners();

        if(DEBUG_SKIP_LOGIN)
        {
            //hack in a user and pin to simulate sending

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
                switch (currentLoginPhase)
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
                    default:
                    {
                    }
                }
            }
        });


        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Clicked back button");

                switch (currentLoginPhase)
                {
                    case INPUTTING_PIN:
                    {
                        changeState(LoginPhase.INPUTTING_PHONE_NUMBER);
                    }
                    break;
                }
            }
        });

    }

    private void sendPhoneNumber(String phoneNumber)
    {
        //sendPhoneNumber
        //send a pin request to the server, transition to waiting for pin phase
        System.out.println("Sending phone number : " + phoneNumber);
        storedPhoneNumber = phoneNumber; //remember so they don't have to reinput once they receive their pin
        changeState(LoginPhase.WAITING_FOR_PHONE_NUMBER_RESPONSE);

        bus.post(new Event.RequestPinCodeEvent(phoneNumber));

        if(DEBUG_FAKE_RESPONSES)
        {
            bus.post(new Event.PinCodeRequestReceivedEvent(true));
        }

    }

    @Subscribe
    public void onPinCodeRequestReceived(Event.PinCodeRequestReceivedEvent event)
    {
        if(currentLoginPhase == LoginPhase.WAITING_FOR_PHONE_NUMBER_RESPONSE)
        {
            if(event.success)
            {
                System.out.println("User should expect a pin code");
                changeState(LoginPhase.INPUTTING_PIN);
            }
            else
            {
                System.out.println("Something went wrong user needs to rerequest pin code");
            }
        }
    }

    @Subscribe
    public void onLoginRequestReceived(Event.LoginRequestReceivedEvent event)
    {
        if(currentLoginPhase == LoginPhase.WAITING_FOR_LOGIN_RESPONSE)
        {
            if (event.success)
            {
                System.out.println("User is set and logged in : " + event.userId);
                changeState(LoginPhase.COMPLETE);
            } else
            {
                System.out.println("Something went wrong user needs to re login");
            }
        }
    }

    private boolean validateLength(String s, int l)
    {
        return (s.length() == l);
    }

    private void sendLoginRequest(String phoneNumber, String pinCode)
    {
        changeState(LoginPhase.WAITING_FOR_LOGIN_RESPONSE);

        //send a login request to the server with our phoneNumber and pin
        bus.post(new Event.RequestLoginEvent(phoneNumber, pinCode));

        if(DEBUG_FAKE_RESPONSES)
        {
            bus.post(new Event.LoginRequestReceivedEvent("11", true));
        }
    }

    private void changeState(LoginPhase phase)
    {
        if(currentLoginPhase == phase)
        {
            return;
        }

        currentLoginPhase = phase;

        updateDisplay(currentLoginPhase);


        if(phase == LoginPhase.INIT)
        {
            changeState(LoginPhase.INPUTTING_PHONE_NUMBER);
        }

        if(phase == LoginPhase.COMPLETE)
        {
            //transition to main activity once we have a user
            startActivity(new Intent(this.getActivity(), MainActivity.class));
        }
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
    }


    private void updateDisplay(LoginPhase phase)
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
                secondaryInstructionsText.setText(R.string.login_instructions_1_b);
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
                secondaryInstructionsText.setVisibility(View.GONE);
                phoneInputLayout.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PIN:
            {
                instructionsText.setText(R.string.login_instructions_2);
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
                instructionsText.setText("");
                phoneInputLayout.setVisibility(View.GONE);
                pinCodeInputLayout.setVisibility(View.GONE);
            }
            break;
        }

    }

}
