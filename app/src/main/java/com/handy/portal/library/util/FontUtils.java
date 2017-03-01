package com.handy.portal.library.util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.StringDef;

import java.util.HashMap;

public class FontUtils {
    private static HashMap<String, Typeface> sTypefaces = new HashMap<>();

    public static final String CIRCULAR_BOOK = "fonts/CircularStd-Book.otf";
    public static final String CIRCULAR_BOLD = "fonts/CircularStd-Bold.otf";
    public static final String CIRCULAR_MEDIUM = "fonts/CircularStd-Medium.otf";


    @StringDef({CIRCULAR_BOOK, CIRCULAR_BOLD, CIRCULAR_MEDIUM})
    @interface Font {}

    public static Typeface getFont(Context context, @Font String font) {
        if (sTypefaces.get(font) == null) {
            sTypefaces.put(font, Typeface.createFromAsset(context.getAssets(), font));
        }
        return sTypefaces.get(font);
    }
}
