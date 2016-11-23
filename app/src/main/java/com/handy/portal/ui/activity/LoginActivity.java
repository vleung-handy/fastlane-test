package com.handy.portal.ui.activity;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.model.ConfigurationResponse;


public class LoginActivity extends BaseActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ConfigurationResponse config = mConfigManager.getConfigurationResponse();
        if (config != null && config.isSltEnabled())
        {
            setContentView(R.layout.activity_login_slt);
        }
        else
        {
            setContentView(R.layout.activity_login);
        }
    }
}
