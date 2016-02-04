package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
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

public class BookingDetailsActionPanelView extends FrameLayout
{
    @Bind(R.id.booking_details_action_text)
    TextView mHelperText;

    private static final ImmutableSet<BookingActionButtonType> ASSOCIATED_BUTTON_ACTION_TYPES =
            ImmutableSet.of(
                    BookingActionButtonType.CLAIM,
                    BookingActionButtonType.ON_MY_WAY,
                    BookingActionButtonType.CHECK_IN,
                    BookingActionButtonType.CHECK_OUT,
                    BookingActionButtonType.CONTACT_PHONE
            );

    public BookingDetailsActionPanelView(final Context context)
    {
        super(context);
        init();
    }

    public BookingDetailsActionPanelView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingDetailsActionPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BookingDetailsActionPanelView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
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

        List<Booking.Action> allowedActions = booking.getAllowedActions();
        boolean removeSection = !hasAllowedAction(allowedActions);
        if (removeSection)
        {
            setVisibility(GONE);
        }
        else
        {
            initHelperText(allowedActions);
            setVisibility(VISIBLE);
        }
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_action, this);
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

    private void initHelperText(List<Booking.Action> allowedActions)
    {
        String helperContent = "";
        for (Booking.Action action : allowedActions)
        {
            if (ASSOCIATED_BUTTON_ACTION_TYPES.contains(UIUtils.getAssociatedActionType(action)))
            {
                if (action.getHelperText() != null && !action.getHelperText().isEmpty())
                {
                    //allow accumulation of helper text, it will all display below the buttons instead of below each button
                    if (!helperContent.isEmpty())
                    {
                        helperContent += "\n";
                    }
                    helperContent += action.getHelperText();
                }
            }
        }

        if (!helperContent.isEmpty())
        {
            mHelperText.setVisibility(View.VISIBLE);
            mHelperText.setText(helperContent);
        }
        else
        {
            mHelperText.setVisibility(View.GONE);
        }
    }

}
