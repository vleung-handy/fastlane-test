package com.handy.portal.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.handy.portal.constant.SupportAction;
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

    public static SupportAction getSupportAction(Action action)
    {
        return supportActionMap.get(action.getActionName());
    }

    private static Map<String, SupportAction> supportActionMap = Maps.newHashMap();
    static
    {
        supportActionMap.put(Action.ACTION_NOTIFY_EARLY, SupportAction.NOTIFY_EARLY);
        supportActionMap.put(Action.ACTION_NOTIFY_LATE, SupportAction.NOTIFY_LATE);

        supportActionMap.put(Action.ACTION_REPORT_NO_SHOW, SupportAction.REPORT_NO_SHOW);
        supportActionMap.put(Action.ACTION_ISSUE_HOURS, SupportAction.ISSUE_HOURS);
        supportActionMap.put(Action.ACTION_ISSUE_UNSAFE, SupportAction.ISSUE_UNSAFE);
        supportActionMap.put(Action.ACTION_ISSUE_OTHER, SupportAction.ISSUE_OTHER);
    }
}
