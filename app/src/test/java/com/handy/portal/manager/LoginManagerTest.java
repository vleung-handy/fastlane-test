package com.handy.portal.manager;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private Bus bus;
    @Mock
    private DataManager dataManager;
    @Mock
    private PrefsManager prefsManager;

    @Captor
    private ArgumentCaptor<DataManager.Callback<PinRequestDetails>> pinCodeRequestCallbackCaptor;
    @Captor
    private ArgumentCaptor<DataManager.Callback<LoginDetails>> loginRequestCallbackCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.ReceivePinCodeSuccess> pinCodeSuccessEventArgumentCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.ReceivePinCodeError> pinCodeErrorEventArgumentCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.ReceiveLoginSuccess> loginSuccessEventArgumentCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.ReceiveLoginError> loginErrorEventArgumentCaptor;

    private LoginManager loginManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        loginManager = new LoginManager(bus, dataManager, prefsManager);
    }

    @Test
    public void onSuccessfulRequestPinCode_shouldPostPinCodeRequestReceivedEventWithDetails() throws Exception
    {
        requestPinCodeAndCaptureCallback();
        PinRequestDetails pinRequestDetails = mock(PinRequestDetails.class);
        when(pinRequestDetails.getSuccess()).thenReturn(true);
        pinCodeRequestCallbackCaptor.getValue().onSuccess(pinRequestDetails);
        verify(bus).post(pinCodeSuccessEventArgumentCaptor.capture());
        assertThat(pinCodeSuccessEventArgumentCaptor.getValue().pinRequestDetails, equalTo(pinRequestDetails));
        assertTrue(pinCodeSuccessEventArgumentCaptor.getValue().pinRequestDetails.getSuccess());
    }

    @Test
    public void onUnsuccessfulRequestPinCode_shouldPostPinCodeRequestReceivedEventWithNullDetails() throws Exception
    {
        requestPinCodeAndCaptureCallback();
        pinCodeRequestCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));
        verify(bus).post(pinCodeErrorEventArgumentCaptor.capture());
        assertNotNull(pinCodeErrorEventArgumentCaptor.getValue().error);
    }

    @Test
    public void onSuccessfulRequestLogin_shouldPostLoginRequestReceivedEventWithDetails() throws Exception
    {
        requestLoginAndCaptureCallback();

        LoginDetails loginDetails = mock(LoginDetails.class);
        when(loginDetails.getSuccess()).thenReturn(true);
        loginRequestCallbackCaptor.getValue().onSuccess(loginDetails);

        verify(bus).post(loginSuccessEventArgumentCaptor.capture());
        assertThat(loginSuccessEventArgumentCaptor.getValue().loginDetails, equalTo(loginDetails));
        assertTrue(loginSuccessEventArgumentCaptor.getValue().loginDetails.getSuccess());
    }

    @Test
    public void onUnsuccessfulRequestLogin_shouldPostLoginRequestReceivedEventWithNullDetails() throws Exception
    {
        requestLoginAndCaptureCallback();

        loginRequestCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));

        verify(bus).post(loginErrorEventArgumentCaptor.capture());
        assertNotNull(loginErrorEventArgumentCaptor.getValue().error);
    }

    private void requestPinCodeAndCaptureCallback()
    {
        loginManager.onRequestPinCode(mock(HandyEvent.RequestPinCode.class));
        verify(dataManager).requestPinCode(anyString(), pinCodeRequestCallbackCaptor.capture());
    }

    private void requestLoginAndCaptureCallback()
    {
        loginManager.onRequestLogin(mock(HandyEvent.RequestLogin.class));
        verify(dataManager).requestLogin(anyString(), anyString(), loginRequestCallbackCaptor.capture());
    }
}
