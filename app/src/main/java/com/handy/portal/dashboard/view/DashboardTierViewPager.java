package com.handy.portal.dashboard.view;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class DashboardTierViewPager extends ViewPager {
    private int mMaxHeight = 0;

    public DashboardTierViewPager(final Context context) {
        super(context);
    }

    public DashboardTierViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Set height of children to height of the biggest child
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > mMaxHeight) { mMaxHeight = h; }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight + 40, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
