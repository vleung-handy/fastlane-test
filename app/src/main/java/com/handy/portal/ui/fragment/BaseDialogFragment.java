package com.handy.portal.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.data.DataManager;

import javax.inject.Inject;

public class BaseDialogFragment extends InjectedDialogFragment
{
    boolean canDismiss;

    @Inject
    DataManager dataManager;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        return new Dialog(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getDialog().setCancelable(canDismiss);
        getDialog().setCanceledOnTouchOutside(canDismiss);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(final DialogInterface dialog, final int keyCode,
                                 final KeyEvent event)
            {
                return !canDismiss;
            }
        });

        return view;
    }
}
