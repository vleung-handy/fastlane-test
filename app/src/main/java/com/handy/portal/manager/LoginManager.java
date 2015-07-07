package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class LoginManager
{
    private final Bus bus;
    private DataManager dataManager;
    private PrefsManager prefsManager;

    @Inject
    public LoginManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
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
        prefsManager.setString(PrefsKey.USER_CREDENTIALS_ID_KEY, loginDetails.getUserCredentialsId());
    }

}
