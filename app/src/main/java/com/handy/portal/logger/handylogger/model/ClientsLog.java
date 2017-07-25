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

        public ListShown() {
            super("client_list");
        }
    }

    public static class DetailViewShown extends ClientsLog {
        public DetailViewShown() {
            super(CLIENT_DETAIL);
        }
    }

    public class DetailViewOpenMapClicked extends EventLog {
        @SerializedName("user_id")
        private String mClientId;

        public DetailViewOpenMapClicked(@NonNull String clientId) {
            super("open_maps_selected", CLIENT_DETAIL);
            mClientId = clientId;
        }
    }
}
