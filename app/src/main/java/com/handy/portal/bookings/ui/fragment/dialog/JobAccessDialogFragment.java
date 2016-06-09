package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.library.ui.fragment.dialog.PopupDialogFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * parent of the early access and job access unlocked dialog fragments
 *
 * has a header image, a title, a subtitle and an action button
 */
public abstract class JobAccessDialogFragment extends PopupDialogFragment
{
    @Bind(R.id.fragment_dialog_job_access_title)
    TextView mTitle;
    @Bind(R.id.fragment_dialog_job_access_description)
    TextView mDescription;
    @Bind(R.id.fragment_dialog_job_access_header_image)
    ImageView mHeaderImage;
    @Bind(R.id.fragment_dialog_job_access_action_button)
    Button mActionButton;

    protected abstract BookingsWrapper.PriorityAccessInfo getPriorityAccessFromBundle();
    protected abstract int getHeaderImageResourceId();
    protected abstract int getActionButtonTextResourceId();

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_dialog_job_access, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        BookingsWrapper.PriorityAccessInfo priorityAccessInfo = getPriorityAccessFromBundle();
        if(priorityAccessInfo == null)
        {
            Crashlytics.logException(new Exception("Priority access is null in early access trial dialog fragment"));
            dismiss();
            return;
        }

        updateActionButtonText();
        updateHeaderImage();
        updateDisplayWithPriorityAccess(priorityAccessInfo);
    }

    private void updateActionButtonText()
    {
        mActionButton.setText(getActionButtonTextResourceId());
    }

    private void updateHeaderImage()
    {
        mHeaderImage.setImageDrawable(ContextCompat.getDrawable(getContext(), getHeaderImageResourceId()));
    }

    private void updateDisplayWithPriorityAccess(@NonNull BookingsWrapper.PriorityAccessInfo priorityAccessInfo)
    {
        mTitle.setText(priorityAccessInfo.getMessageTitle());
        mDescription.setText(priorityAccessInfo.getMessageDescription());
    }

    @OnClick(R.id.fragment_dialog_job_access_action_button)
    public void onActionButtonClicked()
    {
        dismiss();
    }
}
