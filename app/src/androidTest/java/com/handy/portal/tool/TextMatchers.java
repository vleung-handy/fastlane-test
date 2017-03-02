package com.handy.portal.tool;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.regex.Pattern;

/**
 * currently not being used but leaving for reference
 */
public class TextMatchers {
    @NonNull
    public static Matcher<View> withPattern(@NonNull final Pattern pattern) {
        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("with string pattern: ");
                //todo
            }

            @Override
            protected boolean matchesSafely(final TextView item) {
                String text = (String) item.getText();
                return pattern.matcher(text).matches();
            }
        };
    }
}
