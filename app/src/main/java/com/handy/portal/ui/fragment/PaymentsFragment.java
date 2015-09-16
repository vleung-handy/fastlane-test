package com.handy.portal.ui.fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_update_banking:
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.REFRESH_TAB));
                return true;
            case R.id.action_email_verification:
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                bus.post(new HandyEvent.SendIncomeVerification());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.SendIncomeVerificationSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.PAYMENTS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.SendIncomeVerificationError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getActivity(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }
}
