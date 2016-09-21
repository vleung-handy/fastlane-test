package com.handy.portal.onboarding;


import com.handy.portal.flow.FlowStep;
import com.handy.portal.onboarding.model.subflow.SubflowType;

public class OnboardingFlowStep extends FlowStep
{
    private SubflowLauncher mSubflowLauncher;
    private SubflowType mType;

    public OnboardingFlowStep(final SubflowLauncher subflowLauncher, final SubflowType type)
    {
        mSubflowLauncher = subflowLauncher;
        mType = type;
    }

    @Override
    public boolean shouldExecute()
    {
        return true;
    }

    @Override
    public void execute()
    {
        mSubflowLauncher.launchSubflow(mType);
    }
}
