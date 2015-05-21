package com.handy.portal.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

}
