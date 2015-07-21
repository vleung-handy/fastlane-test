package com.handy.portal.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.handy.portal.constant.SupportActionType;
import com.handy.portal.model.Booking.Action;

import java.util.Map;
import java.util.Set;

public class SupportActionUtils
{
    public static final Set<String> ETA_ACTION_NAMES = Sets.newHashSet(
            Action.ACTION_NOTIFY_EARLY,
            Action.ACTION_NOTIFY_LATE
    );

    public static final Set<String> ISSUE_ACTION_NAMES = Sets.newHashSet(
            Action.ACTION_REPORT_NO_SHOW,
            Action.ACTION_ISSUE_HOURS,
            Action.ACTION_ISSUE_UNSAFE,
            Action.ACTION_ISSUE_OTHER
    );

    public static SupportActionType getSupportActionType(Action action)
    {
        return supportActionTypeMap.get(action.getActionName());
    }

    private static Map<String, SupportActionType> supportActionTypeMap = Maps.newHashMap();
    static
    {
        supportActionTypeMap.put(Action.ACTION_NOTIFY_EARLY, SupportActionType.NOTIFY_EARLY);
        supportActionTypeMap.put(Action.ACTION_NOTIFY_LATE, SupportActionType.NOTIFY_LATE);

        supportActionTypeMap.put(Action.ACTION_REPORT_NO_SHOW, SupportActionType.REPORT_NO_SHOW);
        supportActionTypeMap.put(Action.ACTION_ISSUE_HOURS, SupportActionType.ISSUE_HOURS);
        supportActionTypeMap.put(Action.ACTION_ISSUE_UNSAFE, SupportActionType.ISSUE_UNSAFE);
        supportActionTypeMap.put(Action.ACTION_ISSUE_OTHER, SupportActionType.ISSUE_OTHER);
    }
}
