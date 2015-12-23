package com.handy.portal.ui.element.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManagementToolsView extends FrameLayout
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @Bind(R.id.profile_section_header_title_text)
    TextView mTitleText;
    @Bind(R.id.profile_section_header_subtitle_text)
    TextView mSubTitleText;
    @Bind(R.id.provider_get_resupply_kit_text)
    TextView mResupplyText;
    @Bind(R.id.provider_get_resupply_kit_help_text)
    TextView mResupplyHelpText;
    @Bind(R.id.provider_get_resupply_kit)
    ViewGroup mResupply;

    private ProviderProfile mProviderProfile;

    public ManagementToolsView(final Context context, @NonNull final ProviderProfile providerProfile)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_profile_management_tools, this);
        ButterKnife.bind(this);

        mProviderProfile = providerProfile;

        mTitleText.setText(R.string.management_tools);
        mSubTitleText.setVisibility(GONE);
        mResupply.setClickable(false);

        final ResupplyInfo resupplyInfo = mProviderProfile.getResupplyInfo();
        if (resupplyInfo != null && resupplyInfo.providerCanRequestSupplies())
        {
            if (resupplyInfo.providerCanRequestSuppliesNow())
            {
                mResupply.setClickable(true);
                mResupplyText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                mResupplyHelpText.setVisibility(GONE);
            }
            else
            {
                mResupply.setClickable(false);
                mResupplyText.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.subtitle_grey));
                mResupplyHelpText.setText(resupplyInfo.getHelperText());
                mResupplyHelpText.setVisibility(VISIBLE);
            }
        }
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mBus.register(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        mBus.unregister(this);
        super.onDetachedFromWindow();
    }

    @OnClick(R.id.provider_email_income_verification)
    public void emailVerification()
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mBus.post(new HandyEvent.RequestSendIncomeVerification());
    }

    @OnClick(R.id.provider_get_resupply_kit)
    public void getResupplyKit()
    {
        mBus.post(new LogEvent.AddLogEvent(
                mEventLogFactory.createResupplyKitSelectedLog()));
        final Bundle args = new Bundle();
        args.putSerializable(BundleKeys.PROVIDER_PROFILE, mProviderProfile);
        mBus.post(new HandyEvent.NavigateToTab(
                MainViewTab.REQUEST_SUPPLIES, args, TransitionStyle.SLIDE_UP));
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.ReceiveSendIncomeVerificationSuccess event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        // There should be a simpler way to just show the overlay without navigate back to itself
        mBus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.ReceiveSendIncomeVerificationError event)
    {
        mBus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getContext(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }

}
