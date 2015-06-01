package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public final class LoginManager
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

    //Dagger doesn't have a good way to resolve cyclical injection dependencies from what I can tell
    //Hopefully there is some elegant solution that can be found with more googling
    public void setDataManager(DataManager dataManager)
    {
        this.dataManager = dataManager;
    }

    @Subscribe
    public void onRequestPinCode(Event.RequestPinCodeEvent event)
    {
        dataManager.requestPinCode(event.phoneNumber, new DataManager.Callback<PinRequestDetails>()
                {
                    @Override
                    public void onSuccess(final PinRequestDetails pinRequestDetails)
                    {
                        bus.post(new Event.PinCodeRequestReceivedEvent(pinRequestDetails, true));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new Event.PinCodeRequestReceivedEvent(null, false));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestLogin(Event.RequestLoginEvent event)
    {
        dataManager.requestLogin(event.phoneNumber, event.pinCode, new DataManager.Callback<LoginDetails>()
                {
                    @Override
                    public void onSuccess(final LoginDetails loginDetails)
                    {
                        saveLoginDetails(loginDetails);
                        bus.post(new Event.LoginRequestReceivedEvent(loginDetails, true));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new Event.LoginRequestReceivedEvent(null, false));
                    }
                }
        );
    }

    private void saveLoginDetails(final LoginDetails loginDetails)
    {
        prefs.edit().putString(USER_CREDENTIALS_ID_KEY, loginDetails.getUserCredentialsId()).apply();
    }

    public String getLoggedInUserId()
    {
        return prefs.getString(USER_CREDENTIALS_ID_KEY, null);
    }

}
