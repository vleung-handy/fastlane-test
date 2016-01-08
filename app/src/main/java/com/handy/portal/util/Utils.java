package com.handy.portal.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.model.LocationData;
import com.handy.portal.ui.activity.BaseActivity;

public final class Utils //TODO: we should reorganize these methods into more specific util classes
{
    public final static float LDPI = (float) 0.75;
    public final static float MDPI = (float) 1.0;
    public final static float HDPI = (float) 1.5;
    public final static float XHDPI = (float) 2.0;
    public final static float XXHDPI = (float) 3.0;

    public static int getObjectIdentifier(Object object)
    {
        return System.identityHashCode(object);
    }

    //returns true if the intent was successfully launched
    public static boolean safeLaunchIntent(Intent intent, Context context)
    {
        if (context == null)
        {
            Crashlytics.logException(new Exception("Trying to launch an intent with a null context!"));
        }
        else if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent);
            return true;
        }
        else //no activity found to handle the intent
        {
            //note: this must be called from the UI thread
            Toast toast = Toast.makeText(context, R.string.error_no_intent_handler_found, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Crashlytics.logException(new Exception("No activity found to handle the intent " + intent.toString()));
        }
        return false;
    }

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
