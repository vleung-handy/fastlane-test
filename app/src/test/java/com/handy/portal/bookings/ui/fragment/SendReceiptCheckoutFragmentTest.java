package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.handy.portal.R;
import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.ui.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class SendReceiptCheckoutFragmentTest extends RobolectricGradleTestWrapper
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Booking booking;
    @Captor
    private ArgumentCaptor<Object> captor;

    private SendReceiptCheckoutFragment fragment;

    @Before
    public void setUp() throws Exception
    {
        initMocks(this);
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);

        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        activityController.start().resume().visible();

        Booking.User user = mock(Booking.User.class);
        when(user.getFirstName()).thenReturn("Mike");

        List<Booking.BookingInstructionUpdateRequest> list = new ArrayList<>();
        list.add(mock(Booking.BookingInstructionUpdateRequest.class));

        when(booking.getId()).thenReturn("1");
        when(booking.getCustomerPreferences()).thenReturn(list);
        when(booking.getUser()).thenReturn(user);

        fragment = new SendReceiptCheckoutFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);

        FragmentManager fragmentManager = activityController.get().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(fragment, null)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    @Test
    public void onCheckOutSuccess_switchToScheduleTabAndDisplayToast() throws Exception
    {
        fragment.onReceiveNotifyJobCheckOutSuccess(new HandyEvent.ReceiveNotifyJobCheckOutSuccess(null));

        assertThat(getBusCaptorValue(NavigationEvent.NavigateToPage.class).targetPage, equalTo(MainViewPage.SCHEDULED_JOBS));
        assertThat(ShadowToast.getTextOfLatestToast(), equalTo(fragment.getString(R.string.check_out_success)));
    }

    private <T> T getBusCaptorValue(Class<T> classType)
    {
        verify(fragment.getBus(), atLeastOnce()).post(captor.capture());
        return TestUtils.getBusCaptorValue(captor, classType);
    }
}
