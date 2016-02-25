package com.handy.portal.ui.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends ActionBarFragment
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @Bind(R.id.profile_section_header_title_text)
    TextView mTitleText;
    @Bind(R.id.profile_section_header_subtitle_text)
    TextView mSubtitleText;
    @Bind(R.id.profile_section_update)
    TextView mUpdateButton;
    @Bind(R.id.provider_email_text)
    TextView mProviderEmailText;
    @Bind(R.id.provider_phone_text)
    TextView mProviderPhoneText;
    @Bind(R.id.provider_address_text)
    TextView mProviderAddressText;

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
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setActionBar(R.string.profile, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setBackButtonEnabled(true);

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

    @OnClick(R.id.try_again_button)
    public void requestProviderProfile()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProfileEvent.RequestProviderProfile());
    }

    @OnClick(R.id.profile_section_update)
    public void setupUpdateButton()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE_UPDATE, null, TransitionStyle.SLIDE_UP));
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createEditProfileSelectedLog()));
    }

    private void createProfileView()
    {
        ProviderPersonalInfo providerPersonalInfo = mProviderProfile.getProviderPersonalInfo();

        mTitleText.setText(R.string.your_contact_information);
        mSubtitleText.setVisibility(View.GONE);

        String noData = getContext().getString(R.string.no_data);

        String email = providerPersonalInfo.getEmail();
        mProviderEmailText.setText(email != null ? email : noData);

        String phone = providerPersonalInfo.getPhone();
        mProviderPhoneText.setText(phone != null ? phone : noData);

        Address address = providerPersonalInfo.getAddress();
        mProviderAddressText.setText(address != null ? (address.getStreetAddress() + "\n" + address.getCityStateZip()) : noData);

        mUpdateButton.setVisibility(View.VISIBLE);

        fetchErrorLayout.setVisibility(View.GONE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        profileLayout.setVisibility(View.VISIBLE);
    }
}
