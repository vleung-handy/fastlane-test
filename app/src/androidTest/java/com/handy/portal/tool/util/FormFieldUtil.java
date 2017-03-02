package com.handy.portal.tool.util;

import android.view.View;

import com.handy.portal.R;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

/**
 * utility class containing non-specific methods to perform actions on FormFields
 */
public class FormFieldUtil {
    public static void inputDateFormFieldText(int formFieldResourceId, String monthText, String yearText) {
        Matcher<View> dateFieldInputMatcher = allOf(
                withParent(withId(formFieldResourceId)),
                withId(R.id.date_form_field_input_container));

        onView(allOf(withId(R.id.month_value_text), withParent(dateFieldInputMatcher)))
                .perform(click(), replaceText(monthText), closeSoftKeyboard());

        onView(allOf(withId(R.id.year_value_text), withParent(dateFieldInputMatcher)))
                .perform(click(), replaceText(yearText), closeSoftKeyboard());
    }

    public static void inputFormFieldText(int formFieldResourceId, String text) {
        onView(allOf(withId(R.id.value_text), withParent(withId(formFieldResourceId))))
                .perform(click(), replaceText(text), closeSoftKeyboard());
    }
}
