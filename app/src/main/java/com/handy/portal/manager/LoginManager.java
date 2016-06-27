package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class LoginManager
{
    private final EventBus bus;
    private DataManager dataManager;
    private PrefsManager prefsManager;
    private Mixpanel mixpanel;

    @Inject
    public LoginManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager, final Mixpanel mixpanel)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        this.mixpanel = mixpanel;
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
                        mixpanel.onLoginSuccess();
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
        prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, loginDetails.getProviderId());
        prefsManager.setString(PrefsKey.AUTH_TOKEN, loginDetails.getAuthToken());
    }

}
