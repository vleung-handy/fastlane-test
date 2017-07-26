package com.handy.portal.logger.handylogger.model;

public interface EventType {
    String CALL_CUSTOMER_SELECTED = "call_customer_selected";
    String CALL_CUSTOMER_FAILED = "call_customer_failed";
    String TEXT_CUSTOMER_SELECTED = "text_customer_selected";
    String TEXT_CUSTOMER_FAILED = "text_customer_failed";
    String IN_APP_CHAT_WITH_CUSTOMER_SELECTED = "in_app_chat_with_customer_selected";
    String IN_APP_CHAT_WITH_CUSTOMER_FAILED = "in_app_chat_with_customer_failed";
    String IN_APP_CHAT_WITH_CUSTOMER_SUCCESS = "in_app_chat_with_customer_success";

    String CLAIM_SUBMITTED = "claim_submitted";
    String CLAIM_SUCCESS = "claim_success";
    String CLAIM_ERROR = "claim_error";

    String CONTINUE_TO_CHECKOUT_SELECTED = "continue_to_checkout_selected";
    String RECEIPT_SHOWN = "receipt_shown";
    String MANUAL_CHECKOUT_SUBMITTED = "manual_checkout_submitted";
    String MANUAL_CHECKOUT_SUCCESS = "manual_checkout_success";
    String MANUAL_CHECKOUT_ERROR = "manual_checkout_error";
    String CUSTOMER_PREFERENCE_SHOWN = "customer_preference_shown";
    String POST_CHECKOUT_SUBMITTED = "post_checkout_submitted";
    String POST_CHECKOUT_SUCCESS = "post_checkout_success";
    String POST_CHECKOUT_ERROR = "post_checkout_error";

    String SET_HOURS_SUBMITTED = "set_hours_submitted";
    String SET_HOURS_SUCCESS = "set_hours_success";
    String SET_HOURS_ERROR = "set_hours_error";
    String SET_TEMPLATE_HOURS_SUBMITTED = "set_template_hours_submitted";
    String SET_TEMPLATE_HOURS_SUCCESS = "set_template_hours_success";
    String SET_TEMPLATE_HOURS_ERROR = "set_template_hours_error";

    String NAVIGATION = "navigation";
}
