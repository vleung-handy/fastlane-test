package com.handy.portal.flow;

public abstract class FlowStep
{
    private Flow mFlow;

    public abstract boolean shouldExecute();

    public abstract void execute();

    void setFlow(final Flow flow)
    {
        mFlow = flow;
    }

    public void complete()
    {
        mFlow.goForward();
    }
}
