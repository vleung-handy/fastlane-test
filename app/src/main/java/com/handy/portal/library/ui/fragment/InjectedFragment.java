package com.handy.portal.library.ui.fragment;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class InjectedFragment extends android.support.v4.app.Fragment
{
    protected Toast toast;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected ConfigManager configManager;

    @Inject
    protected GoogleManager googleManager;
    @Inject
    protected EventBus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);
    }

    /**
     * should be called by tests only
     *
     * @return
     */
    @VisibleForTesting
    public EventBus getBus()
    {
        return bus;
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
        try
        {
             /*
                 on mostly Samsung Android 5.0 devices (responsible for ~97% of crashes here),
                 Activity.onPause() can be called without Activity.onResume()
                 so unregistering the EventBus here can cause an exception
              */
            bus.unregister(this);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e); //want more info for now
        }
        super.onPause();
    }

    @Subscribe
    public void emptyMethod(HandyEvent event)
    {

    }

    //Each fragment if it requires arguments from the bundles should override this list
    protected List<String> requiredArguments()
    {
        return new ArrayList<>();
    }

    protected boolean validateRequiredArguments()
    {
        boolean validated = true;

        Bundle suppliedArguments = this.getArguments();

        if (suppliedArguments == null)
        {
            return requiredArguments().size() == 0;
        }

        List<String> requiredArguments = requiredArguments();
        String errorDetails = "";
        for (String requiredArgument : requiredArguments)
        {
            //TODO: Is there a way we can validate without knowing the type in advance?
            if (!suppliedArguments.containsKey(requiredArgument))
            {
                validated = false;
                errorDetails += "Missing required argument : " + requiredArgument + "\n";
            }
        }

        try
        {
            if (!validated)
            {
                throw new Exception(errorDetails);
            }
        }
        catch (Exception e)
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
        showToast(message, length, Gravity.CENTER);
    }

    protected void showToast(int stringId, int length, int gravity)
    {
        showToast(getString(stringId), length, gravity);
    }

    protected void showToast(String message, int length, int gravity)
    {
        toast = Toast.makeText(getActivity().getApplicationContext(), message, length);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }
}
