package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.BookingManager;
import com.handy.portal.core.GoogleService;
import com.handy.portal.core.NavigationManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.ui.widget.ProgressDialog;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment
{
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    @Inject
    BookingManager bookingManager;
    @Inject
    Mixpanel mixpanel;
    @Inject
    DataManager dataManager;
    @Inject
    DataManagerErrorHandler dataManagerErrorHandler;
    @Inject
    NavigationManager navigationManager;
    @Inject
    GoogleService googleService;
    @Inject
    Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.bus.register(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        this.bus.unregister(this);
    }

    @Override
    public final void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public void onStop()
    {
        allowCallbacks = false;
        super.onStop();
    }

    protected void disableInputs()
    {
    }

    protected void enableInputs()
    {
    }

    //Helpers
    protected void showErrorToast(int stringId)
    {
        showErrorToast(getString(stringId));
    }

    protected void showErrorToast(String error)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
