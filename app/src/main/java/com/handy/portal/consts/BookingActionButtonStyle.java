package com.handy.portal.consts;

//** KEEP IN SYNC WITH SERVER VALUES **//

import com.handy.portal.R;

public enum BookingActionButtonStyle
{
    GREEN(R.drawable.button_green_round, R.style.Button_Green_Round, R.layout.element_booking_action_button_template),
    RED(R.drawable.button_red_round, R.style.Button_Red_Round, R.layout.element_booking_action_button_template),
    BLUE(R.drawable.button_blue_round, R.style.Button_Blue_Round, R.layout.element_booking_action_button_template),
    CLAIMED_BLUE(R.drawable.button_claimed_blue_round, R.style.Button_ClaimedBlue_Round, R.layout.element_booking_action_button_template),
    CLAIMED_BLUE_EMPTY(R.drawable.button_claimed_blue_empty_round, R.style.Button_ClaimedBlueEmpty_Round, R.layout.element_booking_secondary_action_button_template),
    CONTACT(R.drawable.button_white_round, R.style.Button_White_Round, R.layout.element_booking_contact_action_button_template),
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
