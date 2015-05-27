package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingInstruction;
import com.handy.portal.ui.fragment.BookingDetailsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsJobInstructionsView extends BookingDetailsView
{
    @InjectView(R.id.booking_details_job_instructions_list_layout)
    protected LinearLayout instructionsLayout;

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_job_instructions;
    }

    protected void initFromBooking(Booking booking, Bundle arguments)
    {
        BookingDetailsFragment.BookingStatus bookingStatus = (BookingDetailsFragment.BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        boolean fullDetails = false;
        if(bookingStatus == BookingDetailsFragment.BookingStatus.CLAIMED)
        {
            fullDetails = true;
        }

        //Booking instructions
        if (fullDetails)
        {
            if (booking.getBookingInstructions() != null && booking.getBookingInstructions().size() > 0)
            {
                //New Section
                BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);

                List<String> entries = new ArrayList<>();
                for (int i = 0; i < booking.getBookingInstructions().size(); i++)
                {
                    BookingInstruction instruction = booking.getBookingInstructions().get(i);
                    entries.add(instruction.getDescription());
                }

                //TODO: Hardcoding string and icon, we need to get this data from the booking info
                sectionView.init(context.getString(R.string.customer_details), R.drawable.circle_green, entries, true);
            }
        }

        //Extras
        if (booking.getExtrasInfo() != null && booking.getExtrasInfo().size() > 0)
        {
            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            List<String> entries = new ArrayList<>();

            for (int i = 0; i < booking.getExtrasInfo().size(); i++)
            {
                Booking.ExtraInfo extra = booking.getExtrasInfo().get(i).getExtraInfo();
                entries.add(extra.getName());
            }

            //TODO: Hardcoding string and icon, we need to get this data from the booking info
            sectionView.init(context.getString(R.string.extras), R.drawable.circle_green, entries, true);
        }

        //Note to pro
        if (fullDetails)
        {
            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            List<String> entries = new ArrayList<>();
            entries.add(booking.getDescription());

            if (booking.getProNote() != null && !booking.getProNote().isEmpty())
            {
                entries.add(booking.getProNote());
            }

            //TODO: Hardcoding string and icon, we need to get this data from the booking info
            sectionView.init(context.getString(R.string.customer_request), R.drawable.circle_green, entries, false);
        }
    }

    private BookingDetailsJobInstructionsSectionView addSection(LinearLayout instructionsLayout)
    {
        LayoutInflater.from(context).inflate(R.layout.element_booking_details_job_instructions_section, instructionsLayout);
        BookingDetailsJobInstructionsSectionView sectionView = ((BookingDetailsJobInstructionsSectionView) (instructionsLayout.getChildAt(instructionsLayout.getChildCount() - 1)));
        return sectionView;
    }


}
