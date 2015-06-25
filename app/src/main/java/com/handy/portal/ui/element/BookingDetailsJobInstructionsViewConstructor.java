package com.handy.portal.ui.element;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.Booking.BookingStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class BookingDetailsJobInstructionsViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_job_instructions_list_layout)
    protected LinearLayout instructionsLayout;

    private static final Map<String, Integer> GROUP_ICONS;
    static
    {
        GROUP_ICONS = new HashMap<>();
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_ENTRY_METHOD, R.drawable.ic_details_entry);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_LINENS_LAUNDRY, R.drawable.ic_details_linens);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_REFRIGERATOR, R.drawable.ic_details_fridge);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_TRASH, R.drawable.ic_details_trash);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO, R.drawable.ic_details_request);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_job_instructions;
    }

    protected void constructViewFromBooking(Booking booking, List<Booking.ActionButtonData> allowedActions, Bundle arguments)
    {
        BookingStatus bookingStatus = (BookingStatus) arguments.getSerializable(BundleKeys.BOOKING_STATUS);
        boolean fullDetails = (bookingStatus == BookingStatus.CLAIMED);

        boolean removeJobInstructionsSection = true; //if we don't add any sections we will remove the view

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo = booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (booking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(activity.getString(R.string.bring_cleaning_supplies));

            removeJobInstructionsSection = false;
            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            //TODO: Hardcoding string and icon, we need to get this data from the booking info
            sectionView.init(activity.getString(R.string.supplies), R.drawable.ic_details_supplies, entries, true);
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

        if (fullDetails)
        {
            List<Booking.BookingInstructionGroup> bookingInstructionGroups = booking.getBookingInstructionGroups();
            if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
            {
                removeJobInstructionsSection = false;

                for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
                {
                    BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                    sectionView.init(group.getLabel(), GROUP_ICONS.get(group.getGroup()), group.getItems(), true);
                }
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
