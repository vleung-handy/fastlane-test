package com.handy.portal.constant;

import com.handy.portal.R;
import com.handy.portal.model.Booking;

public enum BookingActionButtonType
{
    CLAIM(Booking.Action.ACTION_CLAIM, R.string.claim, BookingActionButtonStyle.GREEN),
    REMOVE(Booking.Action.ACTION_REMOVE, R.string.remove_job, BookingActionButtonStyle.RED_EMPTY),
    ON_MY_WAY(Booking.Action.ACTION_ON_MY_WAY, R.string.on_my_way, BookingActionButtonStyle.BLUE),
    CHECK_IN(Booking.Action.ACTION_CHECK_IN, R.string.check_in, BookingActionButtonStyle.TEAL),
    CHECK_OUT(Booking.Action.ACTION_CHECK_OUT, R.string.check_out, BookingActionButtonStyle.TEAL),
    HELP(Booking.Action.ACTION_HELP, R.string.i_need_help, BookingActionButtonStyle.TEAL_EMPTY),
    CONTACT_PHONE(Booking.Action.ACTION_CONTACT_PHONE, R.string.call, BookingActionButtonStyle.CONTACT),
    CONTACT_TEXT(Booking.Action.ACTION_CONTACT_TEXT, R.string.text, BookingActionButtonStyle.CONTACT),
    RETRACT_NO_SHOW(Booking.Action.ACTION_RETRACT_NO_SHOW, R.string.customer_no_show_reported, BookingActionButtonStyle.RETRACT);

    private String actionName; //must correspond to server's actionName to match up correctly
    private int displayNameId;
    private BookingActionButtonStyle style;

    BookingActionButtonType(String actionName, int displayNameId, BookingActionButtonStyle style)
    {
        this.actionName = actionName;
        this.displayNameId = displayNameId;
        this.style = style;
    }

    public int getBackgroundDrawableId()
    {
        return style.getBackgroundDrawableId();
    }

    public String getActionName()
    {
        return actionName;
    }

    public int getDisplayNameId(Booking booking)
    {
        if (this == CLAIM)
        {
            return booking.isRecurring() ? R.string.claim_series : R.string.claim_job;
        }
        return displayNameId;
    }

    public int getLayoutTemplateId()
    {
        return style.getLayoutTemplateId();
    }

    public int getTextStyleId()
    {
        return style.getTextStyleId();
    }
}
