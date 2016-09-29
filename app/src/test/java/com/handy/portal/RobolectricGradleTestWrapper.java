package com.handy.portal;

import android.os.Build;

import com.handy.portal.core.TestBaseApplication;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP,
        constants = BuildConfig.class,
        application = TestBaseApplication.class,
        packageName = "com.handy.portal")
public class RobolectricGradleTestWrapper {}
