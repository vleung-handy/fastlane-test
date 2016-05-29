package com.handy.portal.ui.activity;

import android.os.Bundle;

// A blank activity with HnadyActionBarTheme used for testing individual fragment
public class TestActivity extends BaseActivity
{
    @Override
    protected boolean shouldTriggerSetup()
    {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
}
