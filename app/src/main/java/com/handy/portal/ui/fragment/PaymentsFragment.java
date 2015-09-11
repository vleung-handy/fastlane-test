package com.handy.portal.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;

import butterknife.ButterKnife;

public final class PaymentsFragment extends InjectedFragment
{
    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {

        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_payments, container, false);

        ButterKnife.inject(this, view);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) //needed to workaround a bug in android 4.4 that cause webview artifacts to show.
        {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        return view;
    }
}
