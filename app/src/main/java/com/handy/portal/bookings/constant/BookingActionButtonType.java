package com.handy.portal.bookings.constant;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;

public enum BookingActionButtonType {
    CLAIM(R.id.claim_button, Booking.Action.ACTION_CLAIM, R.string.claim, BookingActionButtonStyle.GREEN),
    REMOVE(R.id.remove_button, Booking.Action.ACTION_REMOVE, R.string.remove_job, BookingActionButtonStyle.RED_EMPTY),
    ON_MY_WAY(R.id.on_my_way_button, Booking.Action.ACTION_ON_MY_WAY, R.string.on_my_way, BookingActionButtonStyle.BLUE),
    CHECK_IN(R.id.check_in_button, Booking.Action.ACTION_CHECK_IN, R.string.check_in, BookingActionButtonStyle.TEAL),
    CHECK_OUT(R.id.check_out_button, Booking.Action.ACTION_CHECK_OUT, R.string.check_out, BookingActionButtonStyle.TEAL),
    HELP(R.id.i_need_help_button, Booking.Action.ACTION_HELP, R.string.job_support, BookingActionButtonStyle.GREY),
    CONTACT_PHONE(R.id.contact_call_button, Booking.Action.ACTION_CONTACT_PHONE, R.string.call, BookingActionButtonStyle.CONTACT),
    CONTACT_TEXT(R.id.contact_text_button, Booking.Action.ACTION_CONTACT_TEXT, R.string.text, BookingActionButtonStyle.CONTACT),
    RETRACT_NO_SHOW(0, Booking.Action.ACTION_RETRACT_NO_SHOW, 0, null),;

    private int id;
    private String actionName; //must correspond to server's actionName to match up correctly
    private int displayNameId;
    private BookingActionButtonStyle style;

    BookingActionButtonType(int id, String actionName, int displayNameId, BookingActionButtonStyle style) {
        this.id = id;
        this.actionName = actionName;
        this.displayNameId = displayNameId;
        this.style = style;
    }

    public int getId() {
        return id;
    }

    public int getBackgroundDrawableId() {
        return style.getBackgroundDrawableId();
    }

    public String getActionName() {
        return actionName;
    }

    public int getDisplayNameId(Booking booking) {
        if (this == CLAIM) {
            return booking.isRecurring() ? R.string.claim_series : R.string.claim_job;
        }
        return displayNameId;
    }

    public int getLayoutTemplateId() {
        return style.getLayoutTemplateId();
    }

    public int getTextStyleId() {
        return style.getTextStyleId();
    }
}
