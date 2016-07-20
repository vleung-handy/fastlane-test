package com.handy.portal.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.ConnectivityManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.notification.NotificationUtils;
import com.handy.portal.notification.ui.fragment.NotificationBlockerDialogFragment;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.ui.fragment.PaymentBillBlockerDialogFragment;
import com.handy.portal.payments.ui.fragment.PaymentBlockingFragment;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

//TODO: should move some of this logic out of here
public class MainActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    ConnectivityManager mConnectivityManager;

    private NotificationBlockerDialogFragment mNotificationBlockerDialogFragment
            = new NotificationBlockerDialogFragment();

    @Override
    protected boolean shouldTriggerSetup()
    {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setFullScreen();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        //Check config params every time we resume mainactivity, may have changes which result in flow changes on open
        configManager.prefetch();
        providerManager.prefetch();
        checkIfUserShouldUpdatePaymentInfo();
        checkIfNotificationIsEnabled();
        checkConnectivity();
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        System.out.println("onKeyDown(" + keyCode + ", " + event + ")");

        if (keyCode == KeyEvent.KEYCODE_O)
        {
            mConnectivityManager.setHasConnectivity(!mConnectivityManager.hasConnectivity());
            System.out.println("Toggling online mode : new value : " + mConnectivityManager.hasConnectivity());
        }

        if (keyCode == KeyEvent.KEYCODE_R)
        {
            System.out.println("Forcing a refresh of connectivity status");
            mConnectivityManager.requestRefreshConnectivityStatus(this);
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkIfUserShouldUpdatePaymentInfo()
    {
        bus.post(new PaymentEvent.RequestShouldUserUpdatePaymentInfo());
    }

    public void checkIfNotificationIsEnabled()
    {
        if (NotificationUtils.isNotificationEnabled(this) == NotificationUtils.NOTIFICATION_DISABLED
                && !mNotificationBlockerDialogFragment.isAdded())
        {
            FragmentUtils.safeLaunchDialogFragment(mNotificationBlockerDialogFragment, this,
                    NotificationBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    private void checkConnectivity()
    {
        //boolean hasConnectivity = true;
        //mConnectivityManager.setHasConnectivity(hasConnectivity);
        mConnectivityManager.requestRefreshConnectivityStatus(this);
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
                //Page Navigation Manager should be handling this, but if we got this back too late force a move to blocking fragment
                if (fragmentManager.findFragmentByTag(PaymentBlockingFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
                {
                    bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PAYMENT_BLOCKING, new Bundle()));
                }
            }
            else
            {
                //Non-blocking modal
                if (fragmentManager.findFragmentByTag(PaymentBillBlockerDialogFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
                {
                    PaymentBillBlockerDialogFragment paymentBillBlockerDialogFragment = new PaymentBillBlockerDialogFragment();
                    FragmentUtils.safeLaunchDialogFragment(paymentBillBlockerDialogFragment, this, PaymentBillBlockerDialogFragment.FRAGMENT_TAG);
                }
            }
        }
    }
}
