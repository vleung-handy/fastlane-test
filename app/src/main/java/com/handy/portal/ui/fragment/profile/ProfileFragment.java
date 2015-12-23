package com.handy.portal.ui.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.ui.constructor.ProfileContactViewConstructor;
import com.handy.portal.ui.constructor.ProfileHeaderViewConstructor;
import com.handy.portal.ui.constructor.ProfilePerformanceViewConstructor;
import com.handy.portal.ui.constructor.ProfileReferralViewConstructor;
import com.handy.portal.ui.element.profile.ManagementToolsView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends ActionBarFragment
{
    @Bind(R.id.fetch_error_view)
    ViewGroup fetchErrorLayout;

    @Bind(R.id.profile_layout)
    ViewGroup profileLayout;

    @Bind(R.id.fetch_error_text)
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
        ButterKnife.bind(this, view);

        if (mProviderProfile != null)
        {
            createProfileView();
        }
        else
        {
            requestProviderProfile();
        }

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.profile, false);

        // Do we want to reload this on resume?
        if (mProviderProfile != null)
        {
            createProfileView();
        }
        else
        {
            requestProviderProfile();
        }
    }

    @OnClick(R.id.try_again_button)
    public void requestProviderProfile()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProfileEvent.RequestProviderProfile());
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        mProviderProfile = event.providerProfile;
        createProfileView();
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        fetchErrorText.setText(R.string.error_loading_profile);
        fetchErrorLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(ProfileEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        //Do we really need to remove and recreate ManagementToolsView? Or is it handled on resume?
        ViewGroup resupplyLayout = (ViewGroup) getActivity().findViewById(R.id.resupply_layout);
        if (resupplyLayout != null)
        {
            profileLayout.removeView(resupplyLayout);
        }

        profileLayout.addView(new ManagementToolsView(getContext(), mProviderProfile));

        showToast(R.string.resupply_kit_on_its_way);
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(ProfileEvent.ReceiveSendResupplyKitError event)
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
        profileLayout.addView(new ManagementToolsView(getContext(), mProviderProfile));

        fetchErrorLayout.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        profileLayout.setVisibility(View.VISIBLE);
    }
}
