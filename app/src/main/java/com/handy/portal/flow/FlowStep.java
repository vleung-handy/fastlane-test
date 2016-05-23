package com.handy.portal.flow;

public abstract class FlowStep implements FlowResolver
{
    private Flow mFlow;

    public abstract boolean shouldExecute();

    public abstract void execute();

    void setFlow(final Flow flow)
    {
        mFlow = flow;
    }

    protected int getId()
    {
        return System.identityHashCode(this);
    }

    @Override
    public void complete()
    {
        mFlow.goForward();
    }
}
