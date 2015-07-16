package com.handy.portal.constant;

import com.handy.portal.R;

public enum SupportActionType
{
    NOTIFY_EARLY(R.string.will_be_early, R.drawable.ic_details_trash),
    NOTIFY_LATE(R.string.will_be_late, R.drawable.ic_details_trash),
    REPORT_NO_SHOW(R.string.customer_not_home, R.drawable.ic_details_trash),
    ISSUE_UNSAFE(R.string.i_feel_unsafe, R.drawable.ic_details_trash),
    ISSUE_HOURS(R.string.add_remove_hours, R.drawable.ic_details_trash),
    ISSUE_OTHER(R.string.different_issue, R.drawable.ic_details_trash),;

    private int textId;
    private int iconId;

    SupportActionType(int textId, int iconId)
    {
        this.textId = textId;
        this.iconId = iconId;
    }

    public int getTextId()
    {
        return textId;
    }

    public int getIconId()
    {
        return iconId;
    }
}
