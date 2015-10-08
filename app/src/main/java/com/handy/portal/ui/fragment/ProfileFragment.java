package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.ui.constructor.ProfileContactViewConstructor;
import com.handy.portal.ui.constructor.ProfileHeaderViewConstructor;
import com.handy.portal.ui.constructor.ProfilePerformanceViewConstructor;
import com.handy.portal.ui.constructor.ProfileReferralViewConstructor;
import com.handy.portal.ui.constructor.ProfileResupplyViewConstructor;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ProfileFragment extends ActionBarFragment
{
    @InjectView(R.id.fetch_error_view)
    ViewGroup fetchErrorLayout;
    @InjectView(R.id.profile_layout)
    ViewGroup profileLayout;

    @InjectView(R.id.fetch_error_text)
    TextView fetchErrorText;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.PROFILE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestProviderProfile();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.profile, false);
    }

    @OnClick(R.id.try_again_button)
    public void requestProviderProfile()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new HandyEvent.RequestProviderProfile());
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(HandyEvent.ReceiveProviderProfileSuccess event)
    {
        profileLayout.removeAllViews();

        ProviderProfile providerProfile = event.providerProfile;

        new ProfileHeaderViewConstructor(getActivity()).create(profileLayout, providerProfile);
        new ProfilePerformanceViewConstructor(getActivity()).create(profileLayout, providerProfile.getPerformanceInfo());
        new ProfileReferralViewConstructor(getActivity()).create(profileLayout, providerProfile.getReferralInfo());
        new ProfileContactViewConstructor(getActivity()).create(profileLayout, providerProfile.getProviderPersonalInfo());
        new ProfileResupplyViewConstructor(getActivity()).create(profileLayout, providerProfile.getResupplyInfo());

        fetchErrorLayout.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        profileLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onReceiveProviderProfileError(HandyEvent.ReceiveProviderProfileError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        fetchErrorText.setText(R.string.error_loading_profile);
        fetchErrorLayout.setVisibility(View.VISIBLE);
    }
}
