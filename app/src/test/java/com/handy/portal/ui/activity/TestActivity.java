package com.handy.portal.ui.activity;

import android.os.Bundle;

import com.handy.portal.R;

// A blank activity with HnadyActionBarTheme used for testing individual fragment
public class TestActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HandyActionBarTheme);
    }
}
