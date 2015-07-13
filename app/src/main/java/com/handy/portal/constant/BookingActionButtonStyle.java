package com.handy.portal.constant;

//** KEEP IN SYNC WITH SERVER VALUES **//

import com.handy.portal.R;

public enum BookingActionButtonStyle
{
    GREEN(R.drawable.button_booking_action_green, R.style.Button_BookingAction_Green, R.layout.element_booking_action_button_template),
    RED_EMPTY(R.drawable.button_booking_action_red_empty, R.style.Button_BookingAction_Red_Empty, R.layout.element_booking_action_button_template),
    BLUE(R.drawable.button_booking_action_blue, R.style.Button_BookingAction_Blue, R.layout.element_booking_action_button_template),
    TEAL(R.drawable.button_booking_action_teal, R.style.Button_BookingAction_Teal, R.layout.element_booking_action_button_template),
    TEAL_EMPTY(R.drawable.button_booking_action_teal_empty, R.style.Button_BookingAction_Teal_Empty, R.layout.element_booking_secondary_action_button_template),
    CONTACT(R.drawable.button_booking_action_white, R.style.Button_BookingAction_White, R.layout.element_booking_contact_action_button_template),
    ;

    private int backgroundDrawableId;
    private int textStyleId;
    private int layoutTemplateId;

    BookingActionButtonStyle(int backgroundDrawableId, int textStyleId, int layoutTemplateId)
    {
        this.backgroundDrawableId = backgroundDrawableId;
        this.textStyleId = textStyleId;
        this.layoutTemplateId = layoutTemplateId;
    }

    public int getBackgroundDrawableId()
    {
        return backgroundDrawableId;
    }

    public int getLayoutTemplateId()
    {
        return layoutTemplateId;
    }

    public int getTextStyleId()
    {
        return textStyleId;
    }
}
