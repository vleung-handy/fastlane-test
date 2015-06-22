package com.handy.portal.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.handy.portal.core.booking.Booking;
import com.handy.portal.ui.fragment.BookingDetailsFragment;

/**
 * Created by cdavis on 6/19/15.
 */
public class BookingActionButton extends Button
{
    public BookingActionButton(Context context) {
        super(context);
    }

    public BookingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BookingActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BookingActionButton(Context context, View parent) {
        super(context);
    }

    protected BookingDetailsFragment associatedFragment;

    public void init(BookingDetailsFragment fragment, Booking.ActionButtonData data)
    {
        if(data.getAssociatedActionType() == null)
        {
            System.err.println("BookingActionButton : No associated action type for : " + data.getActionName());
            return;
        }

        final BookingActionButton self = this;
        associatedFragment = fragment;
        setBackgroundResource(data.getAssociatedActionType().getDrawableId());
        setText(data.getAssociatedActionType().getDisplayNameId());
        final Booking.ButtonActionType buttonActionType = data.getAssociatedActionType();
        setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                self.associatedFragment.onActionButtonClick(buttonActionType);
                self.setEnabled(false); //turn self off after click to prevent multiple clicks
            }
        });

    }





}
