package com.handy.portal.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handy.portal.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CheckListItemView extends FrameLayout
{
    @Bind(R.id.checklist_item_check_box)
    CheckBox mCheckBox;
    @Bind(R.id.checklist_item_title)
    TextView mTitleTextView;
    @Bind(R.id.checklist_item_description)
    TextView mDescriptionTextView;


    public CheckListItemView(final Context context)
    {
        super(context);
        init();
    }

    public CheckListItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CheckListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CheckListItemView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void refreshDisplay(boolean checked, @Nullable String title, @NonNull String description)
    {
        mCheckBox.setChecked(checked);
        if (title != null && !title.isEmpty())
        {
            mTitleTextView.setVisibility(VISIBLE);
            mTitleTextView.setText(title);
        }
        else
        {
            mTitleTextView.setVisibility(GONE);
        }
        mDescriptionTextView.setText(description);
    }

    public void setChecked(boolean checked)
    {
        mCheckBox.setChecked(checked);
    }

    public boolean isChecked()
    {
        return mCheckBox.isChecked();
    }

    private void init()
    {
        inflate(getContext(), R.layout.list_item_checkbox, this);
        ButterKnife.bind(this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v)
            {
                mCheckBox.toggle();
            }
        });
    }

}
