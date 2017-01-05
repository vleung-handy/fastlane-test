package com.handy.portal;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.ui.activity.SplashActivity;
import com.handy.portal.test.data.TestUsers;
import com.handy.portal.test.model.TestUser;
import com.handy.portal.test.util.AppInteractionUtil;
import com.handy.portal.test.util.FormFieldUtil;
import com.handy.portal.test.util.ViewUtil;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class UpdatePaymentMethodTest
{
    /*
    See https://stripe.com/docs/testing for test stripe numbers
     */
    private static final TestUser TEST_USER = TestUsers.BOOKINGS_NY_PROVIDER;

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

    @After
    public void tearDown()
    {
        AppInteractionUtil.logOut();
    }

    /**
     * - Goes to the update payment method page
     * - For both bank account + debit card:
     *      - Enters new info and saves it
     *      - Verifies that fields were updated with new info
     *
     * Assumptions:
     * - there are no popup modals
     * - user doesn't have a payment method set up
     */
    @Test
    public void testUpdateBankAccount()
    {
        ViewUtil.waitForViewVisible(R.id.main_container, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewNotVisible(R.id.loading_overlay, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        ViewUtil.waitForViewVisible(R.id.tab_nav_item_more, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tab_nav_item_more)).perform(click());

        ViewUtil.waitForViewVisible(R.id.nav_link_account_settings, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.nav_link_account_settings)).perform(click());

        //click into the update payment method page
        ViewUtil.waitForViewVisible(R.id.edit_payment_option, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.edit_payment_option)).perform(click());

        /*Test updating bank account info*/
        //click the bank account option
        onView(withId(R.id.bank_account_option)).perform(click());

        //update bank account fields
        ViewUtil.waitForViewVisible(R.id.routing_number_field, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        FormFieldUtil.inputFormFieldText(R.id.routing_number_field, "110000000");
        FormFieldUtil.inputFormFieldText(R.id.account_number_field, "000123456789");
        FormFieldUtil.inputFormFieldText(R.id.tax_id_field, "000000000");
        onView(withId(R.id.payments_update_info_bank_account_submit_button)).perform(click());

        //verify bank account was updated
        ViewUtil.waitForViewVisible(R.id.bank_account_option, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewVisibility(allOf(withId(R.id.bank_account_details), withText("**** 6789")),
                true,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        /*Test updating debit card info*/
        //click the debit card option
        onView(withId(R.id.debit_card_option)).perform(click());

        //update debit card fields
        ViewUtil.waitForViewVisible(R.id.debit_card_number_field, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        FormFieldUtil.inputFormFieldText(R.id.debit_card_number_field, "4000056655665556");
        FormFieldUtil.inputDateFormFieldText(R.id.expiration_date_field, "01", "2050");
        FormFieldUtil.inputFormFieldText(R.id.security_code_field, "424");
        FormFieldUtil.inputFormFieldText(R.id.tax_id_field, "000000000");
        onView(withId(R.id.payments_update_info_debit_card_submit_button)).perform(click());

        //verify debit card was updated
        ViewUtil.waitForViewVisible(R.id.debit_card_option, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        ViewUtil.waitForViewVisibility(allOf(withId(R.id.debit_card_details), withText("**** 5556")),
                true,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS);
    }
}
