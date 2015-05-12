package com.handy.portal.ui.element;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingInstruction;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsJobInstructionsView extends BookingDetailsView
{
    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_job_instructions;
    }

    protected void initFromBooking(Booking booking)
    {
        LinearLayout instructionsLayout = (LinearLayout) parentViewGroup.findViewById(R.id.booking_details_job_instructions_list_layout);

        if(booking.getBookingInstructions() != null)
        {
            for (int i = 0; i < booking.getBookingInstructions().size(); i++)
            {
                BookingInstruction instruction = booking.getBookingInstructions().get(i);

                //add a new entry to the layout and set the text for it
                LayoutInflater.from(context).inflate(R.layout.element_booking_details_job_instructions_entry, instructionsLayout);

                TextView entryText = ((TextView) (instructionsLayout.getChildAt(i)));

                System.out.println("instruction : " + instruction.getDescription());

                entryText.setText(instruction.getDescription());
            }
        }

        if(booking.getExtrasInfo() != null)
        {
            for (int i = 0; i < booking.getExtrasInfo().size(); i++)
            {
                Booking.ExtraInfo extra = booking.getExtrasInfo().get(i);

                //add a new entry to the layout and set the text for it
                LayoutInflater.from(context).inflate(R.layout.element_booking_details_job_instructions_entry, instructionsLayout);
                TextView extrasText = ((TextView) (instructionsLayout.getChildAt(i)));
                extrasText.setText(extra.getLabel());
            }
        }
    }
}
