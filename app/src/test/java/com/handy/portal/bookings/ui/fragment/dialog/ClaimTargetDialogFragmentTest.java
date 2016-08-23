package com.handy.portal.bookings.ui.fragment.dialog;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.bookings.model.BookingClaimDetails;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

public class ClaimTargetDialogFragmentTest extends RobolectricGradleTestWrapper
{

    ClaimTargetDialogFragment mFragment;

    private BookingClaimDetails.ClaimTargetInfo claimTargetInfo;

    @Before
    public void setUp() throws Exception
    {
        mFragment = new ClaimTargetDialogFragment();
        SupportFragmentTestUtil.startFragment(mFragment);
    }

    @Test
    public void testUpdateDisplay()
    {
        //confirm the correct text is shown based on information from claimTargetInfo



    }
}