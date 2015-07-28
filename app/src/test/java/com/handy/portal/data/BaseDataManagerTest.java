package com.handy.portal.data;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.squareup.otto.Bus;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class BaseDataManagerTest extends RobolectricGradleTestWrapper
{
    // TODO: Refactor BaseDataManager. The tests are way too redundant.

    @Mock
    HandyRetrofitService service;
    @Mock
    HandyRetrofitEndpoint endpoint;
    @Mock
    Bus bus;
    @Mock
    PrefsManager prefsManager;
    @Mock
    LoginManager loginManager;


    private BaseDataManager dataManager;

    @Captor
    ArgumentCaptor<HandyRetrofitCallback> callbackCaptor;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);

        dataManager = new BaseDataManager(service, endpoint, prefsManager);
    }

    @Test
    public void testSuccessfulCheckForUpdates() throws Exception
    {
        final Object[] responseCatcher = new Object[1];
        dataManager.checkForUpdates("yummy flavor", 1, new DataManager.Callback<UpdateDetails>()
        {
            @Override
            public void onSuccess(UpdateDetails response)
            {
                responseCatcher[0] = response;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });

        verify(service).checkUpdates(anyString(), anyInt(), callbackCaptor.capture());
        HandyRetrofitCallback callback = callbackCaptor.getValue();
        // TODO: Move test JSON data to files
        callback.success(new JSONObject("{\"success\":\"true\", \"should_update\":\"true\", \"download_url\":\"cats.org\"}"));

        UpdateDetails updateDetails = (UpdateDetails) responseCatcher[0];
        assertTrue(updateDetails.getSuccess());
        assertTrue(updateDetails.getShouldUpdate());
        assertThat(updateDetails.getDownloadUrl(), equalTo("cats.org"));
    }

    @Test
    public void testSuccessfulPinCodeRequest() throws Exception
    {
        final Object[] responseCatcher = new Object[1];
        dataManager.requestPinCode("1231231234", new DataManager.Callback<PinRequestDetails>()
        {
            @Override
            public void onSuccess(PinRequestDetails response)
            {
                responseCatcher[0] = response;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });

        verify(service).requestPinCode(anyString(), callbackCaptor.capture());
        HandyRetrofitCallback callback = callbackCaptor.getValue();
        // TODO: Move test JSON data to files
        callback.success(new JSONObject("{\"success\":\"true\"}"));

        PinRequestDetails pinRequestDetails = (PinRequestDetails) responseCatcher[0];
        assertTrue(pinRequestDetails.getSuccess());
    }

    @Test
    public void testSuccessfulLoginRequest() throws Exception
    {
        final Object[] responseCatcher = new Object[1];
        dataManager.requestLogin("1231231234", "1234", new DataManager.Callback<LoginDetails>()
        {
            @Override
            public void onSuccess(LoginDetails response)
            {
                responseCatcher[0] = response;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });

        verify(service).requestLogin(anyString(), anyString(), callbackCaptor.capture());
        HandyRetrofitCallback callback = callbackCaptor.getValue();
        // TODO: Move test JSON data to files
        callback.success(new JSONObject("{\"success\":\"true\", \"user_credentials\":\"credentials\"}"));

        LoginDetails loginDetails = (LoginDetails) responseCatcher[0];
        assertTrue(loginDetails.getSuccess());
        assertThat(loginDetails.getAuthToken(), equalTo("credentials"));
    }

}
