package com.handy.portal.logger.handylogger.model;

public class OnboardingSuppliesLog extends EventLog
{
    private static final String EVENT_CONTEXT = "onboarding_supplies";


    public static class Types
    {
        public static final String LANDING_SCREEN_SHOWN = "landing_screen_shown";
        public static final String PRODUCTS_LIST_SHOWN = "products_list_shown";
        public static final String PURCHASE_SUPPLIES_SELECTED = "purchase_supplies_selected";
        public static final String DECLINE_SUPPLIES_SELECTED = "decline_supplies_selected";
        public static final String DECLINES_SUPPLIES_CONFIRMED = "decline_supplies_confirmed";
        public static final String PAYMENT_SCREEN_SHOWN = "payment_screen_shown";
        public static final String CONTINUE_TO_CONFIRMATION_SELECTED = "continue_to_confirmation_selected";
        public static final String CONFIRMATION_SCREEN_SHOWN = "confirmation_screen_shown";
        public static final String EDIT_ADDRESS_SHOWN = "edit_address_shown";
        public static final String CONFIRM_PURCHASE_SELECTED = "confirm_purchase_selected";
    }


    public enum ServerTypes
    {
        REQUEST_SUPPLIES,
        GET_STRIPE_TOKEN,
        UPDATE_CREDIT_CARD,
        UPDATE_ADDRESS,;

        private static final String SUFFIX_SUBMITTED = "_submitted";
        private static final String SUFFIX_SUCCESS = "_success";
        private static final String SUFFIX_ERROR = "_error";

        public String submitted()
        {
            return this.toString().toUpperCase() + SUFFIX_SUBMITTED;
        }

        public String success()
        {
            return this.toString().toUpperCase() + SUFFIX_SUCCESS;
        }

        public String error()
        {
            return this.toString().toUpperCase() + SUFFIX_ERROR;
        }
    }

    public OnboardingSuppliesLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }
}
