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

import java.util.ArrayList;
import java.util.List;

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
        this.bus.unregister(this);
        super.onPause();
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

    //Each fragment if it requires arguments from the bundles should override this list
    protected List<String> requiredArguments()
    {
        return new ArrayList<String>();
    }

    protected boolean validateRequiredArguments()
    {
        boolean validated = true;

        Bundle suppliedArguments = this.getArguments();
        List<String> requiredArguments = requiredArguments();
        String errorDetails = "";
        for (String requiredArgument : requiredArguments)
        {
            if (!suppliedArguments.containsKey(requiredArgument)
                    || suppliedArguments.getString(requiredArgument) == null)
            {
                validated = false;

                if (!validated)
                {
                    errorDetails += "Missing required argument : " + requiredArgument + "\n";
                }
            }
        }

        try
        {
            if (!validated)
            {
                throw new Exception(errorDetails);
            }
        } catch (Exception e)
        {
            System.err.println(e.toString());
        }

        return validated;
    }

    //Helpers
    protected void showErrorToast(int stringId)
    {
        showErrorToast(getString(stringId));
    }

    protected void showErrorToast(String error)
    {
        showErrorToast(error, Toast.LENGTH_SHORT);
    }

    protected void showErrorToast(int stringId, int length)
    {
        showErrorToast(getString(stringId), length);
    }

    protected void showErrorToast(String error, int length)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), error, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
