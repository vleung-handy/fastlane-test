package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.event.Event;
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
        bus.post(new Event.CheckTermsRequestEvent());
    }

    @Subscribe
    public void onCheckTermsResponse(Event.CheckTermsResponseEvent event)
    {
        if (event.termsDetails.getCode() != null)
        {
            startActivity(new Intent(this, TermsActivity.class));
        }
    }

    @Subscribe
    public void onCheckTermsError(Event.CheckTermsErrorEvent event)
    {
        startActivity(new Intent(this, TermsActivity.class));
    }
}
