package com.handy.portal.dashboard.view;

import android.support.v4.view.ViewPager;

public interface DashboardViewPagerListener extends ViewPager.OnPageChangeListener {
    /**
     * Bind the indicator to a ViewPager.
     *
     * @param view
     */
    void setViewPager(ViewPager view);

    /**
     * <p>Set the current page of both the ViewPager and indicator.</p>
     * <p/>
     * <p>This <strong>must</strong> be used if you need to set the page before
     * the views are drawn on screen (e.g., default start page).</p>
     *
     * @param item
     */
    void setCurrentItem(int item);
}
