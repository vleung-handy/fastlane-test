package com.handy.portal.flow;

import android.support.annotation.NonNull;

import java.util.ArrayList;
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
    private int mCurrentStepIndex;
    private OnFlowCompleteListener mOnFlowCompleteListener;

    public Flow()
    {
        mSteps = new ArrayList<>();
        mCurrentStepIndex = -1;
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
        goForward();
        return this;
    }

    public boolean goForward()
    {
        final boolean wentForward = move(1);
        if (!wentForward && mOnFlowCompleteListener != null)
        {
            mOnFlowCompleteListener.onFlowComplete();
        }
        return wentForward;
    }

    public boolean goBack()
    {
        return move(-1);
    }

    private boolean move(final int direction)
    {
        int directionMultiplier = 1;
        int nextPotentialIndex;
        while (isIndexWithinBounds(
                nextPotentialIndex = mCurrentStepIndex + direction * directionMultiplier
        ))
        {
            final FlowStep nextStep = mSteps.get(nextPotentialIndex);
            if (nextStep.shouldExecute())
            {
                mCurrentStepIndex = nextPotentialIndex;
                nextStep.execute();
                return true;
            }
            directionMultiplier++;
        }
        return false;
    }

    private boolean isIndexWithinBounds(final int index)
    {
        return index >= 0 && index < mSteps.size();
    }

    public interface OnFlowCompleteListener
    {
        void onFlowComplete();
    }
}
