package com.handy.portal.location.ui;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.PermissionsBlockerDialogFragment;

public class LocationPermissionsBlockerDialogFragment extends PermissionsBlockerDialogFragment
{
    public static final String FRAGMENT_TAG = LocationPermissionsBlockerDialogFragment.class.getName();

    @Override
    public int getTitleResourceId()
    {
        return R.string.change_location_permissions_dialog_title;
    }

    @Override
    public int getMessageResourceId()
    {
        return R.string.change_location_permissions_dialog_message;
    }
}
