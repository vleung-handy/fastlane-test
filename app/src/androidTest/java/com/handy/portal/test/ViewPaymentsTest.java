package com.handy.portal.test;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.R;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.tool.data.TestUsers;
import com.handy.portal.tool.model.TestUser;
import com.handy.portal.tool.util.AppInteractionUtil;
import com.handy.portal.tool.util.ViewUtil;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class ViewPaymentsTest {
    private static final TestUser TEST_USER = TestUsers.BOOKINGS_NY_PROVIDER;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityRule = new ActivityTestRule<SplashActivity>(
            SplashActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Intent intent = super.getActivityIntent();
            intent.putExtra(PrefsKey.AUTH_TOKEN, TEST_USER.getPersistenceToken());
            return intent;
        }
    };

    @After
    public void tearDown() {
        AppInteractionUtil.logOut();
    }

    /**
     * basic test for verifying basic views in payments screen
     * FIXME: need better seed data so this can be tested more thoroughly
     */
    @Test
    public void testViewPayments() {
        AppInteractionUtil.removePhotoUpload();

        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewVisible(R.id.tab_nav_item_more, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tab_nav_item_more)).perform(click());

        //navigate to payment screen
        ViewUtil.waitForViewVisible(R.id.more_payments, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.more_payments)).perform(click());

        //make sure payment on header is as expected
        String currentWeekExpectedPaymentString = "$0.00";
        ViewUtil.waitForViewVisible(R.id.payments_batch_list_header, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.payments_current_week_expected_payment))
                .check(matches(withText(currentWeekExpectedPaymentString)));
        onView(withId(R.id.payments_batch_list_header)).perform(click());
        ViewUtil.waitForViewVisible(R.id.payment_details_list_header,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.payments_detail_total_payment_text))
                .check(matches(withText(currentWeekExpectedPaymentString)));

        Espresso.pressBack();

        //this seed user only has one payment list item
        //make sure expected payment is displayed in this case
        String previousWeekExpectedPaymentString = "$40.00";
        //check the list item
        onView(withId(R.id.payments_batch_list_item_payment_amount_text))
                .perform(scrollTo())
                .check(matches(withText(previousWeekExpectedPaymentString)));
        onView(withId(R.id.payments_batch_list_item_payment_amount_text)).perform(click());

        ViewUtil.waitForViewVisible(R.id.payment_details_list_header,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
        //check the header item
        onView(withId(R.id.payments_detail_total_payment_text))
                .check(matches(withText(previousWeekExpectedPaymentString)));
        //check the group title
        onView(withId(R.id.payments_detail_group_payments_text))
                .check(matches(withText(previousWeekExpectedPaymentString)));
        //check the list item
        onView(withId(R.id.payments_detail_payment_text))
                .check(matches(withText(previousWeekExpectedPaymentString)));
    }
}
