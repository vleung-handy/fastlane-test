package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.PaymentEvent;
import com.handy.portal.location.LocationService;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.ui.fragment.PaymentBlockingFragment;
import com.handy.portal.ui.fragment.dialog.NotificationBlockerDialogFragment;
import com.handy.portal.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.handy.portal.util.NotificationUtils;
import com.handy.portal.util.SystemUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    EventLogFactory mEventLogFactory;

    private NotificationBlockerDialogFragment mNotificationBlockerDialogFragment
            = new NotificationBlockerDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setFullScreen();

        startLocationServiceIfNecessary();
    }

    //TODO: move this somewhere else?
    private void startLocationServiceIfNecessary()
    {
        Intent i = new Intent(this, LocationService.class);
        if(mConfigManager.getConfigurationResponse() != null
                && mConfigManager.getConfigurationResponse().isLocationScheduleServiceEnabled())
        {
            //nothing will happen if it's already running
            if(!SystemUtils.isServiceRunning(this, LocationService.class))
            {
                startService(i);
            }
        }
        else
        {
            //nothing will happen if it's not running
            stopService(i);
        }
        //at most one service instance will be running

    }

    @Override
    public void onResume()
    {
        super.onResume();
        mEventLogFactory.createAppOpenLog();

        bus.register(this);
        configManager.prefetch();
        providerManager.prefetch();
        checkForTerms();
        checkIfNotificationIsEnabled();

        bus.post(new LogEvent.SendLogsEvent());
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createAppOpenLog()));
    }

    @Override
    public void onPause()
    {
        bus.post(new LogEvent.SaveLogsEvent());
        bus.unregister(this);
        super.onPause();
    }

    private void checkIfUserShouldUpdatePaymentInfo()
    {
        bus.post(new PaymentEvent.RequestShouldUserUpdatePaymentInfo());
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    public void checkIfNotificationIsEnabled()
    {
        if (NotificationUtils.isNotificationEnabled(this) == NotificationUtils.NOTIFICATION_DISABLED
                && !mNotificationBlockerDialogFragment.isAdded())
        {
            mNotificationBlockerDialogFragment.show(getSupportFragmentManager(),
                    NotificationBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    private void setFullScreen()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Subscribe
    public void onReceiveUserShouldUpdatePaymentInfo(PaymentEvent.ReceiveShouldUserUpdatePaymentInfoSuccess event)
    {
        //check if we need to show the payment bill blocker, we will have either soft and hard blocking (modal and blockingfragment) depending on config params
        if (event.shouldUserUpdatePaymentInfo)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (configManager.getConfigurationResponse() != null &&
                    configManager.getConfigurationResponse().shouldBlockClaimsIfMissingAccountInformation())
            {
                //Tab Navigation Manager should be handling this, but if we got this back too late force a move to blocking fragment
                if (fragmentManager.findFragmentByTag(PaymentBlockingFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
                {
                    bus.post(new HandyEvent.NavigateToTab(MainViewTab.PAYMENT_BLOCKING, new Bundle()));
                }
            }
            else
            {
                //Non-blocking modal
                if (fragmentManager.findFragmentByTag(PaymentBillBlockerDialogFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
                {
                    PaymentBillBlockerDialogFragment paymentBillBlockerDialogFragment = new PaymentBillBlockerDialogFragment();
                    paymentBillBlockerDialogFragment.show(fragmentManager, PaymentBillBlockerDialogFragment.FRAGMENT_TAG);
                }
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
            //have to put this check here due to weird startup flow - after terms are accepted, app switches back to SplashActivity and this activity is relaunched and this function will be called again
        }
    }

    /**
     * TODO: this is temporary to handle case in which config response comes back later
     * ideally, we should probably block the app until the config response is received
     * @param event
     */
    @Subscribe
    public void onReceiveConfigurationResponse(HandyEvent.ReceiveConfigurationSuccess event)
    {
        startLocationServiceIfNecessary();
    }

    @Subscribe
    public void onReceiveCheckTermsError(HandyEvent.ReceiveCheckTermsError event)
    {
        startActivity(new Intent(this, TermsActivity.class));
    }
}
