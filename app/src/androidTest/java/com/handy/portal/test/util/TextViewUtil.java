package com.handy.portal.test.util;

import com.handy.portal.test.model.TestField;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * utility class containing non-app-specific methods to check for certain TextView states
 */
public class TextViewUtil {
    public static void updateEditTextView(int viewResourceId, String newText) {
        onView(withId(viewResourceId)).perform(click(), replaceText(newText), closeSoftKeyboard());
    }

    public static void assertViewHasText(int viewResourceId, String expectedText) {
        onView(withId(viewResourceId)).check(matches(withText(expectedText)));
    }

    /**
     * for each field in the given field set, updates
     * the view associated with the field's resource id
     * with the field's value
     *
     * @param testFieldSet
     */
    public static void updateFieldValues(TestField[] testFieldSet) {
        for (TestField testField : testFieldSet) {
            TextViewUtil.updateEditTextView(testField.getViewResourceId(), testField.getValue());
        }
    }

    /**
     * for each field in the given field set, asserts
     * that the view associated with the field's resource id
     * has a value equal to the field's value
     *
     * @param testFieldSet
     */
    public static void assertFieldValues(TestField[] testFieldSet) {
        for (TestField testField : testFieldSet) {
            TextViewUtil.assertViewHasText(testField.getViewResourceId(), testField.getValue());
        }
    }
}
