package com.handy.portal.flow;

public abstract class FlowStep
{
    private Forwardable mForwardable;

    public abstract boolean shouldExecute();

    public abstract void execute();

    void setFlow(final Forwardable forwardable)
    {
        mForwardable = forwardable;
    }

    protected int getId()
    {
        return System.identityHashCode(this);
    }

    public void complete()
    {
        mForwardable.goForward();
    }
}
