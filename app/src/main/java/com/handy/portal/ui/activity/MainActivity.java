package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{
    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
        configManager.init();
        checkForTerms();
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    @Subscribe
    public void onReceiveCheckTermsSuccess(HandyEvent.ReceiveCheckTermsSuccess event)
    {
        //if the code is null we don't need to to show anything
        if (event.termsDetails.getCode() != null)
        {
            startActivity(new Intent(this, TermsActivity.class));
        }
    }

    @Subscribe
    public void onReceiveCheckTermsError(HandyEvent.ReceiveCheckTermsError event)
    {
        startActivity(new Intent(this, TermsActivity.class));
    }
}
