package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.handy.portal.R;
import com.handy.portal.constant.BookingActionButtonType;
import com.handy.portal.model.Booking;
import com.handy.portal.util.UIUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsActionContactPanelView extends FrameLayout
{
    @Bind(R.id.booking_details_contact_profile_text)
    TextView mProfileText;

    protected final ImmutableList<BookingActionButtonType> associatedButtonActionTypes =
            ImmutableList.of(
                    BookingActionButtonType.CONTACT_PHONE,
                    BookingActionButtonType.CONTACT_TEXT
            );

    public BookingDetailsActionContactPanelView(final Context context, Booking booking)
    {
        super(context);
        init(booking);
    }

    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final Booking booking)
    {
        inflate(getContext(), R.layout.element_booking_details_contact, this);
        ButterKnife.bind(this);

        boolean actionPanelExists = hasAllowedAction(booking.getAllowedActions());
        if (actionPanelExists)
        {
            Booking.User bookingUser = booking.getUser();
            mProfileText.setText(bookingUser.getFullName());
        }
        else
        {
            setVisibility(View.GONE);
        }
    }

    private boolean hasAllowedAction(List<Booking.Action> allowedActions)
    {
        boolean hasAnAction = false;
        for (Booking.Action action : allowedActions)
        {
            if (getAssociatedButtonActionTypes().contains(UIUtils.getAssociatedActionType(action)))
            {
                hasAnAction = true;
                break;
            }
        }
        return hasAnAction;
    }

    private ImmutableList<BookingActionButtonType> getAssociatedButtonActionTypes()
    {
        return associatedButtonActionTypes;
    }
}
