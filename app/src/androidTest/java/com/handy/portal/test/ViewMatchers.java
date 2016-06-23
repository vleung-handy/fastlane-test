package com.handy.portal.test;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ViewMatchers
{
    @NonNull
    public static Matcher<View> childAtIndex(@NonNull final Matcher<View> parentMatcher,
                                             final int childIndex)
    {
        return new TypeSafeMatcher<View>()
        {

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("with child view at index " + childIndex + " of matcher " + parentMatcher.toString());
            }

            @Override
            protected boolean matchesSafely(final View view)
            {
                if(!parentMatcher.matches(view.getParent()) || !(view.getParent() instanceof ViewGroup))
                {
                    //if view's parent isn't a ViewGroup we can't get the child at a specific index, so no match
                    return false;
                }

                ViewGroup group = (ViewGroup) view.getParent();
                return group.getChildAt(childIndex).equals(view);
            }
        };
    }
}
