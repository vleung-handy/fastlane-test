package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class LoginManager
{
    public static final String USER_CREDENTIALS_ID_KEY = "user_credentials_id";
    private final Bus bus;
    private SecurePreferences prefs;
    private DataManager dataManager;

    @Inject
    LoginManager(final Bus bus, final SecurePreferences prefs, final DataManager dataManager)
    {
        this.bus = bus;
        this.prefs = prefs;
        this.dataManager = dataManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onRequestPinCode(HandyEvent.RequestPinCode event)
    {
        dataManager.requestPinCode(event.phoneNumber, new DataManager.Callback<PinRequestDetails>()
                {
                    @Override
                    public void onSuccess(final PinRequestDetails pinRequestDetails)
                    {
                        bus.post(new HandyEvent.ReceivePinCodeSuccess(pinRequestDetails));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceivePinCodeError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestLogin(HandyEvent.RequestLogin event)
    {
        dataManager.requestLogin(event.phoneNumber, event.pinCode, new DataManager.Callback<LoginDetails>()
                {
                    @Override
                    public void onSuccess(final LoginDetails loginDetails)
                    {
                        saveLoginDetails(loginDetails);
                        bus.post(new HandyEvent.ReceiveLoginSuccess(loginDetails));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceiveLoginError(error));
                    }
                }
        );
    }

    private void saveLoginDetails(final LoginDetails loginDetails)
    {
        prefs.edit().putString(USER_CREDENTIALS_ID_KEY, loginDetails.getUserCredentialsId()).apply();
    }

}
