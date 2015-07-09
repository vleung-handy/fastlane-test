package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.data.DataManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedFragment extends android.support.v4.app.Fragment
{
    protected boolean allowCallbacks;
    protected Toast toast;

    @Inject
    DataManager dataManager;
    @Inject
    GoogleManager googleManager;
    @Inject
    Bus bus;
    @Inject
    ConfigManager configManager;

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
            Crashlytics.logException(e);
        }

        return validated;
    }

    //Helpers
    protected void showToast(int stringId)
    {
        showToast(getString(stringId));
    }

    protected void showToast(String message)
    {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected void showToast(int stringId, int length)
    {
        showToast(getString(stringId), length);
    }

    protected void showToast(String message, int length)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
