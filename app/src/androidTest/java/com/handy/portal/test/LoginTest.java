package com.handy.portal.test;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.R;
import com.handy.portal.core.ui.activity.LoginActivity;
import com.handy.portal.tool.data.TestUsers;
import com.handy.portal.tool.model.TestUser;
import com.handy.portal.tool.util.AppInteractionUtil;
import com.handy.portal.tool.util.TermsPageUtil;
import com.handy.portal.tool.util.TextViewUtil;
import com.handy.portal.tool.util.ViewUtil;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class LoginTest {
//    private UiDevice mDevice; //TODO use this to test for system dialogs

    private static final TestUser TEST_USER = TestUsers.FIRST_TIME_NY_PROVIDER;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    @After
    public void tearDown() {
        AppInteractionUtil.logOut();
    }

    /*
    NOTE: for some reason AWS will run the test regardless of @Ignore
    if the prefix is "test"
    TODO: investigate
     */

    /**
     * Logs in as the test user
     * <p>
     * Assumptions:
     * - no one is logged into the app
     * - there are no popup modals (for example, promos)
     */
    @Test
    public void testLogin() {
        //TODO: for proof of concept. we should make this more readable/reusable
        ViewUtil.waitForViewVisible(R.id.phone_number_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.phone_number_edit_text, TEST_USER.getPhoneNumber());
        onView(withId(R.id.login_button)).perform(click());

        ViewUtil.waitForViewVisible(R.id.pin_code_edit_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.pin_code_edit_text, TEST_USER.getPinCode());
        onView(withId(R.id.login_button)).perform(click());

        TermsPageUtil.acceptAllTermsIfPresent();

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
