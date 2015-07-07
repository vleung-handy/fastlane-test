package com.handy.portal.constant;

import com.handy.portal.R;

public enum WarningButtonsText
{
    DEFAULT(R.string.confirm, R.string.cancel),
    REMOVE_JOB(R.string.remove_job, R.string.back);

    private final int positiveStringId;
    private final int negativeStringId;

    WarningButtonsText(int positiveStringId, int negativeStringId)
    {
        this.positiveStringId = positiveStringId;
        this.negativeStringId = negativeStringId;
    }

    public int getPositiveStringId()
    {
        return positiveStringId;
    }

    public int getNegativeStringId()
    {
        return negativeStringId;
    }

    public static WarningButtonsText forAction(BookingActionButtonType actionButtonType)
    {
        switch (actionButtonType)
        {
            case REMOVE:
                return REMOVE_JOB;
            default:
                return DEFAULT;
        }

    }
}
