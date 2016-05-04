package com.handy.portal.preactivation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.preactivation.PreActivationSetupStep;
import com.handy.portal.preactivation.PreActivationSetupStepFragment;
import com.handy.portal.ui.activity.BaseActivity;
import com.handy.portal.ui.activity.SplashActivity;

public class PreActivationSetupActivity extends BaseActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_activation_setup);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        goToFirstStep();
    }

    public void goToStep(@Nullable final PreActivationSetupStep step,
                         final boolean allowBackNavigation)
    {
        if (step != null)
        {
            try
            {
                final PreActivationSetupStepFragment fragment =
                        step.getFragmentClass().newInstance();
                final FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                if (allowBackNavigation)
                {
                    fragmentTransaction.addToBackStack(null);
                }
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );
                fragmentTransaction.replace(R.id.main_container, fragment);
                fragmentTransaction.commit();
            }
            catch (Exception e)
            {
                // should not happen
            }
        }
        else
        {
            final Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void goToFirstStep()
    {
        goToStep(PreActivationSetupStep.first(), false);
    }
}
