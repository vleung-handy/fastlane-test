package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;

public class BookingActionButton extends Button
{
    public BookingActionButton(Context context)
    {
        super(context);
    }

    public BookingActionButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingActionButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public BookingActionButton(Context context, View parent)
    {
        super(context);
    }

    protected BookingDetailsFragment associatedFragment;

    public void init(Booking booking, BookingDetailsFragment fragment, Booking.Action data)
    {
        final BookingActionButtonType bookingActionButtonType = UIUtils.getAssociatedActionType(data);
        if (bookingActionButtonType == null)
        {
            Crashlytics.log("BookingActionButton : No associated action type for : " + data.getActionName());
            return;
        }

        final BookingActionButton self = this;
        associatedFragment = fragment;
        setId(bookingActionButtonType.getId());
        setBackgroundResource(bookingActionButtonType.getBackgroundDrawableId());
        setTextAppearance(getContext(), bookingActionButtonType.getTextStyleId());
        setText(bookingActionButtonType.getDisplayNameId(booking));
        setTypeface(TextUtils.get(getContext(), TextUtils.Fonts.CIRCULAR_BOOK));
        setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                self.associatedFragment.onActionButtonClick(bookingActionButtonType);
            }
        });
        setEnabled(data.isEnabled());
    }

}
