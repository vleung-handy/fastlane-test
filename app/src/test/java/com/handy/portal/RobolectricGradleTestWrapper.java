package com.handy.portal;

import android.os.Build;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        constants = BuildConfig.class,
        packageName = "com.handy.portal")
public class RobolectricGradleTestWrapper {
}
