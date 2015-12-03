package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class EventLogBundle
{
    @SerializedName("eventBundleID")
    private String mEventBundleId;
    @SerializedName("events")
    private List<EventLog> mEventLogs;


    public EventLogBundle(String eventBundleId)
    {
        this(eventBundleId, new ArrayList<EventLog>());
    }

    public EventLogBundle(String eventBundleId, List<EventLog> eventLogs)
    {
        mEventBundleId = eventBundleId;
        mEventLogs = eventLogs;
    }

    public void add(EventLog eventLog)
    {
        mEventLogs.add(eventLog);
    }
}
