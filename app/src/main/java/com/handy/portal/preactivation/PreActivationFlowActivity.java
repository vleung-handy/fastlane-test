package com.handy.portal.preactivation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.onboarding.SuppliesInfo;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.MainActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PreActivationFlowActivity extends BaseActivity
{
    public SuppliesInfo mSuppliesInfo;
    private List<Booking> mPendingBookings;

    @Override
    protected boolean shouldTriggerSetup()
    {
        return true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mSuppliesInfo = (SuppliesInfo) getIntent()
                .getSerializableExtra(BundleKeys.ONBOARDING_SUPPLIES);
        setContentView(R.layout.activity_pre_activation_flow);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        mPendingBookings = new ArrayList<>();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                goToFirstStep();
            }
        }, 5000);
        bus.register(this);
        final Calendar c = Calendar.getInstance();
        final ArrayList<Date> dates = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            c.add(Calendar.DATE, i);
            dates.add(c.getTime());
        }
        bus.post(new HandyEvent.RequestAvailableBookings(dates, true));
    }

    @Override
    protected void onDestroy()
    {
        bus.unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void z(HandyEvent.ReceiveAvailableBookingsSuccess event)
    {
        if (mPendingBookings.size() < 3)
        {
            mPendingBookings.addAll(event.bookingsWrapper.getBookings());
        }
    }

    public void next(@NonNull final PreActivationFlowFragment fragment,
                     final boolean allowBackNavigation)
    {
        final FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        if (allowBackNavigation)
        {
            Bundle arguments = fragment.getArguments();
            if (arguments == null)
            {
                arguments = new Bundle();
            }
            arguments.putBoolean(BundleKeys.ALLOW_BACK_NAVIGATION, true);
            fragment.setArguments(arguments);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public void terminate()
    {
        if (!isFinishing())
        {
            final Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void goToFirstStep()
    {
        next(OnboardingStatusFragment.newInstance(), false);
    }

    @NonNull
    public List<Booking> getPendingBookings()
    {
        return mPendingBookings;
    }

    public void setPendingBookings(@NonNull final List<Booking> pendingBookings)
    {
        mPendingBookings = pendingBookings;
    }
}
