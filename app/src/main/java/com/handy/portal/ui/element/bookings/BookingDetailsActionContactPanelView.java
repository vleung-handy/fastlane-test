package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.common.collect.ImmutableSet;
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

    private boolean mHasContent;

    private final ImmutableSet<BookingActionButtonType> ASSOCIATED_BUTTON_ACTION_TYPES =
            ImmutableSet.of(
                    BookingActionButtonType.CONTACT_PHONE,
                    BookingActionButtonType.CONTACT_TEXT
            );

    public BookingDetailsActionContactPanelView(final Context context)
    {
        super(context);
        init();
    }

    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BookingDetailsActionContactPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void refreshDisplay(final Booking booking)
    {
        // TODO: Currently BookingDetailsFragment is adding ActionButtons. Eventually, the display should be handled here, not from outside.
        // Remove ActionButtons added from outside
        removeAllViews();
        init();

        boolean hasAllowedAction = hasAllowedAction(booking.getAllowedActions());
        if (hasAllowedAction)
        {
            Booking.User bookingUser = booking.getUser();
            mProfileText.setText(bookingUser.getFullName());
            setVisibility(VISIBLE);
        }
        else
        {
            setVisibility(GONE);
        }
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_contact, this);
        ButterKnife.bind(this);
    }

    private boolean hasAllowedAction(List<Booking.Action> allowedActions)
    {
        for (Booking.Action action : allowedActions)
        {
            if (ASSOCIATED_BUTTON_ACTION_TYPES.contains(UIUtils.getAssociatedActionType(action)))
            {
                return true;
            }
        }
        return false;
    }

}
