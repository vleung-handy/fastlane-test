package com.handy.portal.ui.constructor;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.Address;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileContactView extends FrameLayout
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @Bind(R.id.profile_section_header_title_text)
    TextView titleText;
    @Bind(R.id.profile_section_header_subtitle_text)
    TextView subtitleText;
    @Bind(R.id.profile_section_update)
    TextView mUpdateButton;
    @Bind(R.id.provider_email_text)
    TextView providerEmailText;
    @Bind(R.id.provider_phone_text)
    TextView providerPhoneText;
    @Bind(R.id.provider_address_text)
    TextView providerAddressText;

    private ProviderPersonalInfo mProviderPersonalInfo;

    public ProfileContactView(final Context context, @NonNull final ProviderPersonalInfo providerPersonalInfo)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_profile_contact, this);
        ButterKnife.bind(this);

        mProviderPersonalInfo = providerPersonalInfo;

        titleText.setText(R.string.your_contact_information);
        subtitleText.setVisibility(View.GONE);

        String noData = getContext().getString(R.string.no_data);

        String email = mProviderPersonalInfo.getEmail();
        providerEmailText.setText(email != null ? email : noData);

        String phone = mProviderPersonalInfo.getPhone();
        providerPhoneText.setText(phone != null ? phone : noData);

        Address address = mProviderPersonalInfo.getAddress();
        providerAddressText.setText(address != null ? (address.getStreetAddress() + "\n" + address.getCityStateZip()) : noData);

        mUpdateButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.profile_section_update)
    public void setupUpdateButton()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE_UPDATE, null, TransitionStyle.SLIDE_UP));
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createEditProfileSelectedLog()));
    }
}
