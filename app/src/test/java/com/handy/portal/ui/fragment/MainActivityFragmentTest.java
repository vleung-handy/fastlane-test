package com.handy.portal.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MainActivityFragmentTest extends RobolectricGradleTestWrapper
{
    private MainActivityFragment mFragment;

    @Mock
    private ConfigurationResponse mConfigurationResponse;
    @Inject
    ConfigManager mConfigManager;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);
        when(mConfigManager.getConfigurationResponse()).thenReturn(mConfigurationResponse);
        when(mConfigurationResponse.shouldShowNotificationMenuButton()).thenReturn(false);
        mFragment = new MainActivityFragment();
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);
    }

    @Test
    public void shouldHaveActionBar() throws Exception
    {
        assertNotNull(((AppCompatActivity) mFragment.getActivity()).getSupportActionBar());
    }

    @Ignore
    @Test
    public void givenNoTabSelected_whenActivityResumes_thenLoadJobsScreen() throws Exception
    {
        assertThat(getScreenFragment(), instanceOf(AvailableBookingsFragment.class));
    }

    public Fragment getScreenFragment()
    {
        List<Fragment> fragments = mFragment.getActivity().getSupportFragmentManager().getFragments();
        return fragments.get(fragments.size() - 1);
    }
}
