package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.Event;
import com.handy.portal.ui.activity.MainActivity;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends InjectedFragment
{

    private static final boolean DEBUG_FAKE_RESPONSES = true;


    private static final int PHONE_NUMBER_LENGTH = 10;
    private static final int PIN_CODE_LENGTH = 4;

    private String storedPhoneNumber;

    @InjectView(R.id.phone_number_edit_text)
    EditText phoneNumberEditText;
    @InjectView(R.id.pin_code_edit_text)
    EditText pinCodeEditText;
    @InjectView(R.id.login_instructions_text)
    TextView instructionsText;
    @InjectView(R.id.login_button)
    Button loginButton;
    @InjectView(R.id.back_button)
    ImageButton backButton;


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

        changeState(LoginPhase.INPUTTING_PHONE_NUMBER);

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
                System.out.println("Current login phase : " + currentLoginPhase);

                switch (currentLoginPhase)
                {
                    case INIT:
                    {

                    }
                    break;
                    case INPUTTING_PHONE_NUMBER:
                    {
                        String enteredPhoneNumberText = phoneNumberEditText.getText().toString();
                        if (validateLength(enteredPhoneNumberText, PHONE_NUMBER_LENGTH))
                        {
                            sendPhoneNumber(enteredPhoneNumberText);
                        } else
                        {
                            //set warning/red on the text field
                        }
                    }
                    break;
                    case INPUTTING_PIN:
                    {
                        String enteredPin = pinCodeEditText.getText().toString();
                        if (validateLength(enteredPin, PIN_CODE_LENGTH))
                        {
                            sendLoginRequest(storedPhoneNumber, enteredPin);
                        } else
                        {
                            //set warning/red on the text field
                        }
                    }
                    break;
                    case COMPLETE:
                    {

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
                instructionsText.setText("");
                phoneNumberEditText.setVisibility(View.GONE);
                pinCodeEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PHONE_NUMBER:
            {
                instructionsText.setText("Enter your phone number to receive a pin");
                phoneNumberEditText.setVisibility(View.VISIBLE);
                pinCodeEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText("Request PIN");
                backButton.setVisibility(View.GONE);
            }
            break;
            case WAITING_FOR_PHONE_NUMBER_RESPONSE:
            {
                instructionsText.setText("Sending phone number");
                phoneNumberEditText.setVisibility(View.VISIBLE);
                pinCodeEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                loginButton.setText("");
                backButton.setVisibility(View.GONE);
            }
            break;
            case INPUTTING_PIN:
            {
                instructionsText.setText("Enter the pin you received");
                phoneNumberEditText.setVisibility(View.GONE);
                pinCodeEditText.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setText("Login");
                backButton.setVisibility(View.VISIBLE);
            }
            break;
            case WAITING_FOR_LOGIN_RESPONSE:
            {
                instructionsText.setText("Sending ping code");
                phoneNumberEditText.setVisibility(View.VISIBLE);
                pinCodeEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                loginButton.setText("");
                backButton.setVisibility(View.GONE);
            }
            break;
            case COMPLETE:
            {
                instructionsText.setText("");
                phoneNumberEditText.setVisibility(View.GONE);
                pinCodeEditText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
            break;
        }

    }

}
