package com.handy.portal.core.ui.fragment;

import android.content.Intent;
import android.net.Uri;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.updater.ui.PleaseUpdateFragment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

@Ignore //TODO: re-enable when we can mock intent.resolveActivity(context.getPackageManager())
public class PleaseUpdateFragmentTest extends RobolectricGradleTestWrapper {
    @Mock
    private VersionManager versionManager;

    @InjectMocks
    private PleaseUpdateFragment fragment;

    @Before
    public void setUp() throws Exception {
        fragment = new PleaseUpdateFragment();
        SupportFragmentTestUtil.startFragment(fragment);

        initMocks(this);
    }

    @Test
    public void whenDownloadButtonClicked_thenSendInstallIntent() throws Exception {
        Uri mockUri = mock(Uri.class);
        when(versionManager.getNewApkUri(fragment.getContext())).thenReturn(mockUri);

        if (fragment.getView() != null) {
            fragment.getView().findViewById(R.id.update_button).performClick();
        }

        Intent expectedIntent = new Intent(Intent.ACTION_VIEW);
        expectedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        expectedIntent.setDataAndType(mockUri, VersionManager.APK_MIME_TYPE);
        Intent actualIntent = shadowOf(fragment.getActivity()).getNextStartedActivity();

        assertThat(actualIntent, equalTo(expectedIntent));
    }
}
