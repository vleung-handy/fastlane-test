package com.handy.portal.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;

import java.util.Date;

public final class Utils {

    public static int toDP(final float px, final Context context) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, context.getResources().getDisplayMetrics()));
    }

    static int toDP(final int px, final Context context) {
        return toDP((float)px, context);
    }

    public static boolean equalDates(final Date date1, final Date date2) {
        final Time time = new Time();
        time.set(date1.getTime());

        final int thenYear = time.year;
        final int thenMonth = time.month;
        final int thenMonthDay = time.monthDay;

        time.set(date2.getTime());

        return (thenYear == time.year) && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static void extendHitArea(final View view, final View parent, final int extra) {
        parent.post(new Runnable() {
            @Override
            public void run() {
                final Rect delegateArea = new Rect();
                view.getHitRect(delegateArea);
                delegateArea.right += extra;
                delegateArea.bottom += extra;

                final TouchDelegate touchDelegate = new TouchDelegate(delegateArea, view);
                if (View.class.isInstance(view.getParent())) {
                    ((View)view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static int interpolateColor(final int color1, final int color2, final float proportion) {
        final float[] hsva = new float[3];
        final float[] hsvb = new float[3];

        Color.colorToHSV(color1, hsva);
        Color.colorToHSV(color2, hsvb);

        for (int i = 0; i < 3; i++) hsvb[i] = (hsva[i] + ((hsvb[i] - hsva[i]) * proportion));
        return Color.HSVToColor(hsvb);
    }
}
