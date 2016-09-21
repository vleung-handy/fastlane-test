package com.handy.portal.ui.activity;

import android.os.Bundle;
import android.widget.FrameLayout;

// A blank activity with HnadyActionBarTheme used for testing individual fragment
public class TestActivity extends BaseActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));
    }
}
