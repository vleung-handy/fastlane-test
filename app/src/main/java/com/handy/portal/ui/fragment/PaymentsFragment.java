package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;

import javax.inject.Inject;

import butterknife.ButterKnife;

public final class PaymentsFragment extends PortalWebViewFragment
{
    @Inject
    HandyRetrofitEndpoint endpoint;

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_payments, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_banking:
                String url = endpoint.getBaseUrl() + "/portal/home?goto=profile";
                Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
                openPortalUrl(url);
                return true;
            case R.id.action_email_verification:
                Toast.makeText(getActivity(), "email verified", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
