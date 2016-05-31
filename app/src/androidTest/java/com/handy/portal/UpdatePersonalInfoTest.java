package com.handy.portal;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.testutil.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
@LargeTest
public class UpdatePersonalInfoTest
{
//    private UiDevice mDevice; //TODO use this to test for system dialogs

    private static final String PERSISTENCE_TOKEN = "test_persistence_token"; //TODO put somewhere else
    private static final String LAST_PROVIDER_ID = "3546";

    private static final String NEW_ADDRESS_1 = "New Address 1";
    private static final String NEW_ADDRESS_2 = "New Address 2";
    private static final String NEW_CITY = "New City";
    private static final String NEW_STATE = "NY";
    private static final String NEW_ZIP_CODE = "10011";
    private static final String NEW_PHONE = "(646) 222-9879";

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class)
    {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, PERSISTENCE_TOKEN);
            intent.putExtra(PrefsKey.LAST_PROVIDER_ID, LAST_PROVIDER_ID);
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
    public void testPersonalInfo()
    {
        //todo make a function for this
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //todo investigate why this is flaky
        ViewUtil.waitForViewVisible(R.id.tab_nav_item_more, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tab_nav_item_more)).perform(click());

        ViewUtil.waitForViewVisible(R.id.nav_link_account_settings, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.nav_link_account_settings)).perform(click());

        ViewUtil.waitForViewVisible(R.id.contact_info_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.contact_info_layout)).perform(click());

//        ViewUtil.waitForViewVisible(R.id.provider_name_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
//        onView(withId(R.id.provider_name_edit_text)).perform(click(), replaceText("New name"), closeSoftKeyboard());

        ViewUtil.waitForViewVisible(R.id.provider_address_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.provider_address_edit_text)).perform(click(), replaceText(NEW_ADDRESS_1), closeSoftKeyboard());
        onView(withId(R.id.provider_address2_edit_text)).perform(click(), replaceText(NEW_ADDRESS_2), closeSoftKeyboard());
        onView(withId(R.id.provider_city_edit_text)).perform(click(), replaceText(NEW_CITY), closeSoftKeyboard());
        onView(withId(R.id.provider_state_edit_text)).perform(click(), replaceText(NEW_STATE), closeSoftKeyboard());
        onView(withId(R.id.provider_zip_code_edit_text)).perform(click(), replaceText(NEW_ZIP_CODE), closeSoftKeyboard());
        onView(withId(R.id.provider_phone_edit_text)).perform(click(), replaceText(NEW_PHONE), closeSoftKeyboard());
        onView(withId(R.id.profile_update_provider_button)).perform(click());

        ViewUtil.waitForViewVisible(R.id.contact_info_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withId(R.id.contact_info_layout)).perform(click());

        //TODO verify that the fields are correct? and consolidate

        ViewUtil.waitForViewVisible(R.id.provider_address_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.provider_address_edit_text)).check(matches(withText(NEW_ADDRESS_1)));
        onView(withId(R.id.provider_address2_edit_text)).check(matches(withText(NEW_ADDRESS_2)));
        onView(withId(R.id.provider_city_edit_text)).check(matches(withText(NEW_CITY)));
        onView(withId(R.id.provider_state_edit_text)).check(matches(withText(NEW_STATE)));
        onView(withId(R.id.provider_zip_code_edit_text)).check(matches(withText(NEW_ZIP_CODE)));

        onView(withId(R.id.provider_phone_edit_text)).check(matches(withText(NEW_PHONE)));

    }
}
