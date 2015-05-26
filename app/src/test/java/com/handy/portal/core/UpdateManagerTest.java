package com.handy.portal.core;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpdateManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private Bus bus;
    @Mock
    private DataManager dataManager;
    @Captor
    private ArgumentCaptor<DataManager.Callback<UpdateDetails>> updateCheckCallbackCaptor;
    @Captor
    private ArgumentCaptor<Event.UpdateCheckRequestReceivedEvent> updatePostEventArgumentCaptor;

    private UpdateManager updateManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        updateManager = new UpdateManager(bus, dataManager);

        updateManager.onUpdateCheckRequest(mock(Event.UpdateCheckEvent.class));
        verify(dataManager).checkForUpdates(anyInt(), updateCheckCallbackCaptor.capture());
    }

    @Test
    public void onSuccessfulUpdateCheck_shouldPostUpdateCheckRequestReceivedEventWithDetails() throws Exception
    {
        UpdateDetails updateDetails = mock(UpdateDetails.class);
        updateCheckCallbackCaptor.getValue().onSuccess(updateDetails);

        verify(bus).post(updatePostEventArgumentCaptor.capture());
        assertThat(updatePostEventArgumentCaptor.getValue().updateDetails, equalTo(updateDetails));
        assertTrue(updatePostEventArgumentCaptor.getValue().success);
    }

    @Test
    public void onSuccessfulUpdateCheck_shouldSetDownloadUrl() throws Exception
    {
        UpdateDetails updateDetails = mock(UpdateDetails.class);
        when(updateDetails.getDownloadUrl()).thenReturn("http://cats.org/app.apk");
        updateCheckCallbackCaptor.getValue().onSuccess(updateDetails);

        assertThat(updateManager.getDownloadURL(), equalTo("http://cats.org/app.apk"));
    }

    @Test
    public void onUnsuccessfulUpdateCheck_shouldPostUpdateCheckRequestReceivedEventWithNullDetails() throws Exception
    {
        updateCheckCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));

        verify(bus).post(updatePostEventArgumentCaptor.capture());
        assertNull(updatePostEventArgumentCaptor.getValue().updateDetails);
        assertFalse(updatePostEventArgumentCaptor.getValue().success);
    }
}
