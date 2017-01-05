package com.handy.portal.core.ui.activity;

import android.content.Intent;
import android.net.Uri;

import com.handy.portal.RobolectricGradleTestWrapper;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Shadows.shadowOf;

public class SplashActivityTest extends RobolectricGradleTestWrapper
{
    @Test
    public void shouldLaunchLoginActivityIfNotLoggedIn()
    {
        ActivityController<SplashActivity> activityController =
                Robolectric.buildActivity(SplashActivity.class, null).create().start().resume().visible();
        Intent nextStartedActivity =
                shadowOf(activityController.get()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(LoginActivity.class.getName()));
    }

    @Test
    public void shouldLaunchMainActivityIfLoggingInWithSlt()
    {
        Intent intent = new Intent();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("apip.handy.com")
                .appendPath("v3")
                .appendPath("sessions")
                .appendPath("request_slt")
                .appendQueryParameter("n", "n")
                .appendQueryParameter("sig", "sig")
                .appendQueryParameter("slt", "slt");
        intent.setData(builder.build());

        ActivityController<SplashActivity> activityController =
                Robolectric.buildActivity(SplashActivity.class, intent).create().start().resume().visible();
        Intent nextStartedActivity =
                shadowOf(activityController.get()).getNextStartedActivity();
        assertThat(nextStartedActivity.getComponent().getClassName(),
                equalTo(MainActivity.class.getName()));
    }
}
