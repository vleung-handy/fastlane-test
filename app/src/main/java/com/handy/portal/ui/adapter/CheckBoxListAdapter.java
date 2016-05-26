package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.handy.portal.ui.widget.HandyCheckBox;

public class CheckBoxListAdapter extends ArrayAdapter<CheckBoxListAdapter.CheckBoxListItem>
{
    private final CheckBoxListItem[] mItems;

    public CheckBoxListAdapter(final Context context, final CheckBoxListItem[] items)
    {
        super(context, -1, items);
        mItems = items;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        HandyCheckBox view;
        if (convertView == null)
        {
            view = new HandyCheckBox(getContext());
        }
        else
        {
            view = (HandyCheckBox) convertView;
            view.setOnCheckedChangeListener(null);
        }
        view.setLabel(mItems[position].getLabel());
        view.setChecked(mItems[position].isChecked());
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked)
            {
                mItems[position].setChecked(isChecked);
            }
        });
        return view;
    }

    public CheckBoxListItem[] getItems()
    {
        return mItems;
    }

    public static class CheckBoxListItem
    {
        private String mLabel;
        private boolean mChecked;

        public CheckBoxListItem(final String label, final boolean checked)
        {
            mLabel = label;
            mChecked = checked;
        }

        public void setChecked(final boolean checked)
        {
            mChecked = checked;
        }

        public boolean isChecked()
        {
            return mChecked;
        }

        public String getLabel()
        {
            return mLabel;
        }
    }
}
