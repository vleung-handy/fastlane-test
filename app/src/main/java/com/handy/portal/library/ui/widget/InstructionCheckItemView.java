package com.handy.portal.library.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionCheckItemView extends FrameLayout
{
    @Bind(R.id.main_layout)
    ViewGroup mMainLayout;
    @Bind(R.id.checklist_item_check_box)
    CheckBox mCheckBox;
    @Bind(R.id.checklist_item_title_description)
    TextView mTitleDescriptionTextView;

    // cannot user selector for alpha because it doesn't work for pre lollipop
    private final static float ALPHA_ENABLED = 1f;
    private final static float ALPHA_DISABLED = .2f;


    public InstructionCheckItemView(final Context context)
    {
        super(context);
        init();
    }

    public InstructionCheckItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public InstructionCheckItemView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InstructionCheckItemView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void refreshDisplay(@NonNull final Booking.BookingInstructionUpdateRequest instruction)
    {
        mCheckBox.setChecked(instruction.isInstructionCompleted());
        setBackgroundAsNeeded();

        if (instruction.getTitle() != null && !instruction.getTitle().isEmpty())
        {
            mTitleDescriptionTextView.setText(Html.fromHtml("<b>" + instruction.getTitle() + "</b> " + instruction.getDescription()), TextView.BufferType.SPANNABLE);
        }
        else
        {
            mTitleDescriptionTextView.setText(instruction.getDescription());
        }

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                mCheckBox.toggle();
                instruction.setInstructionCompleted(mCheckBox.isChecked());
                setBackgroundAsNeeded();
            }
        });
    }

    @Override
    public void setEnabled(final boolean enabled)
    {
        super.setEnabled(enabled);
        if (enabled)
        {
            mCheckBox.setAlpha(ALPHA_ENABLED);
        }
        else
        {
            mCheckBox.setAlpha(ALPHA_DISABLED);
        }
    }

    public boolean isChecked()
    {
        return mCheckBox.isChecked();
    }

    private void init()
    {
        inflate(getContext(), R.layout.list_item_checkbox, this);
        ButterKnife.bind(this);
    }

    private void setBackgroundAsNeeded()
    {
        if (isChecked())
        {
            mMainLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_green_bg_white));
        }
        else
        {
            mMainLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_gray_bg_white));
        }
    }
}
