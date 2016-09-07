package com.handy.portal.test.util;

import com.handy.portal.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class AppInteractionUtil
{
    public static void logOut()
    {
        ViewUtil.waitForViewVisible(R.id.tab_nav_item_more, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tab_nav_item_more)).perform(click());

        ViewUtil.waitForViewVisible(R.id.nav_link_account_settings, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.nav_link_account_settings)).perform(click());

        ViewUtil.waitForViewVisible(R.id.log_out_button, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.log_out_button)).perform(click());

        onView(withText(R.string.yes)).perform(click());
    }
}
