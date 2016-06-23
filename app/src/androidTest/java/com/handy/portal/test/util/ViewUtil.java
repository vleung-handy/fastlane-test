package com.handy.portal.test.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.espresso.PerformException;
import android.view.View;

import org.hamcrest.Matcher;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * utility class containing non-app-specific methods to check for certain view states
 */
public class ViewUtil
{
    private static final long VIEW_STATE_QUERY_INTERVAL_MS = 50;
    public static final long LONG_MAX_WAIT_TIME_MS = 10000;
    public static final long SHORT_MAX_WAIT_TIME_MS = 5000;

    private ViewUtil()
    {
        //don't want this instantiated. should use static methods only
    }

    public static void waitForViewVisible(int viewId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withId(viewId), true, maxWaitingTimeMs);
    }

    public static void waitForViewNotVisible(int viewId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withId(viewId), false, maxWaitingTimeMs);
    }

    public static void waitForTextVisible(int stringResourceId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withText(stringResourceId), true, maxWaitingTimeMs);
    }

    public static void waitForTextNotVisible(int stringResourceId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withText(stringResourceId), false, maxWaitingTimeMs);
    }

    public static void checkToastDisplayed(int toastStringResourceId, Activity activity)
    {
        onView(withText(toastStringResourceId)).
                inRoot(withDecorView(not(activity.getWindow().getDecorView()))).
                check(matches(isDisplayed()));
    }

    /**
     * waits for the view with the given id to be a given visibility
     * <p>
     * TODO: cleaner way to do this?
     * TODO: add better error logging
     */
    public static void waitForViewVisibility(@NonNull Matcher<View> viewMatcher,
                                             final boolean visible,
                                             final long maxWaitingTimeMs)
    {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + maxWaitingTimeMs;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible == isViewDisplayed(viewMatcher))
            {
                return;
            }

            sleep(VIEW_STATE_QUERY_INTERVAL_MS);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for view visibility " + visible)
                .withViewDescription("view id: " + viewMatcher.toString())
                .withCause(new TimeoutException())
                .build();
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     */
    public static boolean isViewDisplayed(int viewId)
    {
        return isViewDisplayed(withId(viewId));
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     */
    public static boolean isViewDisplayed(@NonNull Matcher<View> viewMatcher)
    {
        try
        {
            onView(viewMatcher).check(matches(isDisplayed()));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    private static void sleep(final long timeMs)
    {
        try
        {
            Thread.sleep(timeMs);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
