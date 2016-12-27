package com.handy.portal.ui.fragment;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.webview.BlockScheduleFragment;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ActivityController;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainActivityFragmentTest2 extends RobolectricGradleTestWrapper
{
    @Inject
    ConfigManager mConfigManager;
    @Inject
    PaymentsManager mPaymentsManager;

    private MainActivityFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        ((TestBaseApplication) RuntimeEnvironment.application.getApplicationContext()).inject(this);
    }

    @Test
    public void shouldShowPaymentBlockingFragment()
    {
        ConfigurationResponse config = mock(ConfigurationResponse.class);
        when(config.shouldBlockClaimsIfMissingAccountInformation()).thenReturn(true);
        when(mConfigManager.getConfigurationResponse()).thenReturn(config);
        when(mPaymentsManager.HACK_directAccessCacheNeedsPayment()).thenReturn(true);

        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        mFragment = (MainActivityFragment) activityController.get().getSupportFragmentManager().getFragments().get(0);
        activityController.start().resume().visible();

        TestUtils.testFragmentNavigation(mFragment, R.id.tab_nav_schedule, PaymentBlockingFragment.class, R.string.payment_blocking_title);
        TestUtils.testFragmentNavigation(mFragment, R.id.tab_nav_available, PaymentBlockingFragment.class, R.string.payment_blocking_title);
    }

    @Test
    public void shouldShowScheduleBlockingFragment()
    {
        ConfigurationResponse config = mock(ConfigurationResponse.class);
        when(config.isBlockCleaner()).thenReturn(true);
        when(mConfigManager.getConfigurationResponse()).thenReturn(config);

        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        mFragment = (MainActivityFragment) activityController.get().getSupportFragmentManager().getFragments().get(0);
        activityController.start().resume().visible();

        TestUtils.testFragmentNavigation(mFragment, R.id.tab_nav_available, BlockScheduleFragment.class, R.string.block_jobs_schedule);
    }
}
