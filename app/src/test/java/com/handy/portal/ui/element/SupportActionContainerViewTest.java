package com.handy.portal.ui.element;

import android.view.View;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.util.SupportActionUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;

public class SupportActionContainerViewTest extends RobolectricGradleTestWrapper
{
    private SupportActionContainerView mView;

    @Before
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
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
