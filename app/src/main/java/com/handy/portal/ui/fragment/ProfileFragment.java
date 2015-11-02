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

    private ProviderProfile mProviderProfile;

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

        if (mProviderProfile != null) {
            createProfileView();
        }
        else {
            requestProviderProfile();
        }

        return view;
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
        mProviderProfile = event.providerProfile;
        createProfileView();
    }

    @Subscribe
    public void onReceiveProviderProfileError(HandyEvent.ReceiveProviderProfileError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        fetchErrorText.setText(R.string.error_loading_profile);
        fetchErrorLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(HandyEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        ViewGroup resupplyLayout = (ViewGroup) getActivity().findViewById(R.id.resupply_layout);
        if (resupplyLayout != null)
        {
            profileLayout.removeView(resupplyLayout);
        }

        new ProfileResupplyViewConstructor(getActivity()).create(profileLayout, event.providerProfile);

        showToast(R.string.resupply_kit_on_its_way);
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(HandyEvent.ReceiveSendResupplyKitError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        String message = event.error.getMessage();
        if (message == null)
        {
            message = getContext().getString(R.string.unable_to_process_request);
        }

        showToast(message);
    }

    private void createProfileView()
    {
        profileLayout.removeAllViews();

        new ProfileHeaderViewConstructor(getActivity()).create(profileLayout, mProviderProfile);
        new ProfilePerformanceViewConstructor(getActivity()).create(profileLayout, mProviderProfile.getPerformanceInfo());
        new ProfileReferralViewConstructor(getActivity()).create(profileLayout, mProviderProfile.getReferralInfo());
        new ProfileContactViewConstructor(getActivity()).create(profileLayout, mProviderProfile.getProviderPersonalInfo());
        new ProfileResupplyViewConstructor(getActivity()).create(profileLayout, mProviderProfile);

        fetchErrorLayout.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        profileLayout.setVisibility(View.VISIBLE);
    }
}
