package com.handy.portal.proavailability.fragment;

import com.handy.portal.R;
import com.handy.portal.core.ui.fragment.ActionBarFragment;

public class WeeklyAvailableHoursFragment extends ActionBarFragment
{
    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.available_hours, true);
    }
}
