package com.handy.portal.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;

import java.util.Calendar;
import java.util.Date;

public final class Utils
{
    public static void inject(Context context, Object object)
    {
        ((BaseApplication) context.getApplicationContext()).inject(object);
    }

    public static int toDP(final float px, final Context context)
    {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, context.getResources().getDisplayMetrics()));
    }

    static int toDP(final int px, final Context context)
    {
        return toDP((float) px, context);
    }

    public static boolean isInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean equalCalendarDates(final Date date1, final Date date2)
    {
        final Time time = new Time();
        time.set(date1.getTime());

        final int thenYear = time.year;
        final int thenMonth = time.month;
        final int thenMonthDay = time.monthDay;

        time.set(date2.getTime());

        return (thenYear == time.year) && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static Date getDateWithoutTime(final Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void extendHitArea(final View view, final View parent, final int extra)
    {
        parent.post(new Runnable()
        {
            @Override
            public void run()
            {
                final Rect delegateArea = new Rect();
                view.getHitRect(delegateArea);
                delegateArea.right += extra;
                delegateArea.bottom += extra;

                final TouchDelegate touchDelegate = new TouchDelegate(delegateArea, view);
                if (View.class.isInstance(view.getParent()))
                {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static int interpolateColor(final int color1, final int color2, final float proportion)
    {
        final float[] hsva = new float[3];
        final float[] hsvb = new float[3];

        Color.colorToHSV(color1, hsva);
        Color.colorToHSV(color2, hsvb);

        for (int i = 0; i < 3; i++) hsvb[i] = (hsva[i] + ((hsvb[i] - hsva[i]) * proportion));
        return Color.HSVToColor(hsvb);
    }

    public static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e)
        {
            // should never happen
            Crashlytics.logException(new RuntimeException("Could not get package name",  e));
            return -1;
        }
    }

    public static LocationData getCurrentLocation(BaseActivity baseActivity)
    {
        LocationData locationData;
        if(baseActivity != null)
        {
            locationData = new LocationData(baseActivity.getLastLocation());
        }
        else
        {
            Crashlytics.log("Attempting to access location data outside of a BaseActivity Context, returning empty");
            locationData = new LocationData();
        }
        return locationData;
    }


}
