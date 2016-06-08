package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;


public abstract class DashboardTiersLog extends EventLog
{
    private static final String EVENT_CONTEXT = "tiers";

    public DashboardTiersLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class TiersCardViewedLog extends DashboardTiersLog
    {
        private static final String EVENT_TYPE = "tier_card_viewed";

        @SerializedName("region")
        protected String mRegionName;
        @SerializedName("service")
        protected String mService;

        public TiersCardViewedLog(final String regionName, final String service)
        {
            super(EVENT_TYPE);
            mRegionName = regionName;
            mService = service;
        }
    }
}
