package com.handy.portal.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class ClientsLog extends EventLog {
    @SerializedName("page")
    private String mPage;

    public ClientsLog(String page) {
        super(EventType.NAVIGATION, EventContext.APP);
        mPage = page;
    }

    public static class ListShown extends ClientsLog {
        private static final String PAGE_NAME = "client_list";

        public ListShown() {
            super(PAGE_NAME);
        }
    }

    public static class DetailViewShown extends ClientsLog {
        private static final String PAGE = EventContext.CLIENT_DETAIL;
        @SerializedName("user_id")
        private String mClientId;

        public DetailViewShown(@NonNull String clientId) {
            super(PAGE);
            mClientId = clientId;
        }
    }

    public static class SendMessageTapped extends EventLog {
        private static final String PAGE_NAME = "in_app_chat_with_customer_selected";
        @SerializedName("user_id")
        private String mClientId;

        public SendMessageTapped(@NonNull String clientId) {
            super(PAGE_NAME, EventContext.CLIENT_DETAIL);
            mClientId = clientId;
        }
    }
}
