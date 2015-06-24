package com.handy.portal.core;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private Bus bus;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SecurePreferences prefs;
    @Mock
    private DataManager dataManager;

    @Captor
    private ArgumentCaptor<DataManager.Callback<PinRequestDetails>> pinCodeRequestCallbackCaptor;
    @Captor
    private ArgumentCaptor<DataManager.Callback<LoginDetails>> loginRequestCallbackCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.PinCodeRequestSuccess> pinCodePostEventArgumentCaptor;
    @Captor
    private ArgumentCaptor<HandyEvent.LoginRequestSuccess> loginPostEventArgumentCaptor;

    private LoginManager loginManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        loginManager = new LoginManager(bus, prefs, dataManager);
    }

    @Test
    public void onSuccessfulRequestPinCode_shouldPostPinCodeRequestReceivedEventWithDetails() throws Exception
    {
        requestPinCodeAndCaptureCallback();

        PinRequestDetails pinRequestDetails = mock(PinRequestDetails.class);
        pinCodeRequestCallbackCaptor.getValue().onSuccess(pinRequestDetails);

        verify(bus).post(pinCodePostEventArgumentCaptor.capture());
        assertThat(pinCodePostEventArgumentCaptor.getValue().pinRequestDetails, equalTo(pinRequestDetails));
        assertTrue(pinCodePostEventArgumentCaptor.getValue().success);
    }

    @Test
    public void onUnsuccessfulRequestPinCode_shouldPostPinCodeRequestReceivedEventWithNullDetails() throws Exception
    {
        requestPinCodeAndCaptureCallback();

        pinCodeRequestCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));

        verify(bus).post(pinCodePostEventArgumentCaptor.capture());
        assertNull(pinCodePostEventArgumentCaptor.getValue().pinRequestDetails);
        assertFalse(pinCodePostEventArgumentCaptor.getValue().success);
    }

    @Test
    public void onSuccessfulRequestLogin_shouldPostLoginRequestReceivedEventWithDetails() throws Exception
    {
        requestLoginAndCaptureCallback();

        LoginDetails loginDetails = mock(LoginDetails.class);
        loginRequestCallbackCaptor.getValue().onSuccess(loginDetails);

        verify(bus).post(loginPostEventArgumentCaptor.capture());
        assertThat(loginPostEventArgumentCaptor.getValue().loginDetails, equalTo(loginDetails));
        assertTrue(loginPostEventArgumentCaptor.getValue().success);
    }

    @Test
    public void onUnsuccessfulRequestLogin_shouldPostLoginRequestReceivedEventWithNullDetails() throws Exception
    {
        requestLoginAndCaptureCallback();

        loginRequestCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));

        verify(bus).post(loginPostEventArgumentCaptor.capture());
        assertNull(loginPostEventArgumentCaptor.getValue().loginDetails);
        assertFalse(loginPostEventArgumentCaptor.getValue().success);
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
