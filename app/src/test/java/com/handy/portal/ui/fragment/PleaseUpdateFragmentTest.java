package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.net.Uri;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.UpdateManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.robolectric.util.SupportFragmentTestUtil;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.Shadows.shadowOf;

public class PleaseUpdateFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock
    private UpdateManager updateManager;

    @InjectMocks
    private PleaseUpdateFragment fragment;

    @Before
    public void setUp() throws Exception
    {
        fragment = new PleaseUpdateFragment();
        SupportFragmentTestUtil.startFragment(fragment);

        initMocks(this);
    }

    @Test
    public void whenDownloadButtonClicked_thenSendDownloadIntent() throws Exception
    {
        when(updateManager.getDownloadURL()).thenReturn("http://url.cats");

        fragment.getView().findViewById(R.id.download_button).performClick();

        Intent expectedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://url.cats"));
        Intent actualIntent = shadowOf(fragment.getActivity()).getNextStartedActivity();

        assertThat(actualIntent, equalTo(expectedIntent));
    }
}
