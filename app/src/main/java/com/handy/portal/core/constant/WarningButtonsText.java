package com.handy.portal.core.constant;

import com.handy.portal.R;
import com.handy.portal.bookings.constant.BookingActionButtonType;

public enum WarningButtonsText
{
    DEFAULT(R.string.are_you_sure, R.string.confirm, R.string.cancel),
    REMOVE_JOB(R.string.are_you_sure, R.string.remove_job, R.string.back),
    REMOVE_NO_SHOW(R.string.remove_customer_no_show_question, R.string.remove_customer_no_show, R.string.back),
    ;

    private final int positiveStringId;
    private final int negativeStringId;
    private final int titleStringId;

    WarningButtonsText(int titleStringId, int positiveStringId, int negativeStringId)
    {
        this.titleStringId = titleStringId;
        this.positiveStringId = positiveStringId;
        this.negativeStringId = negativeStringId;
    }

    public int getTitleStringId()
    {
        return titleStringId;
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
            case RETRACT_NO_SHOW:
                return REMOVE_NO_SHOW;
            default:
                return DEFAULT;
        }

    }
}
