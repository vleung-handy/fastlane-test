package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.PerformanceInfo;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ReferralInfo;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ProfileFragment extends ActionBarFragment
{
    private static final DecimalFormat RATING_FORMAT = new DecimalFormat("0.0");

    @InjectView(R.id.provider_first_name_text)
    TextView providerFirstNameText;
    @InjectView(R.id.provider_last_name_text)
    TextView providerLastNameText;
    @InjectView(R.id.jobs_rating_text)
    TextView jobsRatingText;

    @InjectView(R.id.tier_text)
    TextView tierText;
    @InjectView(R.id.tier_label)
    TextView tierLabel;
    @InjectView(R.id.trailing_rating_text)
    TextView trailingRatingText;
    @InjectView(R.id.trailing_jobs_text)
    TextView trailingJobsText;
    @InjectView(R.id.trailing_rate_text)
    TextView trailingRateText;

    @InjectView(R.id.referral_code_layout)
    ViewGroup referralCodeLayout;
    @InjectView(R.id.section_header_title)
    TextView referralSectionHeaderTitle;

    @InjectView(R.id.referral_code_text)
    TextView referralCodeText;

    @InjectView(R.id.joined_handy_text)
    TextView joinedHandyText;
    @InjectView(R.id.provider_email_text)
    TextView providerEmailText;
    @InjectView(R.id.provider_phone_text)
    TextView providerPhoneText;
    @InjectView(R.id.provider_address_text)
    TextView providerAddressText;

    @InjectView(R.id.resupply_layout)
    ViewGroup resupplyLayout;
    @InjectView(R.id.get_resupply_kit_button)
    Button resupplyButton;

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
        final ProviderProfile providerProfile = event.providerProfile;

        PerformanceInfo performanceInfo = providerProfile.getPerformanceInfo();

        // Header
        ProviderPersonalInfo providerPersonalInfo = providerProfile.getProviderPersonalInfo();
        providerFirstNameText.setText(providerPersonalInfo.getFirstName());
        providerLastNameText.setText(providerPersonalInfo.getLastName());

        String formattedTotalRating = RATING_FORMAT.format(performanceInfo.getTotalRating());
        jobsRatingText.setText(Html.fromHtml(getString(R.string.jobs_and_rating, performanceInfo.getTotalJobsCount(), formattedTotalRating)));

        Date activationDate = providerPersonalInfo.getActivationDate();
        if (activationDate != null)
        {
            joinedHandyText.setText(Html.fromHtml(getString(R.string.joined_handy, DateTimeUtils.formatMonthDateYear(activationDate))));
        }
        else
        {
            joinedHandyText.setVisibility(View.GONE);
        }

        // Tier
        if (performanceInfo.getTier() > 0)
        {
            tierText.setText(getString(R.string.tier_x, performanceInfo.getTier()));
        }
        else
        {
            tierText.setVisibility(View.GONE);
            tierLabel.setVisibility(View.GONE);
        }
        trailingRatingText.setText(RATING_FORMAT.format(performanceInfo.getTrailing28DayRating()));
        trailingJobsText.setText(Integer.toString(performanceInfo.getTrailing28DayJobsCount()));
        trailingRateText.setText(performanceInfo.getRate());

        // Referral
        final ReferralInfo referralInfo = providerProfile.getReferralInfo();
        referralSectionHeaderTitle.setText(getString(R.string.earn_a_bonus, referralInfo.getBonusAmount()));
        referralCodeText.setText(referralInfo.getReferralCode());
        referralCodeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, referralInfo.getReferralLink());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
            }
        });

        // Contact
        providerEmailText.setText(providerPersonalInfo.getEmail());
        providerPhoneText.setText(providerPersonalInfo.getPhone());
        providerAddressText.setText(providerPersonalInfo.getAddress().getStreetAddress() + "\n" + providerPersonalInfo.getAddress().getCityStateZip());

        // Resupply
        ResupplyInfo resupplyInfo = providerProfile.getResupplyInfo();
        if (!resupplyInfo.canRequestSupplies())
        {
            resupplyLayout.setVisibility(View.GONE);
        }
        else
        {
            resupplyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                    bus.post(new HandyEvent.RequestSendResupplyKit());
                }
            });
        }

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

    @Subscribe
    public void onReceiveSendResupplyKitSuccess(HandyEvent.ReceiveSendResupplyKitSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        resupplyLayout.setVisibility(View.GONE);
        showToast(R.string.resupply_kit_on_its_way);
    }

    @Subscribe
    public void onReceiveSendResupplyKitError(HandyEvent.ReceiveSendResupplyKitError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(R.string.unable_to_process_request);
    }
}
