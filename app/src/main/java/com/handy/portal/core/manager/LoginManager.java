package com.handy.portal.core.manager;

import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.model.LoginDetails;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.data.DataManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class LoginManager
{
    private final EventBus bus;
    private DataManager dataManager;
    private PrefsManager prefsManager;

    @Inject
    public LoginManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.prefsManager = prefsManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onRequestPinCode(HandyEvent.RequestPinCode event)
    {
        dataManager.requestPinCode(event.phoneNumber, new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(final SuccessWrapper pinRequestDetails)
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

    /**
     * request single login token deeplink send via sms to the give phone number.
     *
     * @param phoneNumber phone number
     * @param callback    Callback handler
     */
    public void requestSlt(String phoneNumber, DataManager.Callback<SuccessWrapper> callback)
    {
        dataManager.requestSlt(phoneNumber, callback);
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

    /**
     * Login with single login token
     *
     * @param n   String given by the slt deeplink
     * @param sig String give by the deeplink
     * @param slt single login token
     */
    public void loginWithSlt(String n, String sig, String slt)
    {
        dataManager.requestLoginWithSlt(n, sig, slt, new DataManager.Callback<LoginDetails>()
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
        prefsManager.setSecureString(PrefsKey.LAST_PROVIDER_ID, loginDetails.getProviderId());
        prefsManager.setSecureString(PrefsKey.AUTH_TOKEN, loginDetails.getAuthToken());
    }

}
