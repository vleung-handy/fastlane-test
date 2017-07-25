package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class ClientsLog extends EventLog {
    private static final String EVENT_CONTEXT = "clients";

    public ClientsLog(String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ListShown extends ClientsLog {
        private static final String EVENT_TYPE = "list_shown";

        public ListShown() {
            super(EVENT_TYPE);
        }
    }


    public static class DetailViewShown extends ClientsLog {
        private static final String EVENT_TYPE = "detail_view_shown";
        @SerializedName("user_id")
        private String mClientId;

        public DetailViewShown(String clientId) {
            super(EVENT_TYPE);
            mClientId = clientId;
        }
    }
}
