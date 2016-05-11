package com.handy.portal.ui.element;

import android.view.View;

import com.handy.portal.BuildConfig;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.util.SupportActionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class,
        packageName = "com.handy.portal",
        sdk = 19)
public class SupportActionContainerViewTest
{
    private SupportActionContainerView mView;

    @Before
    public void setUp() throws Exception
    {
        mView = new SupportActionContainerView(RuntimeEnvironment.application,
                SupportActionUtils.ETA_ACTION_NAMES, new Booking());
    }

    @Test
    public void shouldNotBeSeenIfSizeIsZero()
    {
        mView = new SupportActionContainerView(RuntimeEnvironment.application,
                SupportActionUtils.ETA_ACTION_NAMES, new Booking());

        assertEquals(View.GONE, mView.getVisibility());
    }
}
