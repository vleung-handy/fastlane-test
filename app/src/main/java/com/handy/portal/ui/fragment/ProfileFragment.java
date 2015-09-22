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
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ProfileFragment extends ActionBarFragment
{

    @InjectView(R.id.provider_name_text)
    TextView providerNameText;

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
        bus.post(new HandyEvent.RequestProviderProfile());
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(HandyEvent.ReceiveProviderProfileSuccess event)
    {
        ProviderProfile providerProfile = event.providerProfile;
        providerNameText.setText(providerProfile.getProviderPersonalInfo().getFirstName());
    }

    @Subscribe
    public void onReceiveProviderProfileError(HandyEvent.ReceiveProviderProfileError event)
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.profile, false);
    }

}
