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
        dataManager.requestPinCode(event.phoneNumber, new DataManager.Callback<String>()
                {
                    @Override
                    public void onSuccess(final String status)
                    {
                        bus.post(new Event.PinCodeRequestReceivedEvent(true));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        System.err.println("Failed to request pin code " + error);
                        bus.post(new Event.PinCodeRequestReceivedEvent(false)); //need to let client know about the fail
                    }
                }
        );
    }

    @Subscribe
    public void onRequestLogin(Event.RequestLoginEvent event)
    {
        dataManager.requestLogin(event.phoneNumber, event.pinCode, new DataManager.Callback<String>()
                {
                    @Override
                    public void onSuccess(final String loginCode)
                    {
                        bus.post(new Event.LoginRequestReceivedEvent(loginCode, true));

                        //TODO: Set our local user based on the return value? Provider users may be less complex than consumer users
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        System.err.println("Failed to login " + error);
                        bus.post(new Event.LoginRequestReceivedEvent("", false));
                    }
                }
        );
    }

}
