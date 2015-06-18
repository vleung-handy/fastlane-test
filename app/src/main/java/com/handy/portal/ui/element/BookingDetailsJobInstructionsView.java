package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingInstruction;
import com.handy.portal.core.booking.Booking.BookingStatus;

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
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        boolean fullDetails = false;
        if (bookingStatus == BookingStatus.CLAIMED)
        {
            fullDetails = true;
        }

        boolean removeJobInstructionsSection = true;

        //Booking instructions
        if (fullDetails)
        {
            if (booking.getBookingInstructions() != null && booking.getBookingInstructions().size() > 0)
            {
                List<String> entries = new ArrayList<>();
                for (int i = 0; i < booking.getBookingInstructions().size(); i++)
                {
                    BookingInstruction instruction = booking.getBookingInstructions().get(i);
                    entries.add(instruction.getDescription());
                }

                if(entries.size() > 0)
                {
                    removeJobInstructionsSection = false;
                    BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                    //TODO: Hardcoding string and icon, we need to get this data from the booking info
                    sectionView.init(activity.getString(R.string.customer_details), R.drawable.ic_details_extras, entries, true);
                }
            }
        }

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo = booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (booking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(activity.getString(R.string.bring_cleaning_supplies));

            removeJobInstructionsSection = false;
            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            //TODO: Hardcoding string and icon, we need to get this data from the booking info
            sectionView.init(activity.getString(R.string.supplies), R.drawable.ic_details_extras, entries, true);
        }

        //Extras - excluding Supplies instructions
        if (booking.getExtrasInfo() != null && booking.getExtrasInfo().size() > 0)
        {
            List<String> entries = new ArrayList<>();
            for (int i = 0; i < booking.getExtrasInfo().size(); i++)
            {
                Booking.ExtraInfo extra = booking.getExtrasInfo().get(i).getExtraInfo();
                if (!extra.getMachineName().equals(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES))
                {
                    entries.add(extra.getName());
                }
            }

            if (entries.size() > 0)
            {
                removeJobInstructionsSection = false;
                BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                //TODO: Hardcoding string and icon, we need to get this data from the booking info
                sectionView.init(activity.getString(R.string.extras), R.drawable.ic_details_extras, entries, true);
            }
        }

        //Note to pro
        if (fullDetails)
        {
            List<String> entries = new ArrayList<>();

            if(booking.getDescription() != null && !booking.getDescription().isEmpty())
            {
                entries.add(booking.getDescription());
            }

            if (booking.getProNote() != null && !booking.getProNote().isEmpty())
            {
                entries.add(booking.getProNote());
            }

            if(entries.size() > 0)
            {
                removeJobInstructionsSection = false;
                BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                //TODO: Hardcoding string and icon, we need to get this data from the booking info
                sectionView.init(activity.getString(R.string.customer_request), R.drawable.ic_details_extras, entries, false);
            }
        }

        if (removeJobInstructionsSection)
        {
            removeView();
        }
    }

    private BookingDetailsJobInstructionsSectionView addSection(LinearLayout instructionsLayout)
    {
        LayoutInflater.from(activity).inflate(R.layout.element_booking_details_job_instructions_section, instructionsLayout);
        BookingDetailsJobInstructionsSectionView sectionView = ((BookingDetailsJobInstructionsSectionView) (instructionsLayout.getChildAt(instructionsLayout.getChildCount() - 1)));
        return sectionView;
    }


}
