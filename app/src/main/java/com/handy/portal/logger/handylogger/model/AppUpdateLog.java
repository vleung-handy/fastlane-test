package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class AppUpdateLog extends EventLog {
    private static final String EVENT_CONTEXT = "app_update";

    @SerializedName("download_url")
    private String mApkDownloadUrl;

    public AppUpdateLog(String eventType, String apkDownloadUrl) {
        super(eventType, EVENT_CONTEXT);
        mApkDownloadUrl = apkDownloadUrl;
    }

    public static class Shown extends AppUpdateLog {
        private static final String EVENT_TYPE = "shown";

        public Shown(final String apkDownloadUrl) {
            super(EVENT_TYPE, apkDownloadUrl);
        }
    }


    public static class Skipped extends AppUpdateLog {
        private static final String EVENT_TYPE = "skipped";

        public Skipped(final String apkDownloadUrl) {
            super(EVENT_TYPE, apkDownloadUrl);
        }
    }


    public static class Started extends AppUpdateLog {
        private static final String EVENT_TYPE = "started";

        public Started(final String apkDownloadUrl) {
            super(EVENT_TYPE, apkDownloadUrl);
        }
    }


    public static class Failed extends AppUpdateLog {
        private static final String EVENT_TYPE = "failed";

        public Failed(final String apkDownloadUrl) {
            super(EVENT_TYPE, apkDownloadUrl);
        }
    }
}
