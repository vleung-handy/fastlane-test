package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.event.Event;
import com.squareup.otto.Subscribe;

public class MainActivity extends BaseActivity
{
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
        checkForTerms();
    }

    private void checkForTerms()
    {
        bus.post(new Event.CheckTermsRequestEvent());
    }

    @Subscribe
    public void onCheckTermsResponse(Event.CheckTermsResponseEvent event)
    {
        if (event.termsDetails == null || event.termsDetails.getCode() != null)
        {
            startActivity(new Intent(this, TermsActivity.class));
        }
    }
}
