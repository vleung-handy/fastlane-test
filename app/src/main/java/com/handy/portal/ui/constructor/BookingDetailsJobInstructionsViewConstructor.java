package com.handy.portal.ui.constructor;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingStatus;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsSectionView;
import com.handy.portal.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class BookingDetailsJobInstructionsViewConstructor extends BookingDetailsViewConstructor
{
    @InjectView(R.id.booking_details_job_instructions_list_layout)
    protected LinearLayout instructionsLayout;
    @InjectView(R.id.job_instructions_reveal_notice)
    protected TextView revealNotice;

    private final boolean isForPayments;

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

    public BookingDetailsJobInstructionsViewConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
        this.isForPayments = arguments.getBoolean(BundleKeys.IS_FOR_PAYMENTS, false);
    }

    protected int getLayoutResourceId()
    {
        return R.layout.element_booking_details_job_instructions;
    }

    @Override
    protected boolean constructView(ViewGroup container, Booking booking)
    {
        BookingStatus bookingStatus = (BookingStatus) getArguments().getSerializable(BundleKeys.BOOKING_STATUS);
        boolean isHomeCleaning = booking.getServiceInfo().isHomeCleaning();
        boolean fullDetails = this.isForPayments || !isHomeCleaning || (bookingStatus == BookingStatus.CLAIMED);

        boolean jobInstructionsSectionConstructed = false; //if we don't add any sections we will not add the view

        if (booking.getRevealDate() != null && booking.isClaimedByMe())
        {
            Spanned noticeText = Html.fromHtml(getContext().getResources().getString(R.string.full_details_and_more_available_on_date, DateTimeUtils.formatDetailedDate(booking.getRevealDate())));
            revealNotice.setText(noticeText);
            revealNotice.setVisibility(View.VISIBLE);
            jobInstructionsSectionConstructed = true;
        }

        //Show description field regardless of claim status if the booking is not for cleaning (e.g. furniture assembly)
        if (!isHomeCleaning && booking.getDescription() != null)
        {
            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            sectionView.init(getContext().getString(R.string.description), R.drawable.ic_details_notes, Lists.newArrayList(booking.getDescription()));

            jobInstructionsSectionConstructed = true;
        }

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo = booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (booking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(getContext().getString(R.string.bring_cleaning_supplies));

            BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
            sectionView.init(getContext().getString(R.string.supplies), R.drawable.ic_details_supplies, entries);

            jobInstructionsSectionConstructed = true;
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
                BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                sectionView.init(getContext().getString(R.string.extras), R.drawable.ic_details_extras, entries);

                jobInstructionsSectionConstructed = true;
            }
        }

        if (fullDetails)
        {
            List<Booking.BookingInstructionGroup> bookingInstructionGroups = booking.getBookingInstructionGroups();
            if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
            {
                for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
                {
                    BookingDetailsJobInstructionsSectionView sectionView = addSection(instructionsLayout);
                    sectionView.init(group.getLabel(), GROUP_ICONS.get(group.getGroup()), group.getItems());
                }

                jobInstructionsSectionConstructed = true;
            }
        }

        if (!jobInstructionsSectionConstructed)
        {
            container.setVisibility(View.GONE);
        }

        return jobInstructionsSectionConstructed;
    }

    private BookingDetailsJobInstructionsSectionView addSection(LinearLayout instructionsLayout)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_section, instructionsLayout);
        return (BookingDetailsJobInstructionsSectionView) instructionsLayout.getChildAt(instructionsLayout.getChildCount() - 1);
    }
}
