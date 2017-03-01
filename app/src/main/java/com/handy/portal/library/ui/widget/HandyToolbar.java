package com.handy.portal.library.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;

/*
 * This class is used for giving an ID to Toolbar's title for the automation tests.
 */
public class HandyToolbar extends Toolbar {
    public HandyToolbar(Context context) {
        super(context);
    }

    public HandyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HandyToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof TextView) {
            child.setId(R.id.action_bar_title);
        }
        super.addView(child, index, params);
    }
}
