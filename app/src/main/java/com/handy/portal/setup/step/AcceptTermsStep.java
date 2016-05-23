package com.handy.portal.setup.step;

import android.content.Context;

import com.handy.portal.flow.FlowStep;
import com.handy.portal.model.TermsDetailsGroup;


public class AcceptTermsStep extends FlowStep
{
    private final Context mContext;
    private final TermsDetailsGroup mTermsDetails;

    public AcceptTermsStep(final Context context, final TermsDetailsGroup termsDetails)
    {
        mContext = context;
        mTermsDetails = termsDetails;
    }

    @Override
    public boolean shouldExecute()
    {
        return mTermsDetails != null && mTermsDetails.hasTerms();
    }

    @Override
    public void execute()
    {
        // FIXME: Actually do things
        complete();
    }
}
