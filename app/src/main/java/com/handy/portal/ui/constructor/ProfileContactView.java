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

    private ProviderPersonalInfo mProviderPersonalInfo;

    public ProfileContactView(final Context context, @NonNull final ProviderPersonalInfo providerPersonalInfo)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_profile_contact, this);
        ButterKnife.bind(this);

        mProviderPersonalInfo = providerPersonalInfo;

        mTitleText.setText(R.string.your_contact_information);
        mSubtitleText.setVisibility(View.GONE);

        String noData = getContext().getString(R.string.no_data);

        String email = mProviderPersonalInfo.getEmail();
        mProviderEmailText.setText(email != null ? email : noData);

        String phone = mProviderPersonalInfo.getPhone();
        mProviderPhoneText.setText(phone != null ? phone : noData);

        Address address = mProviderPersonalInfo.getAddress();
        mProviderAddressText.setText(address != null ? (address.getStreetAddress() + "\n" + address.getCityStateZip()) : noData);

        mUpdateButton.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.profile_section_update)
    public void setupUpdateButton()
    {
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE_UPDATE, null, TransitionStyle.SLIDE_UP));
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createEditProfileSelectedLog()));
    }
}
