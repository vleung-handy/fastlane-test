package com.handy.portal.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentBillBlockerDialogFragment extends InjectedDialogFragment //TODO: consolidate some of this logic with other dialog fragments
{

    @Bind(R.id.payments_bill_blocker_update_now_button)
    protected Button updateNowButton;

    @Bind(R.id.payments_bill_blocker_later_button)
    protected Button laterButton;

    public static final String FRAGMENT_TAG = "fragment_dialog_payment_bill_blocker";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_payment_bill_blocker, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        updateNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.SELECT_PAYMENT_METHOD, null, TransitionStyle.REFRESH_TAB));
                PaymentBillBlockerDialogFragment.this.dismiss();
            }
        });

        laterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PaymentBillBlockerDialogFragment.this.dismiss();
            }
        });
    }
}
