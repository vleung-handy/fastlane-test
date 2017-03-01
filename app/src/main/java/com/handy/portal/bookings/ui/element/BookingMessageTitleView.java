package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.util.AttributeSet;

import com.handy.portal.R;
import com.handy.portal.library.ui.view.LeftIndicatorTextView;

/**
 * the view that displays the message listing or details title
 * of the booking's display attributes model
 * <p>
 * currently it has a green text.
 * would be nice to support multiple styles later.
 */
public class BookingMessageTitleView extends LeftIndicatorTextView {
    public BookingMessageTitleView(final Context context) {
        super(context);
    }

    public BookingMessageTitleView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingMessageTitleView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
        //set specific styles for this view
        setTextColorResourceId(R.color.requested_green)
                .setImageResourceId(R.drawable.ic_swap)
                .setTextSize(R.dimen.small_text_size);
    }

    public BookingMessageTitleView showSwapIcon() {
        getImage().setVisibility(VISIBLE);
        return this;
    }

    public BookingMessageTitleView hideSwapIcon() {
        getImage().setVisibility(GONE);
        return this;
    }
}
