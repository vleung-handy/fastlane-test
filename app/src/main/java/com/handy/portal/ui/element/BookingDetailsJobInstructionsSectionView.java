package com.handy.portal.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handy.portal.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by cdavis on 5/8/15.
 */
public class BookingDetailsJobInstructionsSectionView extends RelativeLayout
{
    @InjectView(R.id.booking_details_job_instructions_section_title_text)
    protected TextView sectionTitleText;

    @InjectView(R.id.booking_details_job_instructions_section_title_icon)
    protected ImageView sectionIcon;

    @InjectView(R.id.booking_details_job_instructions_section_entries_layout)
    protected LinearLayout entriesLayout;

    public BookingDetailsJobInstructionsSectionView(final Context context)
    {
        super(context);
    }

    public BookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public BookingDetailsJobInstructionsSectionView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);
    }
    public void init(String sectionTitle, int sectionIconId, List<String> entries, boolean bulleted)
    {
        ButterKnife.inject(this);

        sectionTitleText.setText(sectionTitle);
        sectionIcon.setImageResource(sectionIconId);

        for(int i = 0; i < entries.size(); i++)
        {
            LayoutInflater.from(getContext()).inflate(R.layout.element_booking_details_job_instructions_entry, entriesLayout);
            BookingDetailsJobInstructionsSectionEntryView entryView = ((BookingDetailsJobInstructionsSectionEntryView) (entriesLayout.getChildAt(i)));
            entryView.init(entries.get(i), bulleted);
        }
    }

}
