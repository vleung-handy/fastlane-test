package com.handy.portal.ui.element.bookings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.handy.portal.R;
import com.handy.portal.constant.PrefsType;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.element.BookingDetailsJobInstructionsSectionView;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.UIUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingDetailsJobInstructionsView extends FrameLayout
{
    @Bind(R.id.booking_details_job_instructions_list_layout)
    LinearLayout mInstructionsLayout;
    @Bind(R.id.job_instructions_reveal_notice)
    TextView mRevealNotice;

    private Booking.BookingInstructionGroup mPreferencesGroup;

    private static final Map<String, Integer> GROUP_ICONS;
    private static final Gson GSON = new Gson();

    static
    {
        GROUP_ICONS = new HashMap<>();
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_ENTRY_METHOD, R.drawable.ic_details_entry);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_LINENS_LAUNDRY, R.drawable.ic_details_linens);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_REFRIGERATOR, R.drawable.ic_details_fridge);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_TRASH, R.drawable.ic_details_trash);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_NOTE_TO_PRO, R.drawable.ic_details_request);
        GROUP_ICONS.put(Booking.BookingInstructionGroup.GROUP_PREFERENCES, R.drawable.ic_details_request);
    }


    public BookingDetailsJobInstructionsView(final Context context)
    {
        super(context);
        init();
    }

    public BookingDetailsJobInstructionsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BookingDetailsJobInstructionsView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public BookingDetailsJobInstructionsView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void refreshDisplay(@NonNull final Booking booking, boolean isFromPayments,
                               @NonNull Booking.BookingStatus bookingStatus)
    {
        mInstructionsLayout.removeAllViews();

        boolean isHomeCleaning = booking.getServiceInfo().isHomeCleaning();
        boolean shouldShowFullDetails =
                isFromPayments || !isHomeCleaning || (bookingStatus == Booking.BookingStatus.CLAIMED);

        boolean hasContent = false;

        if (booking.getRevealDate() != null && booking.isClaimedByMe())
        {
            Spanned noticeText;
            final PaymentInfo hourlyRate = booking.getHourlyRate();
            if (hourlyRate != null && booking.hasFlexibleHours())
            {
                final float minimumHours = booking.getMinimumHours();
                final float maximumHours = booking.getHours();
                final String minimumHoursFormatted = TextUtils.formatHours(minimumHours);
                final String maximumHoursFormatted = TextUtils.formatHours(maximumHours);
                final String currencySymbol = hourlyRate.getCurrencySymbol();
                final String minimumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                        (int) (hourlyRate.getAmount() * minimumHours), currencySymbol);
                final String maximumPaymentFormatted = CurrencyUtils.formatPriceWithCents(
                        (int) (hourlyRate.getAmount() * maximumHours), currencySymbol);
                final String startDateFormatted = DateTimeUtils.formatDetailedDate(booking.getStartDate());
                final String endDateFormatted = DateTimeUtils.formatDetailedDate(booking.getEndDate());
                noticeText = Html.fromHtml(getResources()
                        .getString(R.string.full_details_and_more_available_on_date_flex,
                                minimumHoursFormatted, maximumHoursFormatted,
                                startDateFormatted, endDateFormatted,
                                minimumPaymentFormatted, minimumHoursFormatted,
                                maximumPaymentFormatted, maximumHoursFormatted
                        ));
            }
            else
            {
                noticeText = Html.fromHtml(getResources()
                        .getString(R.string.full_details_and_more_available_on_date,
                                DateTimeUtils.formatDetailedDate(booking.getRevealDate())));
            }
            mRevealNotice.setText(noticeText);
            mRevealNotice.setVisibility(View.VISIBLE);
            hasContent = true;
        }

        //Show description field regardless of claim status if the booking is not for cleaning (e.g. furniture assembly)
        if (!isHomeCleaning && booking.getDescription() != null && !booking.getDescription().isEmpty())
        {
            BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
            sectionView.init(getContext().getString(R.string.description),
                    R.drawable.ic_details_notes, Lists.newArrayList(booking.getDescription()));

            hasContent = true;
        }

        //Special section for "Supplies" extras (UK only)
        List<Booking.ExtraInfoWrapper> cleaningSuppliesExtrasInfo =
                booking.getExtrasInfoByMachineName(Booking.ExtraInfo.TYPE_CLEANING_SUPPLIES);
        if (booking.isUK() && cleaningSuppliesExtrasInfo.size() > 0)
        {
            List<String> entries = new ArrayList<>();
            entries.add(getContext().getString(R.string.bring_cleaning_supplies));

            BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
            sectionView.init(getContext().getString(R.string.supplies), R.drawable.ic_details_supplies, entries);

            hasContent = true;
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
                BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
                sectionView.init(getContext().getString(R.string.extras), R.drawable.ic_details_extras, entries);

                hasContent = true;
            }
        }

        if (shouldShowFullDetails)
        {
            List<Booking.BookingInstructionGroup> bookingInstructionGroups = booking.getBookingInstructionGroups();
            if (bookingInstructionGroups != null && bookingInstructionGroups.size() > 0)
            {
                mPreferencesGroup = null;
                for (Booking.BookingInstructionGroup group : bookingInstructionGroups)
                {
                    if (Booking.BookingInstructionGroup.GROUP_PREFERENCES.equals(group.getGroup()))
                    {
                        mPreferencesGroup = group;
                    }
                    else
                    {
                        BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
                        sectionView.init(group.getLabel(), GROUP_ICONS.get(group.getGroup()),
                                group.getInstructions());
                    }
                }
                if (mPreferencesGroup != null && booking.isCheckedIn())
                {
                    SharedPreferences checklistPreferences =
                            getContext().getSharedPreferences(PrefsType.CHECKLIST, Context.MODE_PRIVATE);
                    List<Booking.BookingInstruction> checklist;
                    if (checklistPreferences.getString(booking.getId(), "").isEmpty())
                    {
                        checklist = mPreferencesGroup.getInstructions();
                    }
                    else
                    {
                        Booking.BookingInstruction[] checklistArray = GSON.fromJson(
                                checklistPreferences.getString(booking.getId(), ""),
                                Booking.BookingInstruction[].class);
                        checklist = Arrays.asList(checklistArray);
                        mPreferencesGroup.setInstructions(checklist);
                    }
                    CustomerRequestsView customerRequestsView = new CustomerRequestsView(getContext(),
                            mPreferencesGroup.getLabel(), GROUP_ICONS.get(mPreferencesGroup.getGroup()),
                            checklist);
                    if (!booking.isCheckedIn())
                    {
                        customerRequestsView.setEnabled(false);
                    }
                    mInstructionsLayout.addView(customerRequestsView);
                }
                else if (mPreferencesGroup != null)
                {
                    BookingDetailsJobInstructionsSectionView sectionView = addSection(mInstructionsLayout);
                    sectionView.init(mPreferencesGroup.getLabel(), GROUP_ICONS.get(mPreferencesGroup.getGroup()),
                            mPreferencesGroup.getInstructions());
                }

                hasContent = true;
            }
        }

        setVisibility(hasContent ? VISIBLE : GONE);
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_booking_details_job_instructions, this);
        ButterKnife.bind(this);
        setLayoutParams(UIUtils.MATCH_PARENT_PARAMS);
    }

    private BookingDetailsJobInstructionsSectionView addSection(LinearLayout instructionsLayout)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_section, instructionsLayout);
        return (BookingDetailsJobInstructionsSectionView) instructionsLayout.getChildAt(instructionsLayout.getChildCount() - 1);
    }

    public List<Booking.BookingInstruction> getInstructions()
    {
        if (mPreferencesGroup != null)
        {
            return mPreferencesGroup.getInstructions();
        }
        else
        {
            return null;
        }
    }
}
