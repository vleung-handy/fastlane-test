package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;

import butterknife.ButterKnife;

public class EditPhotoFragment extends ActionBarFragment
{
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.edit_photo, false);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        bus.register(this);
        setBackButtonEnabled(true);
    }

    @Override
    public void onPause()
    {
//        bus.unregister(this);
        super.onPause();
    }

}
