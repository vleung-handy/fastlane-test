package com.handy.portal.flow;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Flow is a wrapper for a collection of {@code FlowStep} objects. Its job is to execute flow steps
 * in order of their addition to the flow object after calling the {@code start()} method.
 *
 * @see com.handy.portal.ui.activity.BaseActivity.SetupHandler
 */
public class Flow
{
    private List<FlowStep> mSteps;
    private OnFlowCompleteListener mOnFlowCompleteListener;
    private Iterator<FlowStep> mStepsIterator;

    public Flow()
    {
        mSteps = new ArrayList<>();
    }

    public Flow addStep(@NonNull final FlowStep step)
    {
        mSteps.add(step);
        step.setFlow(this);
        return this;
    }

    public Flow setOnFlowCompleteListener(
            @NonNull final OnFlowCompleteListener onFlowCompleteListener)
    {
        mOnFlowCompleteListener = onFlowCompleteListener;
        return this;
    }

    public Flow start()
    {
        mStepsIterator = mSteps.iterator();
        goForward();
        return this;
    }

    public void goForward()
    {
        if (mStepsIterator.hasNext())
        {
            final FlowStep nextStep = mStepsIterator.next();
            if (nextStep.shouldExecute())
            {
                nextStep.execute();
            }
            else
            {
                goForward();
            }
        }
        else if (mOnFlowCompleteListener != null)
        {
            mOnFlowCompleteListener.onFlowComplete();
        }
    }

    public interface OnFlowCompleteListener
    {
        void onFlowComplete();
    }
}
