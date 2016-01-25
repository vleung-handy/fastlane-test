package com.handy.portal.ui.constructor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.ReferralInfo;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileReferralView extends FrameLayout
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @Bind(R.id.profile_section_header_title_text)
    TextView titleText;
    @Bind(R.id.profile_section_header_subtitle_text)
    TextView subtitleText;
    @Bind(R.id.referral_code_text)
    TextView referralCodeText;

    private ReferralInfo mReferralInfo;

    public ProfileReferralView(final Context context, @NonNull final ReferralInfo referralInfo)
    {
        super(context);

        Utils.inject(context, this);

        inflate(getContext(), R.layout.element_profile_referral, this);
        ButterKnife.bind(this);

        mReferralInfo = referralInfo;

        titleText.setText(getContext().getString(R.string.earn_a_bonus, referralInfo.getBonusAmount()));
        subtitleText.setText(R.string.refer_a_pro);
        referralCodeText.setText(referralInfo.getReferralCode());
    }

    @OnClick(R.id.referral_code_layout)
    public void setupReferral(){
        mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createReferralSelectedLog()));
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mReferralInfo.getReferralLink());
        sendIntent.setType("text/plain");
        Utils.safeLaunchIntent(Intent.createChooser(sendIntent, getContext().getString(R.string.share_with)), getContext());
    }
}
