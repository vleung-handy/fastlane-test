package com.handy.portal.core;

import android.app.Activity;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VersionManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private Bus bus;
    @Mock
    private DataManager dataManager;
    @Mock
    private BuildConfigWrapper buildConfigWrapper;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Activity activity;
    @Captor
    private ArgumentCaptor<DataManager.Callback<UpdateDetails>> updateCheckCallbackCaptor;
    @Captor
    private ArgumentCaptor<Event.UpdateCheckEvent> updateCheckEventArgumentCaptor;
    @Captor
    private ArgumentCaptor<Event.UpdateAvailable> updateAvailableEventArgumentCaptor;

    private VersionManager versionManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        versionManager = new VersionManager(bus, dataManager, buildConfigWrapper);

        reset(bus);

        versionManager.onUpdateCheckRequest(new Event.UpdateCheckEvent(activity));
        verify(dataManager).checkForUpdates(anyString(), anyInt(), updateCheckCallbackCaptor.capture());
    }

    @Test
    public void onSuccessfulUpdateCheck_shouldPostUpdateAvailableEvent() throws Exception
    {
        UpdateDetails updateDetails = mock(UpdateDetails.class);
        when(updateDetails.getShouldUpdate()).thenReturn(true);
        updateCheckCallbackCaptor.getValue().onSuccess(updateDetails);

        verify(bus).post(updateAvailableEventArgumentCaptor.capture());
        assertThat(updateAvailableEventArgumentCaptor.getValue(), instanceOf(Event.UpdateAvailable.class));
    }

    @Test
    public void onSuccessfulUpdateCheck_shouldSetDownloadUrl() throws Exception
    {
        UpdateDetails updateDetails = mock(UpdateDetails.class);
        when(updateDetails.getDownloadUrl()).thenReturn("http://cats.org/app.apk");
        updateCheckCallbackCaptor.getValue().onSuccess(updateDetails);

        assertThat(versionManager.getDownloadURL(), equalTo("http://cats.org/app.apk"));
    }

    @Test
    public void onUnsuccessfulUpdateCheck_shouldNotPostUpdateAvailableEvent() throws Exception
    {
        updateCheckCallbackCaptor.getValue().onError(mock(DataManager.DataManagerError.class));

        verifyZeroInteractions(bus);
    }
}
