package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.fragment.dialog.NotificationBlockerDialogFragment;
import com.handy.portal.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.NotificationUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;

    private static Date sToday;
    private NotificationBlockerDialogFragment dialog = new NotificationBlockerDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        configManager.prefetch();
        providerManager.prefetch();
        checkForTerms();
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    private void checkIfNotificationIsEnabled()
    {
        List<Date> dates = new ArrayList<>();
        sToday = DateTimeUtils.getDateWithoutTime(new Date());
        dates.add(sToday);
        bus.post(new HandyEvent.RequestScheduledBookings(dates, true));
    }

    private void checkIfUserShouldUpdatePaymentInfo()
    {
        bus.post(new PaymentEvent.RequestShouldUserUpdatePaymentInfo());
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    @Subscribe
    public void onReceiveScheduledBookingSuccess(HandyEvent.ReceiveScheduledBookingsSuccess event)
    {
        if (!event.day.equals(sToday)) { return; }

        // Do not show notification blocker if now is in between 52 minutes prior and 30 minutes post to a booking.
        long currentTime = System.currentTimeMillis();
        boolean disruptable = true;

        for (Booking booking : event.bookings)
        {
            if (currentTime > booking.getStartDate().getTime() - DateTimeUtils.MILLISECONDS_IN_52_MINS &&
                    currentTime < booking.getEndDate().getTime() + DateTimeUtils.MILLISECONDS_IN_30_MINS)
            {
                disruptable = false;
            }
        }

        if (disruptable && !NotificationUtils.isNotificationEnabled(this) && !dialog.isAdded())
        {
            dialog.show(getSupportFragmentManager(), NotificationBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    @Subscribe
    public void onReceiveUserShouldUpdatePaymentInfo(PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess event)
    {
        //check if we need to show the payment bill blocker
        if (event.shouldUserUpdatePaymentInfo)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(PaymentBillBlockerDialogFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
            {
                PaymentBillBlockerDialogFragment paymentBillBlockerDialogFragment = new PaymentBillBlockerDialogFragment();
                paymentBillBlockerDialogFragment.show(getSupportFragmentManager(), PaymentBillBlockerDialogFragment.FRAGMENT_TAG);
            }
        }
    }

    @Subscribe
    public void onReceiveCheckTermsSuccess(HandyEvent.ReceiveCheckTermsSuccess event)
    {
        //if the code is null we don't need to to show anything
        if (event.termsDetailsGroup.hasTerms())
        {
            startActivity(new Intent(this, TermsActivity.class));
        }
        else //this is gross and can be resolved after we have a state manager - have to make these requests effectively synchronous because
        // we must guarantee the shouldUpdatePaymentInfo response comes after the terms response, else activity might be launched and obscure the update payment info prompt
        {
            checkIfUserShouldUpdatePaymentInfo();
            checkIfNotificationIsEnabled();
            //have to put this check here due to weird startup flow - after terms are accepted, app switches back to SplashActivity and this activity is relaunched and this function will be called again
        }
    }

    @Subscribe
    public void onReceiveCheckTermsError(HandyEvent.ReceiveCheckTermsError event)
    {
        startActivity(new Intent(this, TermsActivity.class));
    }
}
