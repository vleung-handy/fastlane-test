package com.handy.portal;

import android.os.Build;

import com.handy.portal.core.TestBaseApplication;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        constants = BuildConfig.class,
        application = TestBaseApplication.class,
        packageName = "com.handy.portal")
public class RobolectricGradleTestWrapper
{
    private static final String[] mRequiredPermissions = new String[]
            {android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION};

    @Before
    public void setUp() throws Exception
    {
        // This is required because the permissions are checked in BaseActivity
        ShadowApplication app = Shadows.shadowOf(RuntimeEnvironment.application);
        app.grantPermissions(mRequiredPermissions);
    }
}
