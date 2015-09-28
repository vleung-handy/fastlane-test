package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.ui.fragment.dialog.PaymentBillBlockerDialogFragment;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{
    @Inject
    ProviderManager providerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HandyActionBarTheme);
        setContentView(R.layout.activity_main);
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

    private void checkIfUserShouldUpdatePaymentInfo()
    {
        bus.post(new HandyEvent.RequestShouldUserUpdatePaymentInfo());
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    @Subscribe
    public void onReceiveUserShouldUpdatePaymentInfo(HandyEvent.ReceiveShouldUserUpdatePaymentInfo event)
    {
        //check if we need to show the payment bill blocker
        if(event.shouldUserUpdatePaymentInfo)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if(fragmentManager.findFragmentByTag(PaymentBillBlockerDialogFragment.FRAGMENT_TAG) == null) //only show if there isn't an instance of the fragment showing already
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
        if (event.termsDetailsGroup != null && event.termsDetailsGroup.hasTerms())
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

    @Subscribe
    public void onReceiveCheckTermsError(HandyEvent.ReceiveCheckTermsError event)
    {
        startActivity(new Intent(this, TermsActivity.class));
    }
}
