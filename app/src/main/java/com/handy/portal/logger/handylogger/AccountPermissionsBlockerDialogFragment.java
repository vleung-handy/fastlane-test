package com.handy.portal.logger.handylogger;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.PermissionsBlockerDialogFragment;

public class AccountPermissionsBlockerDialogFragment extends PermissionsBlockerDialogFragment
{
    public static final String TAG = AccountPermissionsBlockerDialogFragment.class.getName();

    @Override
    public int getTitleResourceId()
    {
        return R.string.change_account_permissions_dialog_title;
    }

    @Override
    public int getMessageResourceId()
    {
        return R.string.change_account_permissions_dialog_message;
    }
}
