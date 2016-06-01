package com.handy.portal.preactivation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.SlideUpDialogFragment;

import butterknife.OnClick;

public class DeclineSuppliesDialogFragment extends SlideUpDialogFragment
{
    public static DeclineSuppliesDialogFragment newInstance()
    {
        return new DeclineSuppliesDialogFragment();
    }

    @OnClick(R.id.decline_supplies_button)
    void onDeclineSupplies()
    {
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        dismiss();
    }

    @Override
    protected View inflateContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_decline_supplies, container, false);
    }
}
