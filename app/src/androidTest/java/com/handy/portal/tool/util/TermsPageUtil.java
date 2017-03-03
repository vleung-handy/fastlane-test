package com.handy.portal.tool.util;

import com.handy.portal.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class TermsPageUtil {
    public static void acceptAllTermsIfPresent() {
        //accept all the terms
        while (true) //accept any required terms
        {
            try {
                ViewUtil.waitForViewVisible(R.id.accept_checkbox, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
                ViewUtil.waitForViewVisible(R.id.accept_button, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
                onView(withId(R.id.accept_checkbox)).perform(click());
                onView(withId(R.id.accept_button)).perform(click());
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
