package com.handy.portal.setup.step;

import android.content.Context;
import android.content.Intent;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.flow.FlowStep;
import com.handy.portal.library.util.Utils;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.ui.activity.TermsActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;


public class AcceptTermsStep extends FlowStep
{
    @Inject
    EventBus mBus;
    private final Context mContext;
    private final TermsDetailsGroup mTermsDetailsGroup;

    public AcceptTermsStep(final Context context, final TermsDetailsGroup termsDetailsGroup)
    {
        Utils.inject(context, this);
        mContext = context;
        mTermsDetailsGroup = termsDetailsGroup;
        mBus.register(this);
    }

    @Override
    public boolean shouldExecute()
    {
        return mTermsDetailsGroup != null && mTermsDetailsGroup.hasTerms();
    }

    @Override
    public void execute()
    {
        final Intent intent = new Intent(mContext, TermsActivity.class);
        intent.putExtra(BundleKeys.TERMS_GROUP, mTermsDetailsGroup);
        intent.putExtra(BundleKeys.FLOW_STEP_ID, getId());
        mContext.startActivity(intent);
    }

    @Subscribe
    public void onStepCompleted(final HandyEvent.StepCompleted event)
    {
        if (event.getStepId() == getId())
        {
            complete();
        }
    }
}
