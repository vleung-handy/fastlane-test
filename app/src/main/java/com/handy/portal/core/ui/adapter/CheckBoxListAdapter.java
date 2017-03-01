package com.handy.portal.core.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import com.handy.portal.core.ui.widget.HandyCheckBox;

public class CheckBoxListAdapter extends ArrayAdapter<CheckBoxListAdapter.CheckBoxListItem> {
    private final CheckBoxListItem[] mItems;

    public CheckBoxListAdapter(final Context context, final CheckBoxListItem[] items) {
        super(context, -1, items);
        mItems = items;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        HandyCheckBox view;
        if (convertView == null) {
            view = new HandyCheckBox(getContext());
        }
        else {
            view = (HandyCheckBox) convertView;
            // The following line is required to prevent unexpected calls to the existing listener
            // attached to the view. Given that setChecked() is called to initialize the state of
            // the checkbox, this is necessary.
            view.setOnCheckedChangeListener(null);
        }
        view.setLabel(mItems[position].getLabel());
        view.setChecked(mItems[position].isChecked());
        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                mItems[position].setChecked(isChecked);
            }
        });
        return view;
    }

    public CheckBoxListItem[] getItems() {
        return mItems;
    }

    public static class CheckBoxListItem {
        private String mLabel;
        private String mId;
        private boolean mChecked;

        public CheckBoxListItem(final String label, final String id, final boolean checked) {
            mLabel = label;
            mId = id;
            mChecked = checked;
        }

        public void setChecked(final boolean checked) {
            mChecked = checked;
        }

        public boolean isChecked() {
            return mChecked;
        }

        public String getLabel() {
            return mLabel;
        }

        public String getId() {
            return mId;
        }
    }
}
