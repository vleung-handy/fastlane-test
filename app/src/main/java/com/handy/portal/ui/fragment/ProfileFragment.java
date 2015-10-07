package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ProfileFragment extends ActionBarFragment
{
    @InjectView(R.id.provider_first_name_text)
    TextView providerFirstNameText;
    @InjectView(R.id.provider_last_name_text)
    TextView providerLastNameText;
    @InjectView(R.id.provider_email_text)
    TextView providerEmailText;
    @InjectView(R.id.provider_phone_text)
    TextView providerPhoneText;
    @InjectView(R.id.provider_address_text)
    TextView providerAddressText;

    @InjectView(R.id.referral_code_text)
    TextView referralCodeText;
    @InjectView(R.id.referral_code_layout)
    ViewGroup referralCodeLayout;
    @InjectView(R.id.joined_handy_text)
    TextView joinedHandyText;

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
        final ProviderProfile providerProfile = event.providerProfile;

        // Header
        ProviderPersonalInfo providerPersonalInfo = providerProfile.getProviderPersonalInfo();
        providerFirstNameText.setText(providerPersonalInfo.getFirstName());
        providerLastNameText.setText(providerPersonalInfo.getLastName());
        providerEmailText.setText(providerPersonalInfo.getEmail());
        providerPhoneText.setText(providerPersonalInfo.getPhone());
        providerAddressText.setText(providerPersonalInfo.getAddress().getStreetAddress() + "\n" + providerPersonalInfo.getAddress().getCityStateZip());

        Date activationDate = providerPersonalInfo.getActivationDate();
        if (activationDate != null)
        {
            joinedHandyText.setText(Html.fromHtml(getString(R.string.joined_handy, DateTimeUtils.formatMonthDateYear(activationDate))));
        }
        else
        {
            joinedHandyText.setVisibility(View.GONE);
        }

        // Referral
        referralCodeText.setText(providerProfile.getReferralInfo().getReferralCode());
        referralCodeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, providerProfile.getReferralInfo().getReferralLink());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
            }
        });
        // TODO: make bonus amount and bonus condition server-driven
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
