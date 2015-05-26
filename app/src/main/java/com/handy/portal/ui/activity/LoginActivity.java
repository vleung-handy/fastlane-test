package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.BaseApplication;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        ((BaseApplication) this.getApplication()).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    private void openMainActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
    }

}
