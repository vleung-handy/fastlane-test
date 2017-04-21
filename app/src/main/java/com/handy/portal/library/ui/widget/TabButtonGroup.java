package com.handy.portal.library.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TabButtonGroup extends LinearLayout {
    private TabButton[] mTabButtons;
    private TabButton mCurrentlySelectedTab;

    public TabButtonGroup(final Context context) {
        super(context);
    }

    public TabButtonGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public TabButtonGroup(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabButtonGroup(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setTabs(final TabButton... tabButtons) {
        removeAllViews();
        mTabButtons = tabButtons;
        for (TabButton tabButton : tabButtons) {
            tabButton.setGroup(this);
            addView(tabButton);
        }
    }

    public void toggle(final TabButton tabButtonToToggle) {
        if (mTabButtons != null) {
            for (TabButton tabButton : mTabButtons) {
                tabButton.dim(0.5f);
            }
        }
        tabButtonToToggle.dim(1.0f);
        mCurrentlySelectedTab = tabButtonToToggle;
    }

    public TabButton getCurrentlySelectedTab() {
        return mCurrentlySelectedTab;
    }

    public void selected(@Nullable final String currentTabTitle) {
        if (TextUtils.isEmpty(currentTabTitle)) { return; }

        for (TabButton tabButton : mTabButtons) {
            if (currentTabTitle.equals(tabButton.getTitle())) {
                toggle(tabButton);
                return;
            }
        }
    }
}
