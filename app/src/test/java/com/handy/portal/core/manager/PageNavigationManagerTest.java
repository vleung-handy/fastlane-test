package com.handy.portal.core.manager;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handy.portal.RobolectricGradleTestWrapper;
import com.handy.portal.TestUtils;
import com.handy.portal.core.TestBaseApplication;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.ui.activity.MainActivity;
import com.handy.portal.deeplink.DeeplinkUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;

import javax.inject.Inject;

import static com.handy.portal.logger.handylogger.model.DeeplinkLog.Source.WEBVIEW;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class PageNavigationManagerTest extends RobolectricGradleTestWrapper {
    @Inject
    PageNavigationManager mNavigationManager;

    private MainActivity mActivity;

    @Before
    public void setUp() throws Exception {
        ((TestBaseApplication) ShadowApplication.getInstance().getApplicationContext()).inject(this);

        ActivityController<MainActivity> activityController = Robolectric.buildActivity(MainActivity.class).create();
        activityController.start().resume().visible();
        mActivity = activityController.get();
    }

    /**
     * not ideal but we currently have two deeplink handler methods due to logging complications
     *
     * @throws Exception
     */
    @Test
    public void onHandleSupportedDeeplinkUrl_shouldPostNavigationEventForDeeplinkPage() throws Exception {
        // verify the deeplinks defined in DeeplinkMapper.java

        Fragment currentFragment;

        mNavigationManager.handleDeeplinkUrl(mActivity.getSupportFragmentManager(), WEBVIEW, "clients");
        currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(MainViewPage.CLIENTS.getClassType()));

        mNavigationManager.handleDeeplinkUrl(mActivity.getSupportFragmentManager(), WEBVIEW, "payments");
        currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(MainViewPage.PAYMENTS.getClassType()));
    }


    @Test
    public void onHandleSupportedNonUriDerivedDeeplinkBundle_shouldPostNavigationEventForDeeplinkPage() throws Exception {
        // verify the deeplinks defined in DeeplinkMapper.java

        Bundle deeplinkDataBundle;
        Fragment currentFragment;

        deeplinkDataBundle = DeeplinkUtils.createDeeplinkBundleFromUri(Uri.parse("account_settings/edit_profile"));
        mNavigationManager.handleNonUriDerivedDeeplinkDataBundle(mActivity.getSupportFragmentManager(), deeplinkDataBundle, WEBVIEW);
        currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(MainViewPage.PROFILE_UPDATE.getClassType()));

        deeplinkDataBundle = DeeplinkUtils.createDeeplinkBundleFromUri(Uri.parse("account_settings/edit_payment_method"));
        mNavigationManager.handleNonUriDerivedDeeplinkDataBundle(mActivity.getSupportFragmentManager(), deeplinkDataBundle, WEBVIEW);
        currentFragment = TestUtils.getScreenFragment(mActivity.getSupportFragmentManager());
        assertThat(currentFragment, instanceOf(MainViewPage.SELECT_PAYMENT_METHOD.getClassType()));

    }
}
