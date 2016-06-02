package com.handy.portal;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.TextViewUtil;
import com.handy.portal.test.util.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest
{
//    private UiDevice mDevice; //TODO use this to test for system dialogs

    private static final TestUser TEST_USER = TestUsers.FIRST_TIME_NY_PROVIDER;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class)
    {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, "");
            /*
            need to make sure user is logged out
            to work around behavior in which test device
            does not clear app data after each test
             */
            return intent;
        }
    };

    @Before
    public void init() {
//        mDevice = UiDevice.getInstance(getInstrumentation());
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
        ViewUtil.waitForViewVisible(R.id.phone_number_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.phone_number_edit_text, TEST_USER.getPhoneNumber());
        onView(withId(R.id.login_button)).perform(click());

        ViewUtil.waitForViewVisible(R.id.pin_code_edit_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.pin_code_edit_text, TEST_USER.getPinCode());
        onView(withId(R.id.login_button)).perform(click());

        //accept all the terms
        while(true) //accept any required terms
        {
            try
            {
                ViewUtil.waitForViewVisible(R.id.accept_checkbox, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
                ViewUtil.waitForViewVisible(R.id.accept_button, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                break;
            }
            onView(withId(R.id.accept_checkbox)).perform(click());
            onView(withId(R.id.accept_button)).perform(click());
        }

        //TODO support testing with the system permissions dialog in Android 6.0
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//        {
//            //the location permission settings blocker dialog should show
//            UiObject allowPermissions = mDevice.findObject(new UiSelector().text("Allow"));
//            if(allowPermissions.exists())
//            {
//                try {
//                    allowPermissions.click();
//                } catch (UiObjectNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
