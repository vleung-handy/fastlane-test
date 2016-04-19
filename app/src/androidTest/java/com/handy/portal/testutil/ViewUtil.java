package com.handy.portal.testutil;

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
    public static final long DEFAULT_QUERY_INTERVAL_MS = 50;
    public static final long DEFAULT_MAX_WAIT_TIME_MS = 10000;

    private long mQueryIntervalMs;
    private long mMaxWaitingTimeMs;

    public ViewUtil()
    {
        mQueryIntervalMs = DEFAULT_QUERY_INTERVAL_MS;
        mMaxWaitingTimeMs = DEFAULT_MAX_WAIT_TIME_MS;
    }

    public ViewUtil(long queryIntervalMs, long maxWaitingTimeMs)
    {
        mQueryIntervalMs = queryIntervalMs;
        mMaxWaitingTimeMs = maxWaitingTimeMs;
    }

    public void setQueryIntervalMs(final long queryIntervalMs)
    {
        mQueryIntervalMs = queryIntervalMs;
    }

    public void setMaxWaitingTimeMs(final long maxWaitingTimeMs)
    {
        mMaxWaitingTimeMs = maxWaitingTimeMs;
    }

    public void waitForViewVisible(@NonNull Matcher<View> viewMatcher)
    {
        waitForViewVisibility(viewMatcher, true);
    }

    public void waitForViewNotVisible(@NonNull Matcher<View> viewMatcher)
    {
        waitForViewVisibility(viewMatcher, false);
    }

    public void waitForViewVisible(int viewId)
    {
        waitForViewVisibility(withId(viewId), true);
    }

    public void waitForViewNotVisible(int viewId)
    {
        waitForViewVisibility(withId(viewId), false);
    }

    public void waitForViewToAppearThenDisappear(int viewId)
    {
        waitForViewVisible(viewId);
        waitForViewNotVisible(viewId);
    }

    public void checkToastDisplayed(int toastStringResourceId, Activity activity)
    {
        onView(withText(toastStringResourceId)).
                inRoot(withDecorView(not(activity.getWindow().getDecorView()))).
                check(matches(isDisplayed()));
    }

    /**
     * waits for the view with the given id to be a given visibility
     * <p/>
     * TODO: cleaner way to do this?
     * TODO: add better error logging
     *
     * @param viewMatcher
     * @param visible
     */
    public void waitForViewVisibility(@NonNull Matcher<View> viewMatcher, final boolean visible)
    {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + mMaxWaitingTimeMs;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible ? isViewDisplayed(viewMatcher) : !isViewDisplayed(viewMatcher))
            {
                return;
            }

            sleep(mQueryIntervalMs);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for view visibility " + visible)
                .withViewDescription("view id: " + viewMatcher.toString())
                .withCause(new TimeoutException())
                .build();
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     *
     * @param viewId
     * @return
     */
    public static boolean isViewDisplayed(int viewId)
    {
        return isViewDisplayed(withId(viewId));
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     *
     * @param viewMatcher
     * @return
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
