package com.handy.portal.consts;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;

public enum BookingActionButtonType
{
    CLAIM(Booking.ActionButtonData.BOOKING_ACTION_NAME_CLAIM, R.string.claim, BookingActionButtonStyle.GREEN),
    REMOVE(Booking.ActionButtonData.BOOKING_ACTION_NAME_REMOVE, R.string.remove_job, BookingActionButtonStyle.RED),
    ON_MY_WAY(Booking.ActionButtonData.BOOKING_ACTION_NAME_ON_MY_WAY, R.string.on_my_way, BookingActionButtonStyle.BLUE),
    CHECK_IN(Booking.ActionButtonData.BOOKING_ACTION_NAME_CHECK_IN, R.string.check_in, BookingActionButtonStyle.CLAIMED_BLUE),
    CHECK_OUT(Booking.ActionButtonData.BOOKING_ACTION_NAME_CHECK_OUT, R.string.check_out, BookingActionButtonStyle.CLAIMED_BLUE),
    ETA(Booking.ActionButtonData.BOOKING_ACTION_NAME_ETA, R.string.update_arrival_time, BookingActionButtonStyle.CLAIMED_BLUE_EMPTY),
    CONTACT_PHONE(Booking.ActionButtonData.BOOKING_ACTION_NAME_CONTACT_PHONE, R.string.call, BookingActionButtonStyle.CONTACT),
    CONTACT_TEXT(Booking.ActionButtonData.BOOKING_ACTION_NAME_CONTACT_TEXT, R.string.text, BookingActionButtonStyle.CONTACT),
    ;

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
