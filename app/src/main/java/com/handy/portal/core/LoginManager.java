package com.handy.portal.core;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class LoginManager implements Observer
{
    private final Bus bus;
    private DataManager dataManager;

    @Inject
    LoginManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof User)
        {

        }
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

}
