package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedDialogFragment extends DialogFragment
{
    @Inject
    Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
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
    public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }
}
