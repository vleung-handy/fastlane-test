package com.handy.portal;

import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.testdata.TestUser;
import com.handy.portal.testutil.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest
{
    private final TestUser mTestUser = TestUser.FIRST_TIME_PROVIDER_NY;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<>(
            SplashActivity.class);

    @Before
    public void init() {
    }

    /*
    NOTE: for some reason AWS will run the test regardless of @Ignore
    if the prefix is "test"
    TODO: investigate
     */
    /**
     * Logs in as the test user
     *
     * Assumptions:
     * - no one is logged into the app
     * - there are no popup modals (for example, promos)
     */
    @Test
    public void testLogin()
    {
        //TODO: for proof of concept. we should make this more readable/reusable
        ViewUtil viewUtil = new ViewUtil();

        onView(withId(R.id.phone_number_edit_text)).perform(click(), typeText(mTestUser.getPhoneNumber()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        viewUtil.waitForViewVisible(R.id.pin_code_edit_text);
        onView(withId(R.id.pin_code_edit_text)).perform(click(), typeText(mTestUser.getPinCode()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        viewUtil.setMaxWaitingTimeMs(30000); //the network
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //the location permission settings blocker dialog should show
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
            if(allowPermissions.exists())
            {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        viewUtil.waitForViewVisible(R.id.main_container);
    }
}
