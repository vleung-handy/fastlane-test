package com.handy.portal.core.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * See: http://grishma102.blogspot.in/2013/12/how-to-make-text-flow-around-images-in.html
 */
public class MyLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int margin;
    private int lines;

    public MyLeadingMarginSpan2(int lines, int margin) {
        this.margin = margin;
        this.lines = lines;
    }

    /*Returns the value to which must be added indentation*/
    @Override
    public int getLeadingMargin(boolean first) {
        return first ? margin : 0;
    }

    @Override
    public void drawLeadingMargin(Canvas c,
                                  Paint p,
                                  int x,
                                  int dir,
                                  int top,
                                  int baseline,
                                  int bottom,
                                  CharSequence text,
                                  int start,
                                  int end,
                                  boolean first,
                                  Layout layout) {}

    @Override
    public int getLeadingMarginLineCount() {
        return lines;
    }
}
