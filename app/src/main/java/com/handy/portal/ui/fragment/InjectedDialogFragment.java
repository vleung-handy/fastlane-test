package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.BookingManager;
import com.handy.portal.core.UserManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.ui.widget.ProgressDialog;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class InjectedDialogFragment extends DialogFragment {
    protected boolean allowCallbacks;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    @Inject BookingManager bookingManager;
    @Inject UserManager userManager;
    @Inject Mixpanel mixpanel;
    @Inject DataManager dataManager;
    @Inject DataManagerErrorHandler dataManagerErrorHandler;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication)getActivity().getApplication()).inject(this);

        toast = Toast.makeText(getActivity(), null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setDelay(400);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
    }

    @Override
    public final void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        allowCallbacks = false;
    }

    protected void disableInputs() {}

    protected void enableInputs() {}
}
