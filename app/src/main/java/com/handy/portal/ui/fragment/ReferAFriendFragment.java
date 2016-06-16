package com.handy.portal.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.ProfileEvent;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;
import com.handy.portal.logger.handylogger.model.ReferralLog;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ReferralInfo;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReferAFriendFragment extends ActionBarFragment
{
    @Inject
    Bus mBus;
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.envelope)
    View mEnvelope;
    @Bind(R.id.envelope_shadow)
    View mEnvelopeShadow;
    @Bind(R.id.bling)
    View mBling;

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.referral_code_text)
    TextView mReferralCodeText;

    @Bind(R.id.fetch_error_view)
    ViewGroup fetchErrorLayout;
    @Bind(R.id.fetch_error_text)
    TextView fetchErrorText;

    private View fragmentView;

    private ProviderProfile mProviderProfile;
    private ReferralInfo mReferralInfo;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.REFER_A_FRIEND;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        bus.post(new LogEvent.AddLogEvent(new ProfileLog.ReferralOpen()));
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null)
        {
            fragmentView = inflater.inflate(R.layout.fragment_refer_a_friend, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.refer_a_friend, false);

        populateInfo();
    }

    @OnClick(R.id.envelope)
    public void onEnvelopeClicked()
    {
        mBling.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.sparkle_fade));
    }

    @OnClick(R.id.referral_code_layout)
    public void createReferral()
    {
        mBus.post(new LogEvent.AddLogEvent(new ProfileLog.ReferralSelected()));
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mReferralInfo.getReferralLink());
        sendIntent.setType("text/plain");
        Utils.safeLaunchIntent(Intent.createChooser(sendIntent, getContext().getString(R.string.share_with)), getContext());
        bus.post(new LogEvent.AddLogEvent(new ReferralLog.ReferralCompletedLog()));
    }

    @OnClick(R.id.try_again_button)
    public void requestProviderProfile()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new ProfileEvent.RequestProviderProfile(true));
    }

    @Subscribe
    public void onReceiveProviderProfileSuccess(ProfileEvent.ReceiveProviderProfileSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        mProviderProfile = event.providerProfile;
        mReferralInfo = mProviderProfile.getReferralInfo();

        populateText();
        startAnimations();
    }

    @Subscribe
    public void onReceiveProviderProfileError(ProfileEvent.ReceiveProviderProfileError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        fetchErrorText.setText(R.string.error_loading_profile);
        fetchErrorLayout.setVisibility(View.VISIBLE);
    }

    private void populateInfo()
    {
        mProviderProfile = mProviderManager.getCachedProviderProfile();
        if (mProviderProfile == null || mProviderProfile.getReferralInfo() == null)
        {
            requestProviderProfile();
        }
        else
        {
            mReferralInfo = mProviderProfile.getReferralInfo();
            populateText();
            startAnimations();
        }

        bus.post(new LogEvent.AddLogEvent(new ReferralLog.ReferralOpenLog()));
    }

    private void populateText()
    {
        mTitleText.setText(getContext().getString(R.string.earn_a_reward, mReferralInfo.getBonusAmount()));
        mReferralCodeText.setText(mReferralInfo.getReferralCode());
    }

    private void startAnimations()
    {
        mEnvelope.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.levitate));
        mEnvelopeShadow.startAnimation(
                AnimationUtils.loadAnimation(getActivity(), R.anim.expand_contract));
        mBling.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (isVisible())
                {
                    onEnvelopeClicked();
                }
            }
        }, 1000);
    }
}
