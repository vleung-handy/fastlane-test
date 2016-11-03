package com.handy.portal.library.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.handy.portal.library.util.FontUtils;

public class HandyTabLayout extends TabLayout
{
    public HandyTabLayout(Context context)
    {
        super(context);
    }

    public HandyTabLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public HandyTabLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter)
    {
        Typeface typeface = FontUtils.getFont(getContext(), FontUtils.CIRCULAR_BOOK);

        this.removeAllTabs();

        ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

        for (int i = 0, count = adapter.getCount(); i < count; i++)
        {
            Tab tab = this.newTab();
            this.addTab(tab.setText(adapter.getPageTitle(i)));
            AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
            // Try once again if the above failed
            // This is a hacky fix for using older android.support.design.widget.TabLayout from
            // com.android.support:design:22.2.0
            // With com.android.support:design:23.1.0 the above works.
            if (view == null)
            {
                view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(0);
            }
            view.setTypeface(typeface, Typeface.NORMAL);
        }
    }
}
