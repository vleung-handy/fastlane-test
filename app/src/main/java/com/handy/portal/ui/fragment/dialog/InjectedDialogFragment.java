package com.handy.portal.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.handy.portal.R;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedDialogFragment extends DialogFragment
{
    @Inject
    protected Bus bus;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(STYLE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_slide_down_up_from_top;
        return dialog;
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
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
