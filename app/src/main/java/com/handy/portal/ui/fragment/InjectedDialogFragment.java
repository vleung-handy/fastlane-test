package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.Toast;

import com.handy.portal.core.BaseApplication;

import butterknife.ButterKnife;

public class InjectedDialogFragment extends DialogFragment
{
    protected boolean allowCallbacks;
    protected Toast toast;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ((BaseApplication) getActivity().getApplication()).inject(this);

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
    }

    @Override
    public final void onDestroyView()
    {
        super.onDestroyView();
        ButterKnife.reset(this);
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
        super.onStop();
        allowCallbacks = false;
    }

    protected void disableInputs()
    {
    }

    protected void enableInputs()
    {
    }
}
