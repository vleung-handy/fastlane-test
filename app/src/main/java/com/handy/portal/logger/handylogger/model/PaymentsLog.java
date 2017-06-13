package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class PaymentsLog extends EventLog {
    private static final String EVENT_CONTEXT = "payments";

    private PaymentsLog(String eventType) {
        super(eventType, EVENT_CONTEXT);
    }


    public static class PageShown extends PaymentsLog {
        private static final String EVENT_TYPE = "page_shown";

        @SerializedName("cash_out_cta_shown")
        private boolean mCashOutCtaShown;

        @SerializedName("cash_out_cta_enabled")
        private boolean mCashOutCtaEnabled;

        public PageShown(final boolean cashOutCtaShown,
                         final boolean cashOutCtaEnabled) {
            super(EVENT_TYPE);
            mCashOutCtaShown = cashOutCtaShown;
            mCashOutCtaEnabled = cashOutCtaEnabled;
        }
    }


    public static class CashOutEarlyBankHelpSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "cash_out_early_bank_help_selected";

        public CashOutEarlyBankHelpSelected() {
            super(EVENT_TYPE);
        }
    }


    public static class CashOutEarlyPaymentMethodSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "cash_out_early_payment_method_selected";

        public CashOutEarlyPaymentMethodSelected() {
            super(EVENT_TYPE);
        }
    }


    public static class CashOutEarlySelected extends PaymentsLog {
        private static final String EVENT_TYPE = "cash_out_early_selected";

        public CashOutEarlySelected() {
            super(EVENT_TYPE);
        }
    }


    public static class CashOutEarlyConfirmSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "cash_out_early_confirm_selected";

        @SerializedName("cash_out_profit")
        private int mCashOutProfitCents;

        public CashOutEarlyConfirmSelected(int cashOutProfitCents) {
            super(EVENT_TYPE);
            mCashOutProfitCents = cashOutProfitCents;
        }
    }


    //TODO agree on contract
    public static class BatchTransaction extends PaymentsLog {
        private static final String EVENT_SUB_CONTEXT = "batch";

        private BatchTransaction(final String eventType) {
            super(EVENT_SUB_CONTEXT + "_" + eventType);
        }

        public static class SupportDialogSubmitted extends BatchTransaction {
            private static final String EVENT_TYPE = "payment_support_submitted";

            @SerializedName("machine_name")
            private String mPaymentSupportItemMachineName;

            @SerializedName("batch_id")
            private String mBatchId;

            /**
             * text inputted by the user when the “other” option is selected
             * (currently unsupported, so just sending null for now)
             */
            @SerializedName("other_text")
            private String mOtherText;

            public SupportDialogSubmitted(final String batchId,
                                          final String paymentSupportItemMachineName,
                                          final String otherText) {
                super(EVENT_TYPE);
                mBatchId = batchId;
                mPaymentSupportItemMachineName = paymentSupportItemMachineName;
                mOtherText = otherText;
            }
        }


        public static class RequestReviewSubmitted extends BatchTransaction {
            private static final String EVENT_TYPE = "request_review_submitted";

            @SerializedName("machine_name")
            private String mPaymentSupportItemMachineName;

            @SerializedName("batch_id")
            private String mBatchId;

            public RequestReviewSubmitted(final String batchId,
                                          final String paymentSupportItemMachineName) {
                super(EVENT_TYPE);
                mBatchId = batchId;
                mPaymentSupportItemMachineName = paymentSupportItemMachineName;
            }
        }
    }


    public static class BookingTransaction extends PaymentsLog {
        private static final String EVENT_SUB_CONTEXT = "booking";

        private BookingTransaction(final String eventType) {
            super(EVENT_SUB_CONTEXT + "_" + eventType);
        }

        public static class SupportDialogSubmitButtonPressed extends BookingTransaction {
            private static final String EVENT_TYPE = "payment_support_submitted";

            @SerializedName("machine_name")
            private String mPaymentSupportItemMachineName;

            @SerializedName("booking_id")
            private String mBookingId;

            /**
             * text inputted by the user when the “other” option is selected
             * (currently unsupported, so just sending null for now)
             */
            @SerializedName("other_text")
            private String mOtherText;

            public SupportDialogSubmitButtonPressed(final String bookingId,
                                                    final String paymentSupportItemMachineName,
                                                    final String otherText) {
                super(EVENT_TYPE);
                mBookingId = bookingId;
                mPaymentSupportItemMachineName = paymentSupportItemMachineName;
                mOtherText = otherText;
            }
        }
    }


    //in the context of the payment batches screen
    public static class BatchSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "batch_selected";

        @SerializedName("current_week")
        private boolean mCurrentWeek;
        @SerializedName("list_index")
        private int mListIndex;

        public BatchSelected(boolean currentWeek, int listIndex) {
            super(EVENT_TYPE);
            mCurrentWeek = currentWeek;
            mListIndex = listIndex;
        }
    }


    /*
    in context of batch payment screen
    TODO may want to make this extend BatchTransaction log.
    need discussion on how this log is currently being used
     */
    public static class DetailSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "detail_selected";

        @SerializedName("payment_type")
        private String mPaymentType;

        public DetailSelected(String paymentType) {
            super(EVENT_TYPE);
            mPaymentType = paymentType;
        }
    }


    public static class HelpSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "help_selected";

        public HelpSelected() {
            super(EVENT_TYPE);
        }
    }


    //in the context of the payment batches screen
    public static class FeeDetailSelected extends PaymentsLog {
        private static final String EVENT_TYPE = "fee_detail_selected";

        public FeeDetailSelected() {
            super(EVENT_TYPE);
        }
    }

}
