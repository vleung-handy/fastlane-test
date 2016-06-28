package com.handy.portal;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.test.data.TestFields;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestField;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.TextViewUtil;
import com.handy.portal.test.util.ViewUtil;
import com.handy.portal.ui.activity.SplashActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class UpdatePersonalInfoTest
{
    private static final TestUser TEST_USER = TestUsers.BOOKINGS_NY_PROVIDER;
    private static final TestField[] TEST_UPDATE_PERSONAL_INFO_FIELDS
            = TestFields.UPDATE_PERSONAL_INFO_FIELDS;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class)
    {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, TEST_USER.getPersistenceToken());
            return intent;
        }
    };

    /*
    NOTE: for some reason AWS will run the test regardless of @Ignore
    if the prefix is "test"
    TODO: investigate
     */
    /**
     * - Goes to the update personal info page
     * - Enters new info and saves it
     * - Verifies that fields were updated with new info
     *
     * Assumptions:
     * - there are no popup modals
     */
    @Test
    public void testPersonalInfo()
    {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewVisible(R.id.tab_nav_item_more, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tab_nav_item_more)).perform(click());

        ViewUtil.waitForViewVisible(R.id.nav_link_account_settings, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.nav_link_account_settings)).perform(click());

        //click into the update personal info page
        ViewUtil.waitForViewVisible(R.id.contact_info_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.contact_info_layout)).perform(click());

        //update fields
        ViewUtil.waitForViewVisible(R.id.provider_address_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.updateFieldValues(TEST_UPDATE_PERSONAL_INFO_FIELDS);
        onView(withId(R.id.profile_update_provider_button)).perform(click());

        ViewUtil.waitForViewVisible(R.id.contact_info_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withId(R.id.contact_info_layout)).perform(click());

        ViewUtil.waitForViewVisible(R.id.provider_address_edit_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        TextViewUtil.assertFieldValues(TEST_UPDATE_PERSONAL_INFO_FIELDS);

    }
}
