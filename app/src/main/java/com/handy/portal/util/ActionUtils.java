package com.handy.portal.util;

import com.google.common.collect.Sets;
import com.handy.portal.R;
import com.handy.portal.model.Booking.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ActionUtils
{
    private static Map<String, Integer> actionStringIdMap = new HashMap<>();
    static
    {
        actionStringIdMap.put(Action.ACTION_REPORT_NO_SHOW, R.string.customer_not_home);
        actionStringIdMap.put(Action.ACTION_ISSUE_HOURS, R.string.add_remove_hours);
        actionStringIdMap.put(Action.ACTION_ISSUE_UNSAFE, R.string.i_feel_unsafe);
        actionStringIdMap.put(Action.ACTION_ISSUE_OTHER, R.string.different_issue);
    }

    private static Map<String, Integer> actionDrawableIdMap = new HashMap<>();
    static
    {
        actionDrawableIdMap.put(Action.ACTION_REPORT_NO_SHOW, R.drawable.ic_details_trash);
        actionDrawableIdMap.put(Action.ACTION_ISSUE_HOURS, R.drawable.ic_details_trash);
        actionDrawableIdMap.put(Action.ACTION_ISSUE_UNSAFE, R.drawable.ic_details_trash);
        actionDrawableIdMap.put(Action.ACTION_ISSUE_OTHER, R.drawable.ic_details_trash);
    }

    public static final Set<String> SUPPORT_ACTION_NAMES = Sets.newHashSet(
            Action.ACTION_REPORT_NO_SHOW,
            Action.ACTION_ISSUE_HOURS,
            Action.ACTION_ISSUE_UNSAFE,
            Action.ACTION_ISSUE_OTHER
    );

    public static int getActionText(String actionName)
    {
        return actionStringIdMap.get(actionName);
    }

    public static int getActionIcon(String actionName)
    {
        return actionDrawableIdMap.get(actionName);
    }
}
