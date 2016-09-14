package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.data.TestDataManager;
import com.handy.portal.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class BookingDetailsWrapperFragmentTest extends RobolectricGradleTestWrapper
{
    private BookingDetailsWrapperFragment mFragment;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mFragment = new BookingDetailsWrapperFragment();
    }

    @Test
    public void shouldShowErrorPageIfRequestBookingFailed() throws Exception
    {
        Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, TestDataManager.BOOKING_ERROR_ID);
        mFragment.setArguments(args);

        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.mFetchErrorView.getVisibility(), View.VISIBLE);
    }

    @Test
    public void shouldShowBookingDetails() throws Exception
    {
        Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, TestDataManager.BOOKING_UNCLAIMED_ID);
        mFragment.setArguments(args);

//        ShadowApplication app = Shadows.shadowOf(RuntimeEnvironment.application);
//        app.grantPermissions(Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        Context context = mock(Context.class);
//        when(context.checkCallingOrSelfPermission(android.Manifest.permission.READ_PHONE_STATE)).thenReturn(
//                PackageManager.PERMISSION_GRANTED);
//        when(context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)).thenReturn(
//                PackageManager.PERMISSION_GRANTED);
        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.mFetchErrorView.getVisibility(), View.GONE);

        Fragment bookingFragment = mFragment.getChildFragmentManager().findFragmentById(R.id.booking_details_slide_up_panel_container);
        assertNotNull(bookingFragment);
        assertTrue(bookingFragment instanceof BookingFragment);
    }

    @Test
    public void shouldShowInProgressBookingWithChecklist() throws Exception
    {
        Bundle args = new Bundle();
        args.putString(BundleKeys.BOOKING_ID, TestDataManager.BOOKING_IN_PROGRESS_ID);
        mFragment.setArguments(args);

        SupportFragmentTestUtil.startFragment(mFragment, MainActivity.class);

        assertEquals(mFragment.mFetchErrorView.getVisibility(), View.GONE);

        Fragment bookingFragment = mFragment.getChildFragmentManager().findFragmentById(R.id.booking_details_slide_up_panel_container);
        assertNotNull(bookingFragment);
        assertTrue(bookingFragment instanceof InProgressBookingFragment);
    }
}
