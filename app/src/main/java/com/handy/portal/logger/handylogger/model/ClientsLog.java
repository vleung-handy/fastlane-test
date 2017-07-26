package com.handy.portal.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ClientsLog extends EventLog {
    private static final String CLIENT_DETAIL = "client_detail";
    private static final String EVENT_CONTEXT = "app";
    private static final String EVENT_TYPE = "navigation";

    @SerializedName("page")
    private String mPage;

    public ClientsLog(String page) {
        super(EVENT_TYPE, EVENT_CONTEXT);
        mPage = page;
    }

    public static class ListShown extends ClientsLog {
        private static final String PAGE_NAME = "client_list";

        public ListShown() {
            super(PAGE_NAME);
        }
    }

    public static class DetailViewShown extends ClientsLog {
        @SerializedName("user_id")
        private String mClientId;

        public DetailViewShown(@NonNull String clientId) {
            super(CLIENT_DETAIL);
            mClientId = clientId;
        }
    }

    public static class SendMessageTapped extends EventLog {
        private static final String PAGE_NAME = "in_app_chat_with_customer_selected";
        @SerializedName("user_id")
        private String mClientId;

        public SendMessageTapped(@NonNull String clientId) {
            super(PAGE_NAME, CLIENT_DETAIL);
            mClientId = clientId;
        }
    }
}
