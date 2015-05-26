package com.handy.portal.core;

import com.handy.portal.BuildConfig;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public final class LoginManager
{
    private final Bus bus;
    private DataManager dataManager;
    private LoginDetails loginDetails;

    @Inject
    LoginManager(final Bus bus)
    {
        this.bus = bus;
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
                        //TODO: Set our local user based on the return value? Need to wait for the api version that sends back userId
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        bus.post(new Event.LoginRequestReceivedEvent(new LoginDetails(), false));
                    }
                }
        );
    }

    private void saveLoginDetails(final LoginDetails loginDetails)
    {
        this.loginDetails = loginDetails;
    }

    public String getLoggedInUserId()
    {
        String loggedInUserId = "";

        if(BuildConfig.BUILD_TYPE.equals("debug")) //add check using flavor to only allow this hack on debug flavors
        {
            loggedInUserId = "11"; //for quick hacky debug work
        }

        if(this.loginDetails != null)
        {
            loggedInUserId = this.loginDetails.getUserCredentialsId();
        }

        return(loggedInUserId);
    }

}
