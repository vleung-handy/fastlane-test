package com.handy.portal.manager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowDownloadManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class VersionManagerTest extends RobolectricGradleTestWrapper
{
    @Mock
    private Bus bus;
    @Mock
    private DataManager dataManager;
    @Mock
    private PrefsManager prefsManager;
    @Mock
    private BuildConfigWrapper buildConfigWrapper;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Activity activity;
    @Mock
    private UpdateDetails updateDetails;

    @Captor
    private ArgumentCaptor<DataManager.Callback<UpdateDetails>> updateCheckCallbackCaptor;
    @Captor
    private ArgumentCaptor<Object> eventArgumentCaptor;

    private VersionManager versionManager;
    private ShadowDownloadManager downloadManager;
    private DataManager.Callback<UpdateDetails> updateDetailsCallBack;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        versionManager = new VersionManager(RuntimeEnvironment.application, bus, dataManager, prefsManager, buildConfigWrapper);

        reset(bus);

        downloadManager = shadowOf((DownloadManager) RuntimeEnvironment.application.getSystemService(Context.DOWNLOAD_SERVICE));

        versionManager.onUpdateCheckRequest(new HandyEvent.RequestUpdateCheck(activity));
        verify(dataManager).checkForUpdates(anyString(), anyInt(), updateCheckCallbackCaptor.capture());
        updateDetailsCallBack = updateCheckCallbackCaptor.getValue();

        when(updateDetails.getDownloadUrl()).thenReturn("http://cats.org/app.apk");
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNeeded_thenRegisterApkDownloadBroadcastReceiver() throws Exception
    {
        when(updateDetails.getShouldUpdate()).thenReturn(true);

        updateDetailsCallBack.onSuccess(updateDetails);

        List<ShadowApplication.Wrapper> registeredReceivers = shadowOf(RuntimeEnvironment.application).getRegisteredReceivers();
        BroadcastReceiver lastAddedBroadcastReceiver = registeredReceivers.get(registeredReceivers.size() - 1).getBroadcastReceiver();
        assertThat(lastAddedBroadcastReceiver, equalTo(versionManager.downloadReceiver));
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNeeded_thenPostUpdateAvailableEvent() throws Exception
    {
        when(updateDetails.getShouldUpdate()).thenReturn(true);

        updateDetailsCallBack.onSuccess(updateDetails);

        verify(bus).post(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue(), instanceOf(HandyEvent.ReceiveUpdateAvailableSuccess.class));
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNeeded_thenDownloadNewApk() throws Exception
    {
        when(updateDetails.getShouldUpdate()).thenReturn(true);

        updateDetailsCallBack.onSuccess(updateDetails);

        assertThat(downloadManager.getRequestCount(), equalTo(1));
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNotNeeded_thenDoNotDownloadAnything() throws Exception
    {
        when(updateDetails.getShouldUpdate()).thenReturn(false);

        updateDetailsCallBack.onSuccess(updateDetails);

        assertThat(downloadManager.getRequestCount(), equalTo(0));
    }

    @Test
    public void givenUnsuccessfulUpdateCheck_thenDoNotDownloadAnything() throws Exception
    {
        updateDetailsCallBack.onError(mock(DataManager.DataManagerError.class));

        assertThat(downloadManager.getRequestCount(), equalTo(0));
    }
}
