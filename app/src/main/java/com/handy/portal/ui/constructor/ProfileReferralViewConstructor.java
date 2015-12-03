package com.handy.portal.ui.constructor;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.ReferralInfo;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.InjectView;

public class ProfileReferralViewConstructor extends ViewConstructor<ReferralInfo>
{
    @Inject
    Bus mBus;
    @Inject
    EventLogFactory mEventLogFactory;

    @InjectView(R.id.profile_section_header_title_text)
    TextView titleText;
    @InjectView(R.id.profile_section_header_subtitle_text)
    TextView subtitleText;
    @InjectView(R.id.referral_code_layout)
    ViewGroup referralCodeLayout;
    @InjectView(R.id.referral_code_text)
    TextView referralCodeText;

    public ProfileReferralViewConstructor(@NonNull Context context)
    {
        super(context);
        Utils.inject(context, this);
    }

    @Override
    protected int getLayoutResourceId()
    {
        return R.layout.element_profile_referral;
    }

    @Override
    protected boolean constructView(ViewGroup container, final ReferralInfo referralInfo)
    {
        titleText.setText(getContext().getString(R.string.earn_a_bonus, referralInfo.getBonusAmount()));
        subtitleText.setText(R.string.refer_a_pro);
        referralCodeText.setText(referralInfo.getReferralCode());
        referralCodeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mBus.post(new LogEvent.AddLogEvent(mEventLogFactory.createReferralSelectedLog()));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, referralInfo.getReferralLink());
                sendIntent.setType("text/plain");
                Utils.safeLaunchIntent(Intent.createChooser(sendIntent, getContext().getString(R.string.share_with)), getContext());
            }
        });

        return true;
    }
}
