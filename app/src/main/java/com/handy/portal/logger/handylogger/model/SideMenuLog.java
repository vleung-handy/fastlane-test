package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class SideMenuLog extends EventLog {
    private static final String EVENT_CONTEXT = "side_menu";

    public SideMenuLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Opened extends SideMenuLog {
        private static final String EVENT_TYPE = "opened";

        public Opened() {
            super(EVENT_TYPE);
        }
    }


    public static class Closed extends SideMenuLog {
        private static final String EVENT_TYPE = "closed";

        public Closed() {
            super(EVENT_TYPE);
        }
    }


    public static class ItemSelected extends SideMenuLog {
        @SerializedName("item")
        private String mItem;

        private static final String EVENT_TYPE = "item_selected";

        public ItemSelected(final String item) {
            super(EVENT_TYPE);
            mItem = item;
        }
    }
}
