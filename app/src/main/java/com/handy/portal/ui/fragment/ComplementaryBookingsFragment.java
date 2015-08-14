package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ComplementaryBookingsFragment extends Fragment
{
    @InjectView(R.id.loading_overlay)
    View loadingOverlay;
    @InjectView(R.id.complementary_bookings_banner_close_button)
    View closeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_complementary_bookings, container, false);
        ButterKnife.inject(this, view);

        loadingOverlay.setVisibility(View.VISIBLE);
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}
