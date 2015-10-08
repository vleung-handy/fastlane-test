package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.ProviderPersonalInfo;

import butterknife.InjectView;

public class ProfileContactViewConstructor extends ViewConstructor<ProviderPersonalInfo>
{
    @InjectView(R.id.profile_section_header_title_text)
    TextView titleText;
    @InjectView(R.id.profile_section_header_subtitle_text)
    TextView subtitleText;

    @InjectView(R.id.provider_email_text)
    TextView providerEmailText;
    @InjectView(R.id.provider_phone_text)
    TextView providerPhoneText;
    @InjectView(R.id.provider_address_text)
    TextView providerAddressText;

    public ProfileContactViewConstructor(@NonNull Context context)
    {
        super(context);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_contact;
    }

    @Override
    protected boolean constructView(ViewGroup container, ProviderPersonalInfo providerPersonalInfo)
    {
        titleText.setText(R.string.your_contact_information);
        subtitleText.setVisibility(View.GONE);

        providerEmailText.setText(providerPersonalInfo.getEmail());
        providerPhoneText.setText(providerPersonalInfo.getPhone());
        providerAddressText.setText(providerPersonalInfo.getAddress().getStreetAddress() + "\n" + providerPersonalInfo.getAddress().getCityStateZip());

        return true;
    }
}
