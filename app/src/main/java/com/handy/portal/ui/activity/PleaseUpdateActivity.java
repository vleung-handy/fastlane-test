package com.handy.portal.ui.activity;

import android.os.Bundle;

import com.handy.portal.R;

import butterknife.ButterKnife;

public class PleaseUpdateActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_update);
        ButterKnife.inject(this);
    }

    @Override
    public void onBackPressed()
    {

    }


}
