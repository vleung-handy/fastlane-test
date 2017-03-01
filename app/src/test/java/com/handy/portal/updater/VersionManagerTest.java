package com.handy.portal.updater;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.CheckApplicationCapabilitiesUtils;
import com.handy.portal.updater.model.UpdateDetails;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowDownloadManager;
import org.robolectric.shadows.ShadowEnvironment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class VersionManagerTest extends RobolectricGradleTestWrapper {
    @Mock
    private EventBus bus;
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
    @Mock
    private PackageManager packageManager;

    @Captor
    private ArgumentCaptor<DataManager.Callback<UpdateDetails>> updateCheckCallbackCaptor;
    @Captor
    private ArgumentCaptor<Object> eventArgumentCaptor;

    private VersionManager versionManager;
    private ShadowDownloadManager downloadManager;
    private DataManager.Callback<UpdateDetails> updateDetailsCallBack;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        Context applicationSpy = spy(RuntimeEnvironment.application);
        when(applicationSpy.getPackageManager()).thenReturn(packageManager);
        when(packageManager.getApplicationEnabledSetting(CheckApplicationCapabilitiesUtils.DOWNLOAD_MANAGER_PACKAGE_NAME)).thenReturn(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);

        versionManager = new VersionManager(applicationSpy, bus, dataManager, prefsManager, buildConfigWrapper);

        reset(bus);

        downloadManager = shadowOf((DownloadManager) applicationSpy.getSystemService(Context.DOWNLOAD_SERVICE));

        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);

        versionManager.onUpdateCheckRequest(new AppUpdateEvent.RequestUpdateCheck(activity));
        verify(dataManager).checkForUpdates(anyString(), anyInt(), updateCheckCallbackCaptor.capture());
        updateDetailsCallBack = updateCheckCallbackCaptor.getValue();

        when(updateDetails.getDownloadUrl()).thenReturn("http://cats.org/app.apk");
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNeeded_thenRegisterApkDownloadBroadcastReceiver() throws Exception {
        assertNull(versionManager.getUpdateDetails());
        when(updateDetails.getShouldUpdate()).thenReturn(true);
        updateDetailsCallBack.onSuccess(updateDetails);
        assertNotNull(versionManager.getUpdateDetails());
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNeeded_thenPostUpdateAvailableEvent() throws Exception {
        when(updateDetails.getShouldUpdate()).thenReturn(true);

        updateDetailsCallBack.onSuccess(updateDetails);

        verify(bus).post(eventArgumentCaptor.capture());
        assertThat(eventArgumentCaptor.getValue(), instanceOf(AppUpdateEvent.ReceiveUpdateAvailableSuccess.class));
    }

    @Test
    public void givenSuccessfulUpdateCheck_whenUpdateNotNeeded_thenDoNotDownloadAnything() throws Exception {
        when(updateDetails.getShouldUpdate()).thenReturn(false);

        updateDetailsCallBack.onSuccess(updateDetails);

        assertThat(downloadManager.getRequestCount(), equalTo(0));
    }

    @Test
    public void givenUnsuccessfulUpdateCheck_thenDoNotDownloadAnything() throws Exception {
        updateDetailsCallBack.onError(mock(DataManager.DataManagerError.class));

        assertThat(downloadManager.getRequestCount(), equalTo(0));
    }
}
